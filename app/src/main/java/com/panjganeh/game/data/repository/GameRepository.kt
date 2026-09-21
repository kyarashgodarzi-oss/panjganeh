package com.panjganeh.game.data.repository

import android.content.Context
import com.panjganeh.game.data.local.AppDatabase
import com.panjganeh.game.data.local.entity.ChallengeItemEntity
import com.panjganeh.game.data.local.entity.GameSettingsEntity
import com.panjganeh.game.data.local.entity.MatchHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

data class WordItem(
    val word: String,
    val hint: String,
    val scrambled: String,
    val missing: String
)

data class SentenceItem(
    val id: Int,
    val text: String,
    val words: List<String>
)

class GameRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    val allChallenges: Flow<List<ChallengeItemEntity>> = database.challengeDao().getAllChallenges()
    val matchHistory: Flow<List<MatchHistoryEntity>> = database.matchHistoryDao().getMatchHistory()
    val settings: Flow<GameSettingsEntity?> = database.settingsDao().getSettings()

    private var cachedWords: List<WordItem> = emptyList()
    private var cachedSentences: List<SentenceItem> = emptyList()

    suspend fun getSettingsOnce(): GameSettingsEntity {
        return database.settingsDao().getSettingsOnce() ?: GameSettingsEntity().also {
            database.settingsDao().updateSettings(it)
        }
    }

    suspend fun updateSettings(settings: GameSettingsEntity) {
        database.settingsDao().updateSettings(settings)
    }

    suspend fun recordMatch(
        gameType: String,
        userScore: Int,
        aiScore: Int,
        isWin: Boolean,
        aiDifficulty: String,
        rewardCoins: Int
    ) {
        val match = MatchHistoryEntity(
            gameType = gameType,
            userScore = userScore,
            aiScore = aiScore,
            isWin = isWin,
            aiDifficulty = aiDifficulty,
            timestamp = System.currentTimeMillis(),
            rewardCoins = rewardCoins
        )
        database.matchHistoryDao().insertMatch(match)

        // بروزرسانی بالاترین امتیاز چالش
        val stars = if (userScore > aiScore) 3 else if (userScore == aiScore) 2 else 1
        database.challengeDao().updateHighscore(gameType, userScore, stars)
    }

    suspend fun getMatchCount(): Int = withContext(Dispatchers.IO) {
        database.matchHistoryDao().getMatchCount()
    }

    suspend fun loadWords(): List<WordItem> = withContext(Dispatchers.IO) {
        if (cachedWords.isNotEmpty()) return@withContext cachedWords
        try {
            val jsonString = context.assets.open("words.json").bufferedReader().use { it.readText() }
            val array = JSONArray(jsonString)
            val list = mutableListOf<WordItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WordItem(
                        word = obj.getString("word"),
                        hint = obj.getString("hint"),
                        scrambled = obj.getString("scrambled"),
                        missing = obj.getString("missing")
                    )
                )
            }
            cachedWords = list
            list
        } catch (e: Exception) {
            e.printStackTrace()
            // پشتیبان در صورت خطا
            listOf(
                WordItem("کلمات", "واژه‌ها", "لتاکم", "ک_ا_مات"),
                WordItem("ایران", "سرزمین کهن", "رانای", "ا_ر_ن"),
                WordItem("پیروزی", "غلبه بر حریف", "زیوریپ", "پ_ر_زی")
            )
        }
    }

    suspend fun loadSentences(): List<SentenceItem> = withContext(Dispatchers.IO) {
        if (cachedSentences.isNotEmpty()) return@withContext cachedSentences
        try {
            val jsonString = context.assets.open("sentences.json").bufferedReader().use { it.readText() }
            val array = JSONArray(jsonString)
            val list = mutableListOf<SentenceItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val wordsArray = obj.getJSONArray("words")
                val words = mutableListOf<String>()
                for (j in 0 until wordsArray.length()) {
                    words.add(wordsArray.getString(j))
                }
                list.add(
                    SentenceItem(
                        id = obj.getInt("id"),
                        text = obj.getString("text"),
                        words = words
                    )
                )
            }
            cachedSentences = list
            list
        } catch (e: Exception) {
            e.printStackTrace()
            listOf(
                SentenceItem(1, "ایران سرزمینی کهن و پر از قهرمانان است", listOf("ایران", "سرزمینی", "کهن", "و", "پر", "از", "قهرمانان", "است"))
            )
        }
    }
}
