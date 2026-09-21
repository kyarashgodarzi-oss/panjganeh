package com.panjganeh.game.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusBannerType
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * مدیریت تبلیغات تپسل پلاس
 * از SDK واقعی تپسل استفاده می‌کند
 */
class TapsellManager(
    private val context: Context
) {

    companion object {
        private const val TAG = "TapsellManager"

        // Zone ID های اختصاصی اپ پنج‌گانه
        const val REWARDED_ZONE_ID = "6ab13be1e237e15c69fba773"
        const val INTERSTITIAL_ZONE_ID = "6ab0e1f572fad1012032e232"
        const val BANNER_ZONE_ID = "6ab0e1d305017a44fcf2c433"
    }

    private var isInitialized = false
    private val isInitializing = AtomicBoolean(false)
    private val isRequestInProgress = AtomicBoolean(false)
    private val isShowInProgress = AtomicBoolean(false)

    // شمارنده نبردها برای نمایش Interstitial
    private var battlesSinceLastInterstitial = 0

    // رویدادهای تبلیغات برای UI
    private val _adEvents = MutableSharedFlow<String>()
    val adEvents = _adEvents.asSharedFlow()

    // کلید تپسل از BuildConfig خوانده می‌شود (از local.properties)
    val tapsellAppKey: String
        get() = try {
            val key = com.panjganeh.game.BuildConfig.TAPSELL_KEY.trim()
            if (key == "DEFAULT_TAPSELL_KEY" || key == "YOUR_TAPSELL_KEY") "" else key
        } catch (_: Exception) {
            ""
        }

    /**
     * مقداردهی اولیه SDK تپسل
     */
    fun initialize(onInitialized: ((Boolean) -> Unit)? = null) {
        if (isInitialized) {
            onInitialized?.invoke(true)
            return
        }

        val appKey = tapsellAppKey
        if (appKey.isBlank()) {
            Log.w(TAG, "Tapsell appKey is not configured. Initialization skipped.")
            onInitialized?.invoke(false)
            return
        }

        if (!isInitializing.compareAndSet(false, true)) {
            Log.d(TAG, "Tapsell initialization already in progress.")
            return
        }

        TapsellPlus.initialize(context, appKey, object : TapsellPlusInitListener {
            override fun onInitializeSuccess(adNetworks: AdNetworks?) {
                isInitialized = true
                isInitializing.set(false)
                Log.d(TAG, "TapsellPlus initialized successfully.")
                onInitialized?.invoke(true)
            }

            override fun onInitializeFailed(adNetworks: AdNetworks?, adNetworkError: AdNetworkError?) {
                isInitialized = false
                isInitializing.set(false)
                val errMsg = adNetworkError?.errorMessage ?: "خطای ناشناخته در مقداردهی تپسل"
                Log.e(TAG, "TapsellPlus initialization failed: $errMsg")
                onInitialized?.invoke(false)
            }
        })
    }

    /**
     * نمایش ویدیوی جایزه‌دار (Rewarded Video)
     */
    fun showRewardedVideo(
        activity: Activity,
        zoneId: String = REWARDED_ZONE_ID,
        rewardCoins: Int = 150,
        onRewarded: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isInitialized) {
            initialize { success ->
                if (success) {
                    showRewardedVideo(activity, zoneId, rewardCoins, onRewarded, onError)
                } else {
                    onError("کلید تپسل در برنامه پیکربندی نشده است.")
                }
            }
            return
        }

        if (!isRequestInProgress.compareAndSet(false, true)) {
            Log.w(TAG, "Ad request already in progress. Skipping.")
            return
        }

        TapsellPlus.requestRewardedVideoAd(activity, zoneId, object : AdRequestCallback() {
            override fun response(tapsellPlusAdModel: TapsellPlusAdModel) {
                isRequestInProgress.set(false)
                val responseId = tapsellPlusAdModel.responseId
                Log.d(TAG, "Rewarded ad ready: $responseId")
                showRewardedAdNow(activity, responseId, rewardCoins, onRewarded, onError)
            }

            override fun error(errorMessage: String?) {
                isRequestInProgress.set(false)
                val msg = errorMessage ?: "خطا در دریافت ویدیوی جایزه‌دار"
                Log.e(TAG, "Error requesting rewarded ad: $msg")
                onError(msg)
            }
        })
    }

    private fun showRewardedAdNow(
        activity: Activity,
        responseId: String,
        rewardCoins: Int,
        onRewarded: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isShowInProgress.compareAndSet(false, true)) {
            Log.w(TAG, "Ad is already showing.")
            return
        }

        val rewardDelivered = AtomicBoolean(false)

        TapsellPlus.showRewardedVideoAd(activity, responseId, object : AdShowListener() {
            override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel) {
                Log.d(TAG, "Ad opened: ${tapsellPlusAdModel.responseId}")
            }

            override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel) {
                isShowInProgress.set(false)
                Log.d(TAG, "Ad closed: ${tapsellPlusAdModel.responseId}")
            }

            override fun onRewarded(tapsellPlusAdModel: TapsellPlusAdModel) {
                Log.d(TAG, "Ad rewarded: ${tapsellPlusAdModel.responseId}")
                if (rewardDelivered.compareAndSet(false, true)) {
                    onRewarded(rewardCoins)
                }
            }

            override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel) {
                isShowInProgress.set(false)
                val errorMsg = tapsellPlusErrorModel.errorMessage ?: "خطا در نمایش ویدیو"
                Log.e(TAG, "Ad playback error: $errorMsg")
                onError(errorMsg)
            }
        })
    }

    /**
     * نمایش تبلیغ آنی (Interstitial Video)
     */
    fun showInterstitial(
        activity: Activity,
        zoneId: String = INTERSTITIAL_ZONE_ID,
        onClosed: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (!isInitialized) {
            initialize { success ->
                if (success) {
                    showInterstitial(activity, zoneId, onClosed, onError)
                } else {
                    onError?.invoke("کلید تپسل پیکربندی نشده است.")
                    onClosed?.invoke()
                }
            }
            return
        }

        TapsellPlus.requestInterstitialAd(activity, zoneId, object : AdRequestCallback() {
            override fun response(tapsellPlusAdModel: TapsellPlusAdModel) {
                val responseId = tapsellPlusAdModel.responseId
                TapsellPlus.showInterstitialAd(activity, responseId, object : AdShowListener() {
                    override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel) {
                        Log.d(TAG, "Interstitial opened: ${tapsellPlusAdModel.responseId}")
                    }

                    override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel) {
                        Log.d(TAG, "Interstitial closed: ${tapsellPlusAdModel.responseId}")
                        onClosed?.invoke()
                    }

                    override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel) {
                        val err = tapsellPlusErrorModel.errorMessage ?: "خطا در نمایش تبلیغ آنی"
                        Log.e(TAG, "Interstitial show error: $err")
                        onError?.invoke(err)
                        onClosed?.invoke()
                    }
                })
            }

            override fun error(errorMessage: String?) {
                val err = errorMessage ?: "خطا در دریافت تبلیغ آنی"
                Log.e(TAG, "Interstitial request error: $err")
                onError?.invoke(err)
                onClosed?.invoke()
            }
        })
    }

    /**
     * مدیریت نمایش Interstitial بعد از پایان نبرد
     * - اگه کاربر باخت: بلافاصله نمایش بده
     * - اگه برد: هر ۲ بازی یک بار
     */
    fun onBattleFinished(
        activity: Activity,
        isWin: Boolean,
        onFinished: () -> Unit
    ) {
        battlesSinceLastInterstitial++

        val shouldShowAd = !isWin || battlesSinceLastInterstitial >= 2

        if (shouldShowAd) {
            battlesSinceLastInterstitial = 0
            Log.d(TAG, "Showing interstitial after battle (isWin=$isWin)")
            showInterstitial(
                activity = activity,
                onClosed = onFinished,
                onError = { onFinished() }
            )
        } else {
            onFinished()
        }
    }

    /**
     * ریست شمارنده نبردها (مثلاً وقتی کاربر VIP می‌خره)
     */
    fun resetBattleCounter() {
        battlesSinceLastInterstitial = 0
    }

    /**
     * نمایش بنر استاندارد
     */
    fun showBanner(
        activity: Activity,
        container: ViewGroup,
        zoneId: String = BANNER_ZONE_ID,
        onShown: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (!isInitialized) {
            initialize { success ->
                if (success) {
                    showBanner(activity, container, zoneId, onShown, onError)
                } else {
                    onError?.invoke("کلید تپسل پیکربندی نشده است.")
                }
            }
            return
        }

        TapsellPlus.requestStandardBannerAd(
            activity,
            zoneId,
            TapsellPlusBannerType.BANNER_320x50,
            object : AdRequestCallback() {
                override fun response(tapsellPlusAdModel: TapsellPlusAdModel) {
                    val responseId = tapsellPlusAdModel.responseId
                    TapsellPlus.showStandardBannerAd(
                        activity,
                        responseId,
                        container,
                        object : AdShowListener() {
                            override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel) {
                                Log.d(TAG, "Banner opened: $responseId")
                                onShown?.invoke()
                            }

                            override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel) {
                                val err = tapsellPlusErrorModel.errorMessage ?: "خطا در نمایش بنر"
                                Log.e(TAG, "Banner show error: $err")
                                onError?.invoke(err)
                            }
                        }
                    )
                }

                override fun error(errorMessage: String?) {
                    val err = errorMessage ?: "خطا در دریافت بنر"
                    Log.e(TAG, "Banner request error: $err")
                    onError?.invoke(err)
                }
            }
        )
    }

    fun destroyBanner(activity: Activity, container: ViewGroup, responseId: String = "") {
        try {
            TapsellPlus.destroyStandardBanner(activity, responseId, container)
        } catch (e: Exception) {
            Log.w(TAG, "Error destroying banner: ${e.message}")
        }
    }
}
