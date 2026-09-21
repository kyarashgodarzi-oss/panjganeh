package com.panjganeh.game.challenges.memory

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

data class MemoryCard(
    val id: Int,
    val emoji: String,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false,
    val matchedBy: String? = null // "USER" or "AI"
)

data class MemoryUiState(
    val cards: List<MemoryCard> = emptyList(),
    val remainingSeconds: Int = 90,
    val userScore: Int = 0,
    val aiScore: Int = 0,
    val isInitialPreview: Boolean = true,
    val isGameOver: Boolean = false,
    val isWin: Boolean = false,
    val earnedCoins: Int = 0,
    val aiProfile: AiProfile? = null,
    val matchedPairsCount: Int = 0
)

class MemoryCardsViewModel(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryUiState())
    val uiState = _uiState.asStateFlow()

    private val emojis = listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼")
    private var timerJob: Job? = null
    private var aiJob: Job? = null
    private var aiProfile: AiProfile? = null

    private var firstFlippedIndex: Int? = null
    private var isProcessingFlip = false

    fun startGame(difficulty: String) {
        viewModelScope.launch {
            aiProfile = AiProfile.createRandom(difficulty)
            // ساخت جدول 4x4 با ۸ جفت
            val cardList = (emojis + emojis).shuffled().mapIndexed { index, emoji ->
                MemoryCard(id = index, emoji = emoji, isFaceUp = true, isMatched = false)
            }

            _uiState.update {
                it.copy(
                    cards = cardList,
                    remainingSeconds = 90,
                    userScore = 0,
                    aiScore = 0,
                    isInitialPreview = true,
                    isGameOver = false,
                    aiProfile = aiProfile,
                    matchedPairsCount = 0
                )
            }

            // نمایش ۲ ثانیه‌ای کارت‌ها در شروع
            delay(2000L)
            _uiState.update { state ->
                state.copy(
                    cards = state.cards.map { it.copy(isFaceUp = false) },
                    isInitialPreview = false
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
                val delayMs = aiProfile?.getActionDelay() ?: 3500L
                delay(delayMs)
                if (_uiState.value.isGameOver) break

                val unmatched = _uiState.value.cards.filter { !it.isMatched }
                if (unmatched.size < 2) {
                    endGame()
                    break
                }

                val aiSuccess = aiProfile?.willSucceed() == true
                if (aiSuccess) {
                    // هوش مصنوعی یک جفت درست پیدا می‌کند
                    val pairEmoji = unmatched.groupBy { it.emoji }.filter { it.value.size >= 2 }.keys.randomOrNull()
                    if (pairEmoji != null) {
                        val pairCards = unmatched.filter { it.emoji == pairEmoji }.take(2)
                        _uiState.update { state ->
                            val updatedCards = state.cards.map { card ->
                                if (pairCards.any { it.id == card.id }) {
                                    card.copy(isMatched = true, isFaceUp = true, matchedBy = "AI")
                                } else card
                            }
                            state.copy(
                                cards = updatedCards,
                                aiScore = state.aiScore + 15,
                                matchedPairsCount = state.matchedPairsCount + 1
                            )
                        }
                    }
                } else {
                    _uiState.update { it.copy(aiScore = (it.aiScore - 3).coerceAtLeast(0)) }
                }

                // بررسی اتمام کارت‌ها
                if (_uiState.value.cards.all { it.isMatched }) {
                    endGame()
                    break
                }
            }
        }
    }

    fun onCardClick(cardId: Int) {
        val state = _uiState.value
        if (state.isInitialPreview || state.isGameOver || isProcessingFlip) return

        val clickedCard = state.cards.firstOrNull { it.id == cardId } ?: return
        if (clickedCard.isFaceUp || clickedCard.isMatched) return

        if (firstFlippedIndex == null) {
            // کارت اول رو شد
            firstFlippedIndex = cardId
            _uiState.update { s ->
                s.copy(cards = s.cards.map { if (it.id == cardId) it.copy(isFaceUp = true) else it })
            }
        } else {
            // کارت دوم رو شد
            val firstId = firstFlippedIndex!!
            firstFlippedIndex = null
            isProcessingFlip = true

            _uiState.update { s ->
                s.copy(cards = s.cards.map { if (it.id == cardId) it.copy(isFaceUp = true) else it })
            }

            viewModelScope.launch {
                delay(600L)
                val card1 = _uiState.value.cards.first { it.id == firstId }
                val card2 = _uiState.value.cards.first { it.id == cardId }

                if (card1.emoji == card2.emoji) {
                    // جفت درست!
                    _uiState.update { s ->
                        val updatedCards = s.cards.map { c ->
                            if (c.id == firstId || c.id == cardId) {
                                c.copy(isMatched = true, isFaceUp = true, matchedBy = "USER")
                            } else c
                        }
                        s.copy(
                            cards = updatedCards,
                            userScore = s.userScore + 15,
                            matchedPairsCount = s.matchedPairsCount + 1
                        )
                    }
                } else {
                    // جفت اشتباه
                    _uiState.update { s ->
                        val updatedCards = s.cards.map { c ->
                            if (c.id == firstId || c.id == cardId) {
                                c.copy(isFaceUp = false)
                            } else c
                        }
                        s.copy(
                            cards = updatedCards,
                            userScore = (s.userScore - 3).coerceAtLeast(0)
                        )
                    }
                }
                isProcessingFlip = false

                if (_uiState.value.cards.all { it.isMatched }) {
                    endGame()
                }
            }
        }
    }

    private fun endGame() {
        timerJob?.cancel()
        aiJob?.cancel()
        val state = _uiState.value
        val isWin = state.userScore >= state.aiScore
        val coins = if (isWin) 90 else 30

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
                xpReward = if (isWin) 120 else 40,
                coinReward = coins
            )
            gameRepository.recordMatch(
                gameType = "memory",
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
