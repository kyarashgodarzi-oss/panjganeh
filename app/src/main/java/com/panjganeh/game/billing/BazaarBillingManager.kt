package com.panjganeh.game.billing

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.panjganeh.game.BuildConfig
import com.panjganeh.game.billing.security.SecurityHelper
import com.panjganeh.game.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean

sealed class PurchaseResult {
    data class Success(val sku: String, val messageFa: String) : PurchaseResult()
    data class Error(val messageFa: String) : PurchaseResult()
    object Cancelled : PurchaseResult()
}

/**
 * مدیریت خرید درون‌برنامه‌ای کافه‌بازار با AIDL دستی
 */
class BazaarBillingManager(
    private val context: Context,
    private val userRepository: UserRepository
) {

    companion object {
        private const val TAG = "BazaarBilling"
        private const val BAZAAR_PACKAGE = "com.farsitel.bazaar"
        private const val BAZAAR_BILLING_ACTION = "ir.cafebazaar.pardakht.InAppBillingService.BIND"
        private const val BILLING_API_VERSION = 3
        private const val ITEM_TYPE_INAPP = "inapp"

        // Response keys
        private const val RESPONSE_CODE = "RESPONSE_CODE"
        private const val BUY_INTENT = "BUY_INTENT"
        private const val INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA"
        private const val INAPP_DATA_SIGNATURE = "INAPP_DATA_SIGNATURE"

        // Response codes
        const val BILLING_RESPONSE_RESULT_OK = 0
        const val BILLING_RESPONSE_RESULT_USER_CANCELED = 1
        const val BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3
        const val BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4
        const val BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5
        const val BILLING_RESPONSE_RESULT_ERROR = 6
        const val BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var billingService: IBinder? = null
    private val isPurchaseInProgress = AtomicBoolean(false)

    private val _purchaseEvents = MutableSharedFlow<PurchaseResult>()
    val purchaseEvents = _purchaseEvents.asSharedFlow()

    private val rsaKey: String = try {
        BuildConfig.BAZAAR_PUBLIC_KEY.trim()
    } catch (e: Throwable) {
        Log.e(TAG, "BAZAAR_PUBLIC_KEY not found in BuildConfig")
        ""
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Connected to Bazaar billing service")
            billingService = service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.w(TAG, "Disconnected from Bazaar billing service")
            billingService = null
        }
    }

    fun connect(onConnected: (() -> Unit)? = null, onFailed: ((Throwable) -> Unit)? = null) {
        if (billingService != null) {
            onConnected?.invoke()
            return
        }

        val serviceIntent = Intent(BAZAAR_BILLING_ACTION).apply {
            setPackage(BAZAAR_PACKAGE)
        }

        try {
            val bound = context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            if (!bound) {
                Log.e(TAG, "Could not bind to Bazaar billing service")
                onFailed?.invoke(Exception("کافه بازار نصب نیست یا اتصال برقرار نشد"))
            } else {
                onConnected?.invoke()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error binding: ${e.message}")
            onFailed?.invoke(e)
        }
    }

    fun launchPurchaseFlow(
        activityLauncher: ActivityResultLauncher<IntentSenderRequest>,
        productId: String,
        developerPayload: String = ""
    ) {
        if (!isPurchaseInProgress.compareAndSet(false, true)) {
            Log.w(TAG, "Purchase already in progress")
            return
        }

        val service = billingService
        if (service == null) {
            isPurchaseInProgress.set(false)
            emitError("سرویس کافه بازار در دسترس نیست")
            return
        }

        scope.launch {
            try {
                val data = android.os.Parcel.obtain()
                val reply = android.os.Parcel.obtain()
                val buyIntentBundle: Bundle?
                try {
                    data.writeInterfaceToken("com.android.vending.billing.IInAppBillingService")
                    data.writeInt(BILLING_API_VERSION)
                    data.writeString(context.packageName)
                    data.writeString(productId)
                    data.writeString(ITEM_TYPE_INAPP)
                    data.writeString(developerPayload)

                    service.transact(IBinder.FIRST_CALL_TRANSACTION + 2, data, reply, 0)
                    reply.readException()
                    buyIntentBundle = if (reply.readInt() != 0) {
                        Bundle.CREATOR.createFromParcel(reply)
                    } else null
                } finally {
                    data.recycle()
                    reply.recycle()
                }

                if (buyIntentBundle == null) {
                    isPurchaseInProgress.set(false)
                    emitError("پاسخی از کافه بازار دریافت نشد")
                    return@launch
                }

                val responseCode = buyIntentBundle.getInt(RESPONSE_CODE, BILLING_RESPONSE_RESULT_ERROR)
                if (responseCode != BILLING_RESPONSE_RESULT_OK) {
                    isPurchaseInProgress.set(false)
                    emitError(mapResponseCode(responseCode))
                    return@launch
                }

                val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    buyIntentBundle.getParcelable(BUY_INTENT, PendingIntent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    buyIntentBundle.getParcelable(BUY_INTENT)
                }

                if (pendingIntent == null) {
                    isPurchaseInProgress.set(false)
                    emitError("درخواست پرداخت معتبر صادر نشد")
                    return@launch
                }

                val request = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                activityLauncher.launch(request)
            } catch (e: RemoteException) {
                isPurchaseInProgress.set(false)
                Log.e(TAG, "RemoteException: ${e.message}")
                emitError("خطای ارتباط با بازار: ${e.message}")
            } catch (e: Exception) {
                isPurchaseInProgress.set(false)
                Log.e(TAG, "Exception: ${e.message}")
                emitError("خطای غیرمنتظره: ${e.message}")
            }
        }
    }

    fun handleActivityResult(result: ActivityResult) {
        isPurchaseInProgress.set(false)

        if (result.resultCode == Activity.RESULT_CANCELED) {
            emitCancelled()
            return
        }

        val dataIntent = result.data
        if (result.resultCode != Activity.RESULT_OK || dataIntent == null) {
            emitError("عملیات خرید تکمیل نشد")
            return
        }

        val responseCode = dataIntent.getIntExtra(RESPONSE_CODE, BILLING_RESPONSE_RESULT_OK)
        val purchaseData = dataIntent.getStringExtra(INAPP_PURCHASE_DATA)
        val dataSignature = dataIntent.getStringExtra(INAPP_DATA_SIGNATURE)

        if (responseCode != BILLING_RESPONSE_RESULT_OK || purchaseData.isNullOrBlank() || dataSignature.isNullOrBlank()) {
            emitError(mapResponseCode(responseCode))
            return
        }

        verifyAndDeliver(purchaseData, dataSignature)
    }

    private fun verifyAndDeliver(purchaseData: String, dataSignature: String) {
        scope.launch {
            if (rsaKey.isBlank()) {
                Log.e(TAG, "RSA key is blank, cannot verify")
                emitError("کلید عمومی بازار تنظیم نشده است")
                return@launch
            }

            val isValid = SecurityHelper.verifyPurchase(rsaKey, purchaseData, dataSignature)
            if (!isValid) {
                Log.e(TAG, "Signature verification failed")
                emitError("امضای دیجیتال خرید نامعتبر است")
                return@launch
            }

            try {
                val json = JSONObject(purchaseData)
                val productId = json.getString("productId")
                val purchaseState = json.getInt("purchaseState")

                if (purchaseState != 0) {
                    emitError("وضعیت خرید نامعتبر است")
                    return@launch
                }

                deliverProduct(productId)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing purchase: ${e.message}")
                emitError("خطا در پردازش اطلاعات خرید")
            }
        }
    }

    private suspend fun deliverProduct(sku: String) {
        try {
            val token = "bazaar_" + System.currentTimeMillis()
            when (sku) {
                BazaarConfig.SKU_VIP_MONTHLY -> {
                    userRepository.setVip(isVip = true, durationDays = 30, sku = sku, token = token)
                    userRepository.addCoins(500)
                }
                BazaarConfig.SKU_VIP_YEARLY -> {
                    userRepository.setVip(isVip = true, durationDays = 365, sku = sku, token = token)
                    userRepository.addCoins(5000)
                    userRepository.addTickets(10)
                }
                BazaarConfig.SKU_COINS_1000 -> userRepository.addCoins(1000)
                BazaarConfig.SKU_COINS_5000 -> userRepository.addCoins(5000)
                BazaarConfig.SKU_TICKETS_10 -> userRepository.addTickets(10)
            }

            _purchaseEvents.emit(PurchaseResult.Success(sku, "خرید با موفقیت انجام شد!"))
        } catch (e: Exception) {
            Log.e(TAG, "Error delivering product: ${e.message}")
            _purchaseEvents.emit(PurchaseResult.Error("خطا در تحویل محصول"))
        }
    }

    private fun emitError(message: String) {
        scope.launch {
            _purchaseEvents.emit(PurchaseResult.Error(message))
        }
    }

    private fun emitCancelled() {
        scope.launch {
            _purchaseEvents.emit(PurchaseResult.Cancelled)
        }
    }

    private fun mapResponseCode(code: Int): String {
        return when (code) {
            BILLING_RESPONSE_RESULT_USER_CANCELED -> "پرداخت توسط کاربر لغو شد"
            BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE -> "سرویس پرداخت کافه بازار در دسترس نیست"
            BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE -> "محصول در کافه بازار موجود نیست"
            BILLING_RESPONSE_RESULT_DEVELOPER_ERROR -> "خطای توسعه‌دهنده"
            BILLING_RESPONSE_RESULT_ERROR -> "خطای ناشناخته در پرداخت"
            BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED -> "قبلاً این محصول را خریداری کرده‌اید"
            else -> "خطای نامشخص (کد: $code)"
        }
    }

    fun disconnect() {
        try {
            context.unbindService(serviceConnection)
        } catch (e: Exception) {
            Log.w(TAG, "Error unbinding: ${e.message}")
        }
        billingService = null
    }
}
