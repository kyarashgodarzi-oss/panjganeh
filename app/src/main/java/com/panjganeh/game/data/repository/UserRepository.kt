package com.panjganeh.game.data.repository

import com.panjganeh.game.data.local.AppDatabase
import com.panjganeh.game.data.local.entity.RewardItemEntity
import com.panjganeh.game.data.local.entity.UserProfileEntity
import com.panjganeh.game.data.local.entity.VipStateEntity
import com.panjganeh.game.data.local.entity.ProcessedPurchaseEntity
import androidx.room.withTransaction
import com.panjganeh.game.billing.BazaarConfig
import kotlinx.coroutines.flow.Flow

class UserRepository(private val database: AppDatabase) {

    val userProfile: Flow<UserProfileEntity?> = database.userDao().getUserProfile()
    val vipState: Flow<VipStateEntity?> = database.vipDao().getVipState()
    val dailyRewards: Flow<List<RewardItemEntity>> = database.rewardDao().getAllRewards()

    suspend fun getUserProfileOnce(): UserProfileEntity {
        return database.userDao().getUserProfileOnce() ?: UserProfileEntity().also {
            database.userDao().insertOrUpdateUser(it)
        }
    }

    suspend fun getVipStateOnce(): VipStateEntity {
        return database.vipDao().getVipStateOnce() ?: VipStateEntity().also {
            database.vipDao().setVipState(it)
        }
    }

    suspend fun addCoins(amount: Int) {
        database.userDao().addCoins(amount)
    }

    suspend fun deductTickets(amount: Int = 1): Boolean {
        val user = getUserProfileOnce()
        if (user.tickets >= amount) {
            database.userDao().addTickets(-amount)
            return true
        }
        return false
    }

    suspend fun addTickets(amount: Int) {
        database.userDao().addTickets(amount)
    }

    suspend fun updateUser(user: UserProfileEntity) {
        database.userDao().insertOrUpdateUser(user)
    }

    suspend fun recordMatchResult(isWin: Boolean, xpReward: Int, coinReward: Int) {
        if (isWin) {
            database.userDao().recordWin(xpReward)
        } else {
            database.userDao().recordLoss(xpReward)
        }
        if (coinReward > 0) {
            database.userDao().addCoins(coinReward)
        }
        // ارتقای سطح خودکار
        val user = getUserProfileOnce()
        val nextLevelXp = user.level * 200
        if (user.xp >= nextLevelXp) {
            val newLevel = user.level + 1
            val newTitle = when (newLevel) {
                in 1..2 -> "تازه وارد"
                in 3..5 -> "مبارز شجاع"
                in 6..9 -> "استاد تاکتیک"
                in 10..15 -> "فرمانده آرنا"
                else -> "افسانه زنده"
            }
            database.userDao().insertOrUpdateUser(
                user.copy(
                    level = newLevel,
                    xp = user.xp - nextLevelXp,
                    title = newTitle,
                    coins = user.coins + (newLevel * 100)
                )
            )
        }
    }

    suspend fun setVip(isVip: Boolean, durationDays: Int, sku: String, token: String) {
        val now = System.currentTimeMillis()
        val currentVip = if (isVip) getVipStateOnce() else null
        val expireTime = if (isVip) {
            VipExpiryPolicy.calculateExpiry(
                now = now,
                currentExpiry = currentVip?.expireTimestamp ?: 0L,
                currentVipActive = currentVip?.isVip == true,
                durationDays = durationDays
            )
        } else {
            0L
        }
        val vip = VipStateEntity(
            id = 1,
            isVip = isVip,
            expireTimestamp = expireTime,
            sku = sku,
            purchaseToken = token
        )
        database.vipDao().setVipState(vip)
    }


    /**
     * اعتبارسنجی و تحویل پاداش در یک تراکنش؛ توکن تکراری هرگز دوباره پاداش نمی‌گیرد.
     * @return true فقط وقتی این توکن برای اولین بار پردازش و پاداش تحویل شد.
     */
    suspend fun deliverPurchaseOnce(sku: String, purchaseToken: String): Boolean {
        if (purchaseToken.isBlank()) return false
        if (sku !in BazaarConfig.ALL_PRODUCTS.map { it.sku }) return false

        return database.withTransaction {
            val inserted = database.purchaseDao().insertIfAbsent(
                ProcessedPurchaseEntity(purchaseToken = purchaseToken, sku = sku)
            )
            if (inserted == -1L) return@withTransaction false

            when (sku) {
                BazaarConfig.SKU_VIP_MONTHLY -> {
                    setVip(true, 30, sku, purchaseToken)
                    database.userDao().addCoins(500)
                }
                BazaarConfig.SKU_VIP_YEARLY -> {
                    setVip(true, 365, sku, purchaseToken)
                    database.userDao().addCoins(5000)
                    database.userDao().addTickets(10)
                }
                BazaarConfig.SKU_COINS_1000 -> database.userDao().addCoins(1000)
                // The store description promises a 1,000-coin bonus with this 5,000-coin pack.
                BazaarConfig.SKU_COINS_5000 -> database.userDao().addCoins(6000)
                BazaarConfig.SKU_TICKETS_10 -> database.userDao().addTickets(10)
                else -> return@withTransaction false
            }
            true
        }
    }

    suspend fun claimDailyReward(day: Int): Boolean {
        val rewards = database.rewardDao()
        val user = getUserProfileOnce()
        // find reward for day
        database.rewardDao().claimReward(day, System.currentTimeMillis())
        when (day) {
            1 -> addCoins(100)
            2 -> addCoins(200)
            3 -> addTickets(2)
            4 -> addCoins(350)
            5 -> addTickets(3)
            6 -> addCoins(500)
            7 -> {
                // Unlock avatar
                database.userDao().insertOrUpdateUser(user.copy(avatarId = (user.avatarId + 1) % 6))
                addCoins(1000)
            }
        }
        return true
    }
}
