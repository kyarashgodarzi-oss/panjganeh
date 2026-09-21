package com.panjganeh.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * موجودیت مشخصات و آمار کاربر در بازی آرنا کلش ۲
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "جنگجوی آرنا",
    val coins: Int = 500,
    val tickets: Int = 5,
    val level: Int = 1,
    val xp: Int = 0,
    val avatarId: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val title: String = "تازه وارد"
)

/**
 * موجودیت وضعیت اشتراک ویژه (VIP)
 */
@Entity(tableName = "vip_state")
data class VipStateEntity(
    @PrimaryKey val id: Int = 1,
    val isVip: Boolean = false,
    val expireTimestamp: Long = 0L,
    val sku: String = "",
    val purchaseToken: String = ""
)

/**
 * موجودیت جوایز روزانه ورود به بازی
 */
@Entity(tableName = "daily_rewards")
data class RewardItemEntity(
    @PrimaryKey val day: Int,
    val rewardType: String, // "COINS", "TICKETS", "AVATAR"
    val amount: Int,
    val isClaimed: Boolean = false,
    val claimTimestamp: Long = 0L
)

/**
 * موجودیت چالش‌های پنج‌گانه بازی
 */
@Entity(tableName = "challenges")
data class ChallengeItemEntity(
    @PrimaryKey val challengeId: String, // "word", "memory", "dice", "rps", "sentence"
    val nameFa: String,
    val descriptionFa: String,
    val icon: String,
    val highscore: Int = 0,
    val stars: Int = 0
)

/**
 * موجودیت تاریخچه مسابقات با هوش مصنوعی
 */
@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val gameType: String, // "word", "memory", "dice", "rps", "sentence"
    val userScore: Int,
    val aiScore: Int,
    val isWin: Boolean,
    val aiDifficulty: String, // "آسان", "متوسط", "سخت", "افسانه‌ای"
    val timestamp: Long = System.currentTimeMillis(),
    val rewardCoins: Int = 0
)

/**
 * موجودیت تنظیمات عمومی بازی
 */
@Entity(tableName = "game_settings")
data class GameSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val aiDifficulty: String = "متوسط", // آسان, متوسط, سخت, افسانه‌ای
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val language: String = "fa"
)
