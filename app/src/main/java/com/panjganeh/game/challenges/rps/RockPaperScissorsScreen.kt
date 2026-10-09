package com.panjganeh.game.challenges.rps

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.panjganeh.game.ui.components.BattleRoundStars
import com.panjganeh.game.ui.components.BattleResultDialog
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.GoldLight
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary

@Composable
fun RockPaperScissorsScreen(
    viewModel: RockPaperScissorsViewModel,
    tapsellManager: TapsellManager,
    difficulty: String,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    // وضعیت نمایش دیالوگ نتیجه (با تأخیر برای Interstitial)
    var showResultDialog by remember { mutableStateOf(false) }

    LaunchedEffect(difficulty) {
        viewModel.startGame(difficulty)
    }

    // وقتی بازی تموم شد، اول Interstitial نشون بده، بعد دیالوگ
    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) {
            if (activity != null) {
                tapsellManager.onBattleFinished(
                    activity = activity,
                    isWin = state.isWin,
                    onFinished = { showResultDialog = true }
                )
            } else {
                showResultDialog = true
            }
        } else {
            showResultDialog = false
        }
    }

    Scaffold(
        containerColor = ArenaBackground
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            BattleArenaBackground(challenge = "سنگ کاغذ قیچی", modifier = Modifier.fillMaxSize())
            Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BattleHeader(
                challengeTitle = "سنگ کاغذ قیچی (بهترین از ۵)",
                userScore = state.userWins,
                aiScore = state.aiWins,
                currentRound = state.roundNumber,
                maxRounds = 5,
                aiProfile = null,
                onExitClick = onNavigateBack
            )

            BattleRoundStars(
                userScore = state.userWins,
                aiScore = state.aiWins
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 33.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = 0.86f)),
                border = BorderStroke(1.dp, ArenaSurfaceBorder.copy(alpha = 0.85f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.18f))
                            .border(2.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.countdownSeconds.coerceAtLeast(0).toString(),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (state.isCountingDown) {
                            "آماده‌باش: " + state.countdownSeconds
                        } else {
                            state.roundStatusText
                        },
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ArenaBackground)
                            .padding(vertical = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "دست خود را انتخاب کنید:",
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RpsReferenceChoice(
                            choice = RpsChoice.PAPER,
                            selected = state.userChoice == RpsChoice.PAPER,
                            enabled = !state.isCountingDown && !state.isRevealed && !state.isGameOver,
                            onClick = { viewModel.onUserSelect(RpsChoice.PAPER) },
                            modifier = Modifier.fillMaxWidth(0.56f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf(RpsChoice.SCISSORS, RpsChoice.ROCK).forEach { choice ->
                                RpsReferenceChoice(
                                    choice = choice,
                                    selected = state.userChoice == choice,
                                    enabled = !state.isCountingDown && !state.isRevealed && !state.isGameOver,
                                    onClick = { viewModel.onUserSelect(choice) },
                                    modifier = Modifier.weight(1f)
                                )
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
                userScore = state.userWins,
                aiScore = state.aiWins,
                earnedCoins = state.earnedCoins,
                onPlayAgain = { viewModel.startGame(difficulty) },
                onBackHome = onNavigateBack
            )
        }
    }

}

@Composable
private fun RpsReferenceChoice(
    choice: RpsChoice,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val emoji = when (choice) {
        RpsChoice.ROCK -> "✊"
        RpsChoice.PAPER -> "✋"
        RpsChoice.SCISSORS -> "✌️"
    }
    val accent = when (choice) {
        RpsChoice.ROCK -> SkySecondary
        RpsChoice.PAPER -> EmeraldTertiary
        RpsChoice.SCISSORS -> Color(0xFFB46CFF)
    }
    Box(
        modifier = modifier
            .height(118.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (selected) accent.copy(alpha = 0.26f)
                else accent.copy(alpha = 0.10f)
            )
            .border(
                if (selected) 2.dp else 1.2.dp,
                if (selected) GoldPrimary else accent.copy(alpha = 0.8f),
                RoundedCornerShape(22.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .testTag("rps_choice_" + choice.name.lowercase()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 40.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                choice.titleFa,
                color = if (selected) GoldPrimary else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

