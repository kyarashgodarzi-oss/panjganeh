package com.panjganeh.game.challenges.dice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panjganeh.game.ai.AiProfile
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DiceBattleUiState(
    val roundNumber: Int = 1,
    val userRoundWins: Int = 0,
    val aiRoundWins: Int = 0,
    val userDiceValue: Int = 1,
    val aiDiceValue: Int = 1,
    val isRolling: Boolean = false,
    val roundResultMessage: String = "برای پرتاب تاس، دکمه زیر را لمس کنید!",
    val isGameOver: Boolean = false,
    val isWin: Boolean = false,
    val earnedCoins: Int = 0,
    val aiProfile: AiProfile? = null
)

class DiceBattleViewModel(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiceBattleUiState())
    val uiState = _uiState.asStateFlow()

    private var aiProfile: AiProfile? = null

    fun startGame(difficulty: String) {
        aiProfile = AiProfile.createRandom(difficulty)
        _uiState.update {
            DiceBattleUiState(
                roundNumber = 1,
                userRoundWins = 0,
                aiRoundWins = 0,
                userDiceValue = 1,
                aiDiceValue = 1,
                isRolling = false,
                roundResultMessage = "دست اول — آماده پرتاب تاس!",
                isGameOver = false,
                aiProfile = aiProfile
            )
        }
    }

    fun rollDice() {
        val state = _uiState.value
        if (state.isRolling || state.isGameOver) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRolling = true,
                    roundResultMessage = "تاس‌ها در حال چرخش و پرتاب..."
                )
            }

            // افکت چرخش ۳ ثانیه‌ای با تغییر مقادیر میانی
            val rollIterations = 15
            for (i in 0 until rollIterations) {
                _uiState.update {
                    it.copy(
                        userDiceValue = Random.nextInt(1, 7),
                        aiDiceValue = Random.nextInt(1, 7)
                    )
                }
                delay(120L)
            }

            // نتیجه نهایی بر اساس سطح AI و شانس
            var userFinal = Random.nextInt(1, 7)
            var aiFinal = Random.nextInt(1, 7)

            // تاثیر سطح هوش مصنوعی روی پرتاب تاس
            val aiSuccess = aiProfile?.willSucceed() == true
            if (aiSuccess && aiFinal <= userFinal && aiFinal < 6) {
                // با درصد احتمال موفقیت سطح خود، گاهی تاس بهتری می‌آورد
                aiFinal = (userFinal + Random.nextInt(0, 2)).coerceIn(1, 6)
            }

            val roundMessage: String
            var newUWins = state.userRoundWins
            var newAiWins = state.aiRoundWins

            if (userFinal > aiFinal) {
                newUWins++
                roundMessage = "🎉 شما برنده این دست شدید! ($userFinal در برابر $aiFinal)"
            } else if (aiFinal > userFinal) {
                newAiWins++
                roundMessage = " حریف برنده این دست شد! ($aiFinal در برابر $userFinal)"
            } else {
                roundMessage = "🤝 مساوی شد! ($userFinal برابر $aiFinal) — مجدد تاس بریزید"
            }

            val isOver = newUWins >= 3 || newAiWins >= 3
            val isUserChampion = newUWins >= 3
            val coins = if (isUserChampion) 100 else 30

            _uiState.update {
                it.copy(
                    isRolling = false,
                    userDiceValue = userFinal,
                    aiDiceValue = aiFinal,
                    userRoundWins = newUWins,
                    aiRoundWins = newAiWins,
                    roundNumber = if (!isOver && userFinal != aiFinal) it.roundNumber + 1 else it.roundNumber,
                    roundResultMessage = roundMessage,
                    isGameOver = isOver,
                    isWin = isUserChampion,
                    earnedCoins = coins
                )
            }

            if (isOver) {
                userRepository.recordMatchResult(
                    isWin = isUserChampion,
                    xpReward = if (isUserChampion) 120 else 40,
                    coinReward = coins
                )
                gameRepository.recordMatch(
                    gameType = "dice",
                    userScore = newUWins,
                    aiScore = newAiWins,
                    isWin = isUserChampion,
                    aiDifficulty = aiProfile?.level?.titleFa ?: "متوسط",
                    rewardCoins = coins
                )
            }
        }
    }
}
