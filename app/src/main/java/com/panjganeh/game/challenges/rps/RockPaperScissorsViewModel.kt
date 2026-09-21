package com.panjganeh.game.challenges.rps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panjganeh.game.ai.AiProfile
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class RpsChoice(val titleFa: String, val emoji: String) {
    ROCK("سنگ", "✊"),
    PAPER("کاغذ", "✋"),
    SCISSORS("قیچی", "✌️")
}

data class RpsUiState(
    val roundNumber: Int = 1,
    val userWins: Int = 0,
    val aiWins: Int = 0,
    val countdownSeconds: Int = 3,
    val isCountingDown: Boolean = true,
    val userChoice: RpsChoice? = null,
    val aiChoice: RpsChoice? = null,
    val isRevealed: Boolean = false,
    val roundStatusText: String = "یک گزینه انتخاب کنید...",
    val isGameOver: Boolean = false,
    val isWin: Boolean = false,
    val earnedCoins: Int = 0,
    val aiProfile: AiProfile? = null
)

class RockPaperScissorsViewModel(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RpsUiState())
    val uiState = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var aiProfile: AiProfile? = null

    fun startGame(difficulty: String) {
        aiProfile = AiProfile.createRandom(difficulty)
        _uiState.update {
            RpsUiState(
                roundNumber = 1,
                userWins = 0,
                aiWins = 0,
                countdownSeconds = 3,
                isCountingDown = true,
                userChoice = null,
                aiChoice = null,
                isRevealed = false,
                roundStatusText = "شمارش معکوس نبرد... دست خود را آماده کنید!",
                isGameOver = false,
                aiProfile = aiProfile
            )
        }
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _uiState.update { it.copy(isCountingDown = true, isRevealed = false, userChoice = null, aiChoice = null) }
            for (sec in 3 downTo 1) {
                _uiState.update { it.copy(countdownSeconds = sec, roundStatusText = "آماده‌باش: $sec") }
                delay(800L)
            }
            _uiState.update {
                it.copy(
                    isCountingDown = false,
                    roundStatusText = "انتخاب کنید: سنگ، کاغذ یا قیچی!"
                )
            }
        }
    }

    fun onUserSelect(choice: RpsChoice) {
        val state = _uiState.value
        if (state.isCountingDown || state.isRevealed || state.isGameOver) return

        viewModelScope.launch {
            _uiState.update { it.copy(userChoice = choice, roundStatusText = "در حال خواندن دست حریف...") }
            delay(500L)

            // انتخاب هوش مصنوعی
            val aiWillCounter = aiProfile?.willSucceed() == true
            val aiChoice = if (aiWillCounter && Random.nextFloat() < 0.65f) {
                // هوش مصنوعی پیش‌بینی و دست برنده را انتخاب می‌کند
                when (choice) {
                    RpsChoice.ROCK -> RpsChoice.PAPER
                    RpsChoice.PAPER -> RpsChoice.SCISSORS
                    RpsChoice.SCISSORS -> RpsChoice.ROCK
                }
            } else {
                RpsChoice.values().random()
            }

            // تعیین برنده
            val (roundResultText, userWonRound, isTie) = evaluateRound(choice, aiChoice)
            var newUWins = state.userWins
            var newAiWins = state.aiWins

            if (!isTie) {
                if (userWonRound) newUWins++ else newAiWins++
            }

            val isOver = newUWins >= 3 || newAiWins >= 3
            val isUserChampion = newUWins >= 3
            val coins = if (isUserChampion) 100 else 30

            _uiState.update {
                it.copy(
                    aiChoice = aiChoice,
                    isRevealed = true,
                    userWins = newUWins,
                    aiWins = newAiWins,
                    roundStatusText = roundResultText,
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
                    gameType = "rps",
                    userScore = newUWins,
                    aiScore = newAiWins,
                    isWin = isUserChampion,
                    aiDifficulty = aiProfile?.level?.titleFa ?: "متوسط",
                    rewardCoins = coins
                )
            } else {
                // رفتن خودکار به دست بعد پس از ۲ ثانیه
                delay(2000L)
                if (!_uiState.value.isGameOver) {
                    _uiState.update { it.copy(roundNumber = it.roundNumber + 1) }
                    startCountdown()
                }
            }
        }
    }

    private fun evaluateRound(u: RpsChoice, a: RpsChoice): Triple<String, Boolean, Boolean> {
        if (u == a) {
            return Triple("🤝 مساوی شد! (${u.titleFa} در برابر ${a.titleFa})", false, true)
        }
        val userWins = when (u) {
            RpsChoice.ROCK -> a == RpsChoice.SCISSORS
            RpsChoice.PAPER -> a == RpsChoice.ROCK
            RpsChoice.SCISSORS -> a == RpsChoice.PAPER
        }
        val text = if (userWins) {
            "🎉 شما برنده شدید! (${u.titleFa} در برابر ${a.titleFa})"
        } else {
            " حریف برنده شد! (${a.titleFa} در برابر ${u.titleFa})"
        }
        return Triple(text, userWins, false)
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
