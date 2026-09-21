package com.panjganeh.game.ai

import kotlin.random.Random

/**
 * سطح سختی هوش مصنوعی
 */
enum class AiDifficultyLevel(
    val titleFa: String,
    val accuracyPercentage: Int,
    val speedIntervalMs: Long,
    val descriptionFa: String
) {
    EASY("آسان", 50, 4200L, "مناسب تازه کاران و دستگرمی"),
    MEDIUM("متوسط", 70, 3200L, "حریف باهوش با چالش متعادل"),
    HARD("سخت", 90, 2200L, "بسیار سریع و کم اشتباه"),
    LEGENDARY("افسانه‌ای", 95, 1500L, "فوق سریع با تسلط تقریباً کامل")
}

/**
 * مدل مشخصات حریف هوش مصنوعی در نبرد
 */
data class AiProfile(
    val name: String,
    val title: String,
    val avatarId: Int,
    val level: AiDifficultyLevel
) {
    companion object {
        private val AI_NAMES = listOf(
            Pair("سایه شب", "شکارچی کلمات"),
            Pair("تایتان هوشمند", "غول پردازش"),
            Pair("آریا بات", "قهرمان محاسبات"),
            Pair("ققنوس آتشین", "استاد حافظه"),
            Pair("سایبر سامورایی", "تیغه بدون خطا"),
            Pair("سیروس ربات", "پیشگوی آرنا"),
            Pair("شبح دیجیتال", "ناشناس بی‌رحم")
        )

        fun createRandom(difficulty: String): AiProfile {
            val level = when (difficulty) {
                "آسان" -> AiDifficultyLevel.EASY
                "سخت" -> AiDifficultyLevel.HARD
                "افسانه‌ای" -> AiDifficultyLevel.LEGENDARY
                else -> AiDifficultyLevel.MEDIUM
            }
            val (name, title) = AI_NAMES.random()
            val avatarId = Random.nextInt(0, 5)
            return AiProfile(name = name, title = title, avatarId = avatarId, level = level)
        }
    }

    /**
     * بررسی موفقیت اقدام هوش مصنوعی بر اساس درصد دقت سطح
     */
    fun willSucceed(): Boolean {
        val roll = Random.nextInt(1, 101)
        return roll <= level.accuracyPercentage
    }

    /**
     * مدت زمان تصمیم‌گیری هوش مصنوعی با تلرانس تصادفی
     */
    fun getActionDelay(): Long {
        val jitter = Random.nextLong(-300L, 400L)
        return (level.speedIntervalMs + jitter).coerceAtLeast(800L)
    }
}
