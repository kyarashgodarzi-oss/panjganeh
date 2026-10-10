package com.panjganeh.game.data.repository

internal object VipExpiryPolicy {
    private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L

    fun calculateExpiry(
        now: Long,
        currentExpiry: Long,
        currentVipActive: Boolean,
        durationDays: Int
    ): Long {
        require(durationDays > 0) { "VIP duration must be positive" }
        val extensionBase = if (currentVipActive && currentExpiry > now) currentExpiry else now
        return extensionBase + durationDays.toLong() * MILLIS_PER_DAY
    }
}
