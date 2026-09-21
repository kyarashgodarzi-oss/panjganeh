package com.panjganeh.game.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.panjganeh.game.data.local.entity.ChallengeItemEntity
import com.panjganeh.game.data.local.entity.GameSettingsEntity
import com.panjganeh.game.data.local.entity.MatchHistoryEntity
import com.panjganeh.game.data.local.entity.RewardItemEntity
import com.panjganeh.game.data.local.entity.UserProfileEntity
import com.panjganeh.game.data.local.entity.VipStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserProfileEntity)

    @Query("UPDATE user_profile SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE user_profile SET tickets = tickets + :amount WHERE id = 1")
    suspend fun addTickets(amount: Int)

    @Query("UPDATE user_profile SET wins = wins + 1, xp = xp + :xpReward WHERE id = 1")
    suspend fun recordWin(xpReward: Int)

    @Query("UPDATE user_profile SET losses = losses + 1, xp = xp + :xpReward WHERE id = 1")
    suspend fun recordLoss(xpReward: Int)
}

@Dao
interface VipDao {
    @Query("SELECT * FROM vip_state WHERE id = 1")
    fun getVipState(): Flow<VipStateEntity?>

    @Query("SELECT * FROM vip_state WHERE id = 1")
    suspend fun getVipStateOnce(): VipStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setVipState(vipState: VipStateEntity)
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM daily_rewards ORDER BY day ASC")
    fun getAllRewards(): Flow<List<RewardItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewards(rewards: List<RewardItemEntity>)

    @Query("UPDATE daily_rewards SET isClaimed = 1, claimTimestamp = :timestamp WHERE day = :day")
    suspend fun claimReward(day: Int, timestamp: Long)

    @Query("UPDATE daily_rewards SET isClaimed = 0")
    suspend fun resetWeeklyRewards()
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeItemEntity>>

    @Query("SELECT * FROM challenges WHERE challengeId = :id")
    suspend fun getChallenge(id: String): ChallengeItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeItemEntity>)

    @Query("UPDATE challenges SET highscore = :score, stars = :stars WHERE challengeId = :id AND :score > highscore")
    suspend fun updateHighscore(id: String, score: Int, stars: Int)
}

@Dao
interface MatchHistoryDao {
    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 30")
    fun getMatchHistory(): Flow<List<MatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchHistoryEntity)

    @Query("SELECT COUNT(*) FROM match_history")
    suspend fun getMatchCount(): Int
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM game_settings WHERE id = 1")
    fun getSettings(): Flow<GameSettingsEntity?>

    @Query("SELECT * FROM game_settings WHERE id = 1")
    suspend fun getSettingsOnce(): GameSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: GameSettingsEntity)
}
