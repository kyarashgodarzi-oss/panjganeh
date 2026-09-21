package com.panjganeh.game.billing

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.panjganeh.game.BuildConfig
import com.panjganeh.game.billing.security.SecurityHelper
import com.panjganeh.game.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class PurchaseResult {
    data class Success(val sku: String, val messageFa: String) : PurchaseResult()
    data class Error(val messageFa: String) : PurchaseResult()
    object Cancelled : PurchaseResult()
}

class BazaarBillingManager(
    private val context: Context,
    private val userRepository: UserRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _purchaseEvents = MutableSharedFlow<PurchaseResult>()
    val purchaseEvents = _purchaseEvents.asSharedFlow()

    private val rsaKey: String = try {
        BuildConfig.BAZAAR_RSA_KEY
    } catch (e: Throwable) {
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3DemoArenaClashBazaarKey"
    }

    /**
     * شروع فرایند خرید محصول از بازار
     */
    fun purchaseProduct(activity: Activity, sku: String) {
        scope.launch {
            try {
                // شبیه‌سازی داده خرید معتبر و تایید با SecurityHelper
                val purchaseToken = "token_" + UUID.randomUUID().toString().take(8)
                val purchaseData = """{"productId":"$sku","purchaseToken":"$purchaseToken","purchaseTime":${System.currentTimeMillis()}}"""
                val fakeSignature = "mockSignatureBase64=="

                val isValid = SecurityHelper.verifyPurchase(rsaKey, purchaseData, fakeSignature)
                if (isValid) {
                    deliverProduct(sku, purchaseToken)
                    _purchaseEvents.emit(PurchaseResult.Success(sku, "خرید با موفقیت انجام شد و به حسابتان اضافه گردید!"))
                } else {
                    _purchaseEvents.emit(PurchaseResult.Error("تایید امضای پرداخت با خطا مواجه شد."))
                }
            } catch (e: Exception) {
                _purchaseEvents.emit(PurchaseResult.Error("خطا در انجام تراکنش: ${e.localizedMessage}"))
            }
        }
    }

    private suspend fun deliverProduct(sku: String, token: String) {
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
            BazaarConfig.SKU_COINS_1000 -> {
                userRepository.addCoins(1000)
            }
            BazaarConfig.SKU_COINS_5000 -> {
                userRepository.addCoins(5000)
            }
            BazaarConfig.SKU_TICKETS_10 -> {
                userRepository.addTickets(10)
            }
        }
    }
}
