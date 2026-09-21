package com.panjganeh.game.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import com.panjganeh.game.BuildConfig
import com.panjganeh.game.billing.security.SecurityHelper
import com.panjganeh.game.data.repository.UserRepository
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.ConnectionState
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import ir.cafebazaar.poolakey.entity.PurchaseInfo
import ir.cafebazaar.poolakey.request.PurchaseRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class PurchaseResult {
    data class Success(val sku: String, val messageFa: String) : PurchaseResult()
    data class Error(val messageFa: String) : PurchaseResult()
    object Cancelled : PurchaseResult()
}

/**
 * مدیریت خرید درون‌برنامه‌ای کافه‌بازار با Poolakey
 */
class BazaarBillingManager(
    private val context: Context,
    private val userRepository: UserRepository
) {
    companion object {
        private const val TAG = "BazaarBilling"
    }

    private val _purchaseEvents = MutableSharedFlow<PurchaseResult>()
    val purchaseEvents = _purchaseEvents.asSharedFlow()

    // کلید RSA از BuildConfig (تزریق شده از local.properties)
    private val rsaKey: String = try {
        BuildConfig.BAZAAR_PUBLIC_KEY.trim()
    } catch (e: Throwable) {
        Log.e(TAG, "BAZAAR_PUBLIC_KEY not found in BuildConfig")
        ""
    }

    private val paymentConfig: PaymentConfiguration by lazy {
        PaymentConfiguration(
            localSecurityCheck = if (rsaKey.isNotBlank()) {
                SecurityCheck.Enable(rsaPublicKey = rsaKey)
            } else {
                SecurityCheck.Disable
            },
            poolakeyConfig = PaymentConfiguration.PoolakeyConfig(
                isAutoConnect = false
            )
        )
    }

    private val payment: Payment by lazy {
        Payment(context = context, config = paymentConfig)
    }

    private var connection: Connection? = null

    /**
     * اتصال به سرویس پرداخت کافه‌بازار
     */
    fun connect(onConnected: (() -> Unit)? = null, onFailed: ((Throwable) -> Unit)? = null) {
        if (connection != null) {
            onConnected?.invoke()
            return
        }

        connection = payment.connect {
            connectionSucceed {
                Log.d(TAG, "Connected to Bazaar billing service")
                onConnected?.invoke()
            }
            connectionFailed { throwable ->
                Log.e(TAG, "Connection failed: ${throwable.message}")
                onFailed?.invoke(throwable)
            }
            disconnected {
                Log.d(TAG, "Disconnected from Bazaar billing service")
            }
        }
    }

    /**
     * شروع فرایند خرید
     */
    fun purchaseProduct(activity: Activity, sku: String) {
        if (activity !is ComponentActivity) {
            emitError("Activity must be ComponentActivity")
            return
        }

        // اطمینان از اتصال
        if (connection == null) {
            connect(
                onConnected = { doPurchase(activity, sku) },
                onFailed = { emitError("اتصال به کافه‌بازار برقرار نشد") }
            )
        } else {
            doPurchase(activity, sku)
        }
    }

    private fun doPurchase(activity: ComponentActivity, sku: String) {
        val request = PurchaseRequest(
            productId = sku,
            payload = ""
        )

        val registry: ActivityResultRegistry = activity.activityResultRegistry

        payment.purchaseProduct(registry, request) {
            purchaseFlowBegan {
                Log.d(TAG, "Purchase flow began for $sku")
            }

            failedToBeginFlow { throwable ->
                Log.e(TAG, "Failed to begin flow: ${throwable.message}")
                emitError("خطا در شروع تراکنش: ${throwable.message}")
            }

            purchaseSucceed { purchaseInfo ->
                Log.d(TAG, "Purchase succeeded: ${purchaseInfo.originalJson}")
                handlePurchaseSuccess(purchaseInfo)
            }

            purchaseFailed { throwable ->
                Log.e(TAG, "Purchase failed: ${throwable.message}")
                emitError("خرید ناموفق: ${throwable.message}")
            }

            purchaseCanceled {
                Log.d(TAG, "Purchase canceled by user")
                emitCancelled()
            }
        }
    }

    private fun handlePurchaseSuccess(purchaseInfo: PurchaseInfo) {
        val originalJson = purchaseInfo.originalJson
        val signature = purchaseInfo.signature

        // تایید امضا
        val isValid = if (rsaKey.isNotBlank()) {
            SecurityHelper.verifyPurchase(rsaKey, originalJson, signature)
        } else {
            Log.w(TAG, "RSA key is blank, skipping signature verification")
            true
        }

        if (!isValid) {
            emitError("امضای دیجیتال خرید نامعتبر است")
            return
        }

        // پردازش محصول
        val sku = purchaseInfo.productId
        val token = purchaseInfo.purchaseToken

        deliverProduct(sku, token)
    }

    private fun deliverProduct(sku: String, token: String) {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
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

                _purchaseEvents.emit(
                    PurchaseResult.Success(sku, "خرید با موفقیت انجام شد!")
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error delivering product: ${e.message}")
                _purchaseEvents.emit(PurchaseResult.Error("خطا در تحویل محصول"))
            }
        }
    }

    private fun emitError(message: String) {
        kotlinx.coroutines.GlobalScope.launch {
            _purchaseEvents.emit(PurchaseResult.Error(message))
        }
    }

    private fun emitCancelled() {
        kotlinx.coroutines.GlobalScope.launch {
            _purchaseEvents.emit(PurchaseResult.Cancelled)
        }
    }

    fun disconnect() {
        connection?.disconnect()
        connection = null
    }
}
