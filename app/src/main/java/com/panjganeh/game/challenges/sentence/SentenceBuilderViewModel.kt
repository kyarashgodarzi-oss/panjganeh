package com.panjganeh.game.challenges.sentence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panjganeh.game.ai.AiProfile
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.SentenceItem
import com.panjganeh.game.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WordTile(
    val id: Int,
    val text: String,
    val isSelected: Boolean = false
)

data class SentenceUiState(
    val remainingSeconds: Int = 120,
    val userScore: Int = 0,
    val aiScore: Int = 0,
    val userCompletedCount: Int = 0,
    val aiCompletedCount: Int = 0,
    val currentSentence: SentenceItem? = null,
    val availableTiles: List<WordTile> = emptyList(),
    val constructedWords: List<WordTile> = emptyList(),
    val checkStatus: Boolean? = null, // true = correct, false = error, null = editing
    val feedbackMessage: String = "کلمات را برای ساختن جمله به ترتیب انتخاب کنید",
    val isGameOver: Boolean = false,
    val isWin: Boolean = false,
    val earnedCoins: Int = 0,
    val aiProfile: AiProfile? = null
)

class SentenceBuilderViewModel(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SentenceUiState())
    val uiState = _uiState.asStateFlow()

    private var sentencePool: List<SentenceItem> = emptyList()
    private var currentIndex = 0
    private var timerJob: Job? = null
    private var aiJob: Job? = null
    private var aiProfile: AiProfile? = null

    fun startGame(difficulty: String) {
        viewModelScope.launch {
            sentencePool = gameRepository.loadSentences().shuffled()
            currentIndex = 0
            aiProfile = AiProfile.createRandom(difficulty)

            loadNextSentence()

            _uiState.update {
                it.copy(
                    remainingSeconds = 120,
                    userScore = 0,
                    aiScore = 0,
                    userCompletedCount = 0,
                    aiCompletedCount = 0,
                    isGameOver = false,
                    aiProfile = aiProfile
                )
            }

            startTimer()
            startAiLoop()
        }
    }

    private fun loadNextSentence() {
        if (sentencePool.isEmpty()) return
        val item = sentencePool[currentIndex % sentencePool.size]
        currentIndex++

        val tiles = item.words.mapIndexed { idx, word ->
            WordTile(id = idx, text = word, isSelected = false)
        }.shuffled()

        _uiState.update {
            it.copy(
                currentSentence = item,
                availableTiles = tiles,
                constructedWords = emptyList(),
                checkStatus = null,
                feedbackMessage = "جمله شماره ${it.userCompletedCount + 1}: کلمات را مرتب کنید"
            )
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
                val delayMs = (aiProfile?.getActionDelay() ?: 4000L) * 2
                delay(delayMs)
                if (_uiState.value.isGameOver) break

                val aiSuccess = aiProfile?.willSucceed() == true
                if (aiSuccess) {
                    _uiState.update {
                        it.copy(
                            aiScore = it.aiScore + 20,
                            aiCompletedCount = it.aiCompletedCount + 1
                        )
                    }
                }
            }
        }
    }

    fun onTileClick(tile: WordTile) {
        val state = _uiState.value
        if (state.isGameOver) return

        // افزودن کاشی به جمله کاربر
        val updatedAvailable = state.availableTiles.map {
            if (it.id == tile.id) it.copy(isSelected = true) else it
        }
        val updatedConstructed = state.constructedWords + tile

        _uiState.update {
            it.copy(
                availableTiles = updatedAvailable,
                constructedWords = updatedConstructed,
                checkStatus = null
            )
        }
    }

    fun onRemoveConstructed(tile: WordTile) {
        val state = _uiState.value
        if (state.isGameOver) return

        val updatedConstructed = state.constructedWords.filter { it.id != tile.id }
        val updatedAvailable = state.availableTiles.map {
            if (it.id == tile.id) it.copy(isSelected = false) else it
        }

        _uiState.update {
            it.copy(
                availableTiles = updatedAvailable,
                constructedWords = updatedConstructed,
                checkStatus = null
            )
        }
    }

    fun clearConstructed() {
        val state = _uiState.value
        if (state.isGameOver) return
        _uiState.update {
            it.copy(
                availableTiles = it.availableTiles.map { t -> t.copy(isSelected = false) },
                constructedWords = emptyList(),
                checkStatus = null
            )
        }
    }

    fun checkSentence() {
        val state = _uiState.value
        val target = state.currentSentence ?: return
        if (state.constructedWords.isEmpty()) return

        val constructedSentence = state.constructedWords.joinToString(" ") { it.text.trim() }
        val targetSentence = target.words.joinToString(" ") { it.trim() }

        if (constructedSentence == targetSentence) {
            // جمله صحیح است!
            val newScore = state.userScore + 20
            val newCompleted = state.userCompletedCount + 1
            _uiState.update {
                it.copy(
                    userScore = newScore,
                    userCompletedCount = newCompleted,
                    checkStatus = true,
                    feedbackMessage = "🎉 آفرین! جمله کاملاً درست بود (+۲۰ امتیاز)"
                )
            }

            viewModelScope.launch {
                delay(1000L)
                if (!_uiState.value.isGameOver) {
                    loadNextSentence()
                }
            }
        } else {
            // جمله اشتباه است
            _uiState.update {
                it.copy(
                    checkStatus = false,
                    feedbackMessage = " اشتباه است! ترتیب کلمات را بررسی و اصلاح کنید"
                )
            }
        }
    }

    private fun endGame() {
        timerJob?.cancel()
        aiJob?.cancel()
        val state = _uiState.value
        val isWin = state.userScore >= state.aiScore
        val coins = if (isWin) 100 else 35

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
                xpReward = if (isWin) 130 else 45,
                coinReward = coins
            )
            gameRepository.recordMatch(
                gameType = "sentence",
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
