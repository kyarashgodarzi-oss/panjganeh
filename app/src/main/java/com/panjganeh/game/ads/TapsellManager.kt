package com.panjganeh.game.ads

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.panjganeh.game.BuildConfig
import com.panjganeh.game.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

object TapsellZoneIds {
    const val REWARDED_VIDEO_ZONE = "arena_rewarded_video_zone_1"
    const val INTERSTITIAL_VIDEO_ZONE = "arena_interstitial_video_zone_2"
    const val STANDARD_BANNER_ZONE = "arena_banner_zone_3"
}

class TapsellManager(
    private val context: Context,
    private val userRepository: UserRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var battlesSinceLastAd = 0

    private val _adEvents = MutableSharedFlow<String>()
    val adEvents = _adEvents.asSharedFlow()

    private val tapsellKey = try {
        BuildConfig.TAPSELL_KEY
    } catch (e: Throwable) {
        "demo_tapsell_app_key_arena_clash"
    }

    /**
     * بررسی اینکه آیا کاربر اشتراک VIP دارد یا خیر.
     * کاربران VIP تبلیغ نمی‌بینند.
     */
    private suspend fun isUserVip(): Boolean {
        val vip = userRepository.vipState.firstOrNull()
        return vip?.isVip == true && (vip.expireTimestamp == 0L || vip.expireTimestamp > System.currentTimeMillis())
    }

    /**
     * نمایش ویدیوی جایزه‌دار برای دریافت سکه رایگان
     */
    fun showRewardedVideo(activity: Activity, onRewardEarned: (rewardCoins: Int) -> Unit) {
        scope.launch {
            if (isUserVip()) {
                // کاربر VIP نیازی به دیدن ویدیو ندارد و پاداش سریع دریافت می‌کند
                onRewardEarned(300)
                _adEvents.emit("پاداش VIP دریافت شد! (+۳۰۰ سکه طلا)")
                return@launch
            }

            // شبیه‌سازی پخش ویدیوی جایزه‌دار تپسل
            val rewardAmount = 150
            userRepository.addCoins(rewardAmount)
            onRewardEarned(rewardAmount)
            _adEvents.emit("ویدیوی تبلیغاتی به پایان رسید. +۱۵۰ سکه جایزه گرفتید!")
        }
    }

    /**
     * بررسی و نمایش تبلیغ بین‌صفحه‌ای بعد از هر ۲-۳ بازی
     */
    fun onBattleFinished(activity: Activity, onAdFinished: () -> Unit = {}) {
        scope.launch {
            if (isUserVip()) {
                onAdFinished()
                return@launch
            }

            battlesSinceLastAd++
            if (battlesSinceLastAd >= 2) {
                battlesSinceLastAd = 0
                // نمایش تبلیغ بین‌صفحه‌ای
                _adEvents.emit("تبلیغ بین‌صفحه‌ای تپسل پلاس")
            }
            onAdFinished()
        }
    }
}
