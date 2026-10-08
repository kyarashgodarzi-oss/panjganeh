package com.panjganeh.game.challenges.word

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.ui.components.BattleArenaBackground
import com.panjganeh.game.ui.components.BattleHeader
import com.panjganeh.game.ui.components.BattleResultDialog
import com.panjganeh.game.ui.components.BattleRoundStars
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaError
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.EmeraldTertiary
import com.panjganeh.game.ui.theme.GoldLight
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.PurplePrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordBattleScreen(
    viewModel: WordBattleViewModel,
    tapsellManager: TapsellManager,
    difficulty: String,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity
    var showResultDialog by remember { mutableStateOf(false) }

    LaunchedEffect(difficulty) { viewModel.startGame(difficulty) }

    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) {
            if (activity != null) {
                tapsellManager.onBattleFinished(activity, state.isWin) { showResultDialog = true }
            } else {
                showResultDialog = true
            }
        } else {
            showResultDialog = false
        }
    }

    Scaffold(containerColor = ArenaBackground) { padding ->
        Box(Modifier.fillMaxSize()) {
            BattleArenaBackground(challenge = "نبرد کلمات", modifier = Modifier.fillMaxSize())

            Column(
                Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BattleHeader(
                    challengeTitle = "نبرد کلمات",
                    userScore = state.userScore,
                    aiScore = state.aiScore,
                    remainingSeconds = state.remainingSeconds,
                    aiProfile = state.aiProfile,
                    onExitClick = onNavigateBack
                )
                BattleRoundStars(userScore = state.userScore, aiScore = state.aiScore)

                val current = state.currentWordItem
                if (current != null) {
                    Card(
                        Modifier.fillMaxWidth().padding(horizontal = 18.dp).testTag("word_puzzle_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = .96f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SkySecondary.copy(alpha = .55f))
                    ) {
                        Column(
                            Modifier.fillMaxWidth().padding(15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.clip(RoundedCornerShape(10.dp))
                                        .background(GoldPrimary.copy(alpha = .12f))
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lightbulb, null, tint = GoldPrimary, modifier = Modifier.size(15.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("راهنما", color = GoldLight, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                                Text(state.currentMode.titleFa, color = SkySecondary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }

                            Text(
                                current.hint,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            )

                            Spacer(Modifier.height(13.dp))

                            if (state.currentMode == WordBattleMode.SCRAMBLED_LETTERS) {
                                Text(
                                    current.scrambled,
                                    color = SkySecondary,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 4.sp,
                                    modifier = Modifier.testTag("scrambled_word")
                                )
                            }

                            WordSlots(
                                wordLength = current.word.length,
                                input = state.userInput,
                                revealed = if (state.currentMode == WordBattleMode.MISSING_LETTERS) current.word else null
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                if (state.userInput.isEmpty()) "حروف را انتخاب کن" else "پاسخ: ${state.userInput}",
                                color = if (state.isCorrectFeedback) EmeraldTertiary else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )

                            Spacer(Modifier.height(12.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth().testTag("word_letter_keyboard"),
                                horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                letterChoices(current.word).forEach { letter ->
                                    LetterKey(
                                        letter = letter,
                                        enabled = !state.isGameOver && state.userInput.length < current.word.length,
                                        onClick = { viewModel.onUserInputChange(state.userInput + letter) }
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            AnimatedVisibility(state.feedbackMessage.isNotEmpty()) {
                                Text(
                                    state.feedbackMessage,
                                    color = if (state.isCorrectFeedback) EmeraldTertiary else ArenaError,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = { viewModel.onUserInputChange(state.userInput.dropLast(1)) },
                                    enabled = state.userInput.isNotEmpty() && !state.isGameOver,
                                    modifier = Modifier.weight(1f).testTag("word_backspace_button")
                                ) {
                                    Icon(Icons.Default.Backspace, null, tint = TextMuted, modifier = Modifier.size(17.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("پاک کردن", color = TextMuted, fontSize = 11.sp)
                                }

                                TextButton(
                                    onClick = { viewModel.skipWord() },
                                    enabled = !state.isGameOver,
                                    modifier = Modifier.weight(1f).testTag("skip_word_button")
                                ) {
                                    Icon(Icons.Default.SkipNext, null, tint = TextMuted, modifier = Modifier.size(17.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("بعدی", color = TextMuted, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { viewModel.submitAnswer() },
                                    enabled = state.userInput.isNotEmpty() && !state.isGameOver,
                                    modifier = Modifier.weight(1.45f).height(46.dp).testTag("submit_word_button"),
                                    shape = RoundedCornerShape(13.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF201307))
                                ) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(17.dp))
                                    Spacer(Modifier.width(5.dp))
                                    Text("ثبت کلمه", fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showResultDialog && state.isGameOver) {
            BattleResultDialog(
                isWin = state.isWin,
                userScore = state.userScore,
                aiScore = state.aiScore,
                earnedCoins = state.earnedCoins,
                onPlayAgain = { viewModel.startGame(difficulty) },
                onBackHome = onNavigateBack
            )
        }
    }
}

@Composable
private fun WordSlots(wordLength: Int, input: String, revealed: String?) {
    Row(
        Modifier.fillMaxWidth().testTag("word_letter_slots"),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(wordLength) { index ->
            val char = input.getOrNull(index)
            val reveal = revealed?.getOrNull(index)
            Box(
                Modifier.padding(horizontal = 3.dp).size(38.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        when {
                            char != null -> PurplePrimary.copy(alpha = .28f)
                            reveal != null && index % 2 == 0 -> GoldPrimary.copy(alpha = .12f)
                            else -> ArenaBackground
                        }
                    )
                    .border(
                        1.dp,
                        when {
                            char != null -> PurplePrimary
                            reveal != null && index % 2 == 0 -> GoldPrimary.copy(alpha = .6f)
                            else -> ArenaSurfaceBorder
                        },
                        RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char?.toString() ?: if (reveal != null && index % 2 == 0) reveal.toString() else "•",
                    color = if (char != null) TextPrimary else if (reveal != null && index % 2 == 0) GoldLight else TextMuted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun LetterKey(letter: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.size(43.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (enabled) PurplePrimary.copy(alpha = .18f) else ArenaBackground)
            .border(1.dp, if (enabled) PurplePrimary.copy(alpha = .75f) else ArenaSurfaceBorder, RoundedCornerShape(11.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .testTag("letter_$letter"),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = if (enabled) TextPrimary else TextMuted, fontSize = 17.sp, fontWeight = FontWeight.Black)
    }
}

private fun letterChoices(word: String): List<String> {
    val base = word.filter { !it.isWhitespace() }.map { it.toString() }.distinct()
    val distractors = listOf("ا", "ب", "ت", "ر", "س", "ن", "م", "ی", "د", "ک", "ل", "و")
    return (base + distractors).distinct().shuffled().take(12)
}
