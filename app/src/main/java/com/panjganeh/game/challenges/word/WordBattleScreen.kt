package com.panjganeh.game.challenges.word

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.ui.components.BattleHeader
import com.panjganeh.game.ui.components.BattleResultDialog
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaError
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.EmeraldTertiary
import com.panjganeh.game.ui.theme.GoldLight
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordBattleScreen(
    viewModel: WordBattleViewModel,
    difficulty: String,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(difficulty) {
        viewModel.startGame(difficulty)
    }

    Scaffold(
        containerColor = ArenaBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // هدر نبرد
            BattleHeader(
                challengeTitle = "نبرد کلمات",
                userScore = state.userScore,
                aiScore = state.aiScore,
                remainingSeconds = state.remainingSeconds,
                aiProfile = state.aiProfile,
                onExitClick = onNavigateBack
            )

            Spacer(modifier = Modifier.height(8.dp))

            // حالت جاری چالش
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SkySecondary.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = state.currentMode.titleFa,
                    color = SkySecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // کارت معما و کلمه
            val current = state.currentWordItem
            if (current != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(ArenaSurfaceBorder))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // راهنمای کلمه
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldPrimary.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "راهنما: ${current.hint}", color = GoldLight, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // نمایش فرمت معما بر اساس حالت جاری
                        when (state.currentMode) {
                            WordBattleMode.MISSING_LETTERS -> {
                                Text(
                                    text = current.missing,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    letterSpacing = 4.sp
                                )
                                Text(text = "حروف جاافتاده را در کلمه بنویسید", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                            }
                            WordBattleMode.SCRAMBLED_LETTERS -> {
                                Text(
                                    text = current.scrambled,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SkySecondary,
                                    letterSpacing = 4.sp
                                )
                                Text(text = "حروف به‌هم‌ریخته را مرتب کرده و کلمه صحیح را بیابید", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                            }
                            WordBattleMode.TABLE_FILL -> {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    current.word.forEachIndexed { idx, ch ->
                                        val isHidden = idx % 2 != 0
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isHidden) ArenaBackground else GoldPrimary.copy(alpha = 0.2f))
                                                .border(1.dp, if (isHidden) ArenaSurfaceBorder else GoldPrimary, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isHidden) "؟" else ch.toString(),
                                                color = if (isHidden) TextMuted else GoldPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                }
                                Text(text = "کلمه کامل جدول را در کادر زیر تایپ کنید", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // فیلد ورودی کلمه
                        OutlinedTextField(
                            value = state.userInput,
                            onValueChange = { viewModel.onUserInputChange(it) },
                            placeholder = { Text("پاسخ کلمه را وارد کنید...", color = TextMuted) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("word_input_field"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ArenaBackground,
                                unfocusedContainerColor = ArenaBackground,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ArenaSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { viewModel.submitAnswer() })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // پیام بازخورد
                        AnimatedVisibility(visible = state.feedbackMessage.isNotEmpty()) {
                            Text(
                                text = state.feedbackMessage,
                                color = if (state.isCorrectFeedback) EmeraldTertiary else ArenaError,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // دکمه‌های تایید و بعدی
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { viewModel.skipWord() },
                                modifier = Modifier.testTag("skip_word_button")
                            ) {
                                Icon(imageVector = Icons.Default.SkipNext, contentDescription = null, tint = TextMuted)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "کلمه بعدی", color = TextMuted)
                            }

                            Button(
                                onClick = { viewModel.submitAnswer() },
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("submit_word_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "ثبت کلمه (+۱۰)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // دیالوگ پایان نبرد
        if (state.isGameOver) {
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
