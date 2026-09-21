package com.panjganeh.game.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.panjganeh.game.data.local.dao.ChallengeDao
import com.panjganeh.game.data.local.dao.MatchHistoryDao
import com.panjganeh.game.data.local.dao.RewardDao
import com.panjganeh.game.data.local.dao.SettingsDao
import com.panjganeh.game.data.local.dao.UserDao
import com.panjganeh.game.data.local.dao.VipDao
import com.panjganeh.game.data.local.entity.ChallengeItemEntity
import com.panjganeh.game.data.local.entity.GameSettingsEntity
import com.panjganeh.game.data.local.entity.MatchHistoryEntity
import com.panjganeh.game.data.local.entity.RewardItemEntity
import com.panjganeh.game.data.local.entity.UserProfileEntity
import com.panjganeh.game.data.local.entity.VipStateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        VipStateEntity::class,
        RewardItemEntity::class,
        ChallengeItemEntity::class,
        MatchHistoryEntity::class,
        GameSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun vipDao(): VipDao
    abstract fun rewardDao(): RewardDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun matchHistoryDao(): MatchHistoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "panjganeh_database.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    initDefaults(database)
                }
            }
        }
    }
}

suspend fun initDefaults(database: AppDatabase) {
    // ایجاد پروفایل اولیه کاربر در صورت نبود
    if (database.userDao().getUserProfileOnce() == null) {
        database.userDao().insertOrUpdateUser(
            UserProfileEntity(
                id = 1,
                username = "قهرمان پنجگانه",
                coins = 500,
                tickets = 5,
                level = 1,
                xp = 0,
                avatarId = 0,
                wins = 0,
                losses = 0,
                title = "تازه وارد"
            )
        )
    }

    if (database.vipDao().getVipStateOnce() == null) {
        database.vipDao().setVipState(
            VipStateEntity(
                id = 1,
                isVip = false,
                expireTimestamp = 0L,
                sku = "",
                purchaseToken = ""
            )
        )
    }

    if (database.settingsDao().getSettingsOnce() == null) {
        database.settingsDao().updateSettings(
            GameSettingsEntity(
                id = 1,
                aiDifficulty = "متوسط",
                soundEnabled = true,
                vibrationEnabled = true,
                musicEnabled = true,
                language = "fa"
            )
        )
    }

    // مقداردهی اولیه ۵ چالش آرنا
    val defaultChallenges = listOf(
        ChallengeItemEntity(
            challengeId = "word",
            nameFa = "نبرد کلمات",
            descriptionFa = "حدس جای خالی، کلمات درهم و حل جدول واژگان با حریف AI",
            icon = "spellcheck",
            highscore = 0,
            stars = 0
        ),
        ChallengeItemEntity(
            challengeId = "memory",
            nameFa = "حافظه کارت‌ها",
            descriptionFa = "به خاطر سپردن و یافتن جفت کارت‌های مشابه در جدول ۴×۴",
            icon = "style",
            highscore = 0,
            stars = 0
        ),
        ChallengeItemEntity(
            challengeId = "dice",
            nameFa = "نبرد تاس",
            descriptionFa = "مسابقه تاس‌اندازی مهیج (بهترین از ۵ دست) با شانس و استراتژی",
            icon = "casino",
            highscore = 0,
            stars = 0
        ),
        ChallengeItemEntity(
            challengeId = "rps",
            nameFa = "سنگ کاغذ قیچی",
            descriptionFa = "نبرد کلاسیک سنگ، کاغذ و قیچی با شمارش معکوس و خواندن دست حریف",
            icon = "sports_kabaddi",
            highscore = 0,
            stars = 0
        ),
        ChallengeItemEntity(
            challengeId = "sentence",
            nameFa = "ساخت جملات",
            descriptionFa = "مرتب‌سازی کاشی‌های واژگان درهم برای تشکیل جملات فارسی",
            icon = "text_fields",
            highscore = 0,
            stars = 0
        )
    )
    database.challengeDao().insertChallenges(defaultChallenges)

    // جوایز ورود روزانه ۷ روزه
    val dailyRewards = listOf(
        RewardItemEntity(day = 1, rewardType = "COINS", amount = 100, isClaimed = false),
        RewardItemEntity(day = 2, rewardType = "COINS", amount = 200, isClaimed = false),
        RewardItemEntity(day = 3, rewardType = "TICKETS", amount = 2, isClaimed = false),
        RewardItemEntity(day = 4, rewardType = "COINS", amount = 350, isClaimed = false),
        RewardItemEntity(day = 5, rewardType = "TICKETS", amount = 3, isClaimed = false),
        RewardItemEntity(day = 6, rewardType = "COINS", amount = 500, isClaimed = false),
        RewardItemEntity(day = 7, rewardType = "AVATAR", amount = 1, isClaimed = false)
    )
    database.rewardDao().insertRewards(dailyRewards)
}
