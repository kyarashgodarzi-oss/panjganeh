package com.panjganeh.game.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class VipExpiryPolicyTest {
    @Test
    fun renewalExtendsFromExistingExpiryWhenVipIsStillActive() {
        val now = 1_000_000L
        val currentExpiry = now + 5L * DAY

        val result = VipExpiryPolicy.calculateExpiry(
            now = now,
            currentExpiry = currentExpiry,
            currentVipActive = true,
            durationDays = 30
        )

        assertEquals(now + 35L * DAY, result)
    }

    @Test
    fun expiredVipStartsAgainFromCurrentTime() {
        val now = 1_000_000L

        val result = VipExpiryPolicy.calculateExpiry(
            now = now,
            currentExpiry = now - DAY,
            currentVipActive = true,
            durationDays = 30
        )

        assertEquals(now + 30L * DAY, result)
    }

    @Test
    fun inactiveVipStartsFromCurrentTimeEvenIfStoredExpiryIsFuture() {
        val now = 1_000_000L

        val result = VipExpiryPolicy.calculateExpiry(
            now = now,
            currentExpiry = now + 5L * DAY,
            currentVipActive = false,
            durationDays = 365
        )

        assertEquals(now + 365L * DAY, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNonPositiveDuration() {
        VipExpiryPolicy.calculateExpiry(
            now = 1_000_000L,
            currentExpiry = 0L,
            currentVipActive = false,
            durationDays = 0
        )
    }

    private companion object {
        const val DAY = 24L * 60L * 60L * 1000L
    }
}
