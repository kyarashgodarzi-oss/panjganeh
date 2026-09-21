package com.panjganeh.game.challenges.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panjganeh.game.ai.AiProfile
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.data.repository.WordItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class WordBattleMode(val titleFa: String) {
    MISSING_LETTERS("حدس جای خالی"),
    SCRAMBLED_LETTERS("تشخیص کلمات درهم"),
    TABLE_FILL("پر کردن جدول کلمات")
}

data class WordBattleUiState(
    val remainingSeconds: Int = 60,
    val userScore: Int = 0,
    val aiScore: Int = 0,
    val currentMode: WordBattleMode = WordBattleMode.MISSING_LETTERS,
    val currentWordItem: WordItem? = null,
    val userInput: String = "",
    val feedbackMessage: String = "",
    val isCorrectFeedback: Boolean = false,
    val isGameOver: Boolean = false,
    val aiProfile: AiProfile? = null,
    val isWin: Boolean = false,
    val earnedCoins: Int = 0,
    val solvedCount: Int = 0
)

class WordBattleViewModel(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordBattleUiState())
    val uiState = _uiState.asStateFlow()

    private var wordsPool: List<WordItem> = emptyList()
    private var timerJob: Job? = null
    private var aiJob: Job? = null
    private var aiProfile: AiProfile? = null

    fun startGame(difficulty: String) {
        viewModelScope.launch {
            wordsPool = gameRepository.loadWords().shuffled()
            aiProfile = AiProfile.createRandom(difficulty)

            _uiState.update {
                it.copy(
                    remainingSeconds = 60,
                    userScore = 0,
                    aiScore = 0,
                    isGameOver = false,
                    aiProfile = aiProfile,
                    currentWordItem = wordsPool.firstOrNull(),
                    currentMode = WordBattleMode.values().random(),
                    userInput = "",
                    solvedCount = 0
                )
            }

            startTimer()
            startAiLoop()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000L)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            endGame()
        }
    }

    private fun startAiLoop() {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            while (!_uiState.value.isGameOver && _uiState.value.remainingSeconds > 0) {
                val delayMs = aiProfile?.getActionDelay() ?: 3000L
                delay(delayMs)
                if (_uiState.value.isGameOver) break

                val aiSuccess = aiProfile?.willSucceed() == true
                if (aiSuccess) {
                    _uiState.update { it.copy(aiScore = it.aiScore + 10) }
                } else {
                    _uiState.update { it.copy(aiScore = (it.aiScore - 2).coerceAtLeast(0)) }
                }
            }
        }
    }

    fun onUserInputChange(input: String) {
        _uiState.update { it.copy(userInput = input) }
    }

    fun submitAnswer() {
        val state = _uiState.value
        if (state.isGameOver || state.currentWordItem == null) return

        val trimmed = state.userInput.trim()
        val correctWord = state.currentWordItem.word.trim()

        if (trimmed == correctWord) {
            // کلمه درست
            val newScore = state.userScore + 10
            val nextWords = wordsPool.filter { it.word != correctWord }
            val nextItem = nextWords.randomOrNull() ?: state.currentWordItem
            val nextMode = WordBattleMode.values().random()

            _uiState.update {
                it.copy(
                    userScore = newScore,
                    currentWordItem = nextItem,
                    currentMode = nextMode,
                    userInput = "",
                    feedbackMessage = "آفرین! کلمه درست بود (+۱۰ امتیاز)",
                    isCorrectFeedback = true,
                    solvedCount = it.solvedCount + 1
                )
            }
        } else {
            // کلمه اشتباه
            val newScore = (state.userScore - 2).coerceAtLeast(0)
            _uiState.update {
                it.copy(
                    userScore = newScore,
                    feedbackMessage = "اشتباه بود! (-۲ امتیاز)",
                    isCorrectFeedback = false
                )
            }
        }
    }

    fun skipWord() {
        val state = _uiState.value
        if (state.isGameOver) return
        val nextItem = wordsPool.randomOrNull() ?: return
        val nextMode = WordBattleMode.values().random()
        _uiState.update {
            it.copy(
                currentWordItem = nextItem,
                currentMode = nextMode,
                userInput = "",
                feedbackMessage = "کلمه جدید بارگذاری شد"
            )
        }
    }

    private fun endGame() {
        timerJob?.cancel()
        aiJob?.cancel()
        val state = _uiState.value
        val isWin = state.userScore >= state.aiScore
        val coins = if (isWin) 80 else 25

        _uiState.update {
            it.copy(
                isGameOver = true,
                isWin = isWin,
                earnedCoins = coins
            )
        }

        viewModelScope.launch {
            userRepository.recordMatchResult(
                isWin = isWin,
                xpReward = if (isWin) 100 else 30,
                coinReward = coins
            )
            gameRepository.recordMatch(
                gameType = "word",
                userScore = state.userScore,
                aiScore = state.aiScore,
                isWin = isWin,
                aiDifficulty = aiProfile?.level?.titleFa ?: "متوسط",
                rewardCoins = coins
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        aiJob?.cancel()
    }
}
