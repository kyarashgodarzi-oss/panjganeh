package com.panjganeh.game.challenges.rps

import android.app.Activity
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Star
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
            RpsReferenceHeader(
                round = state.roundNumber,
                userScore = state.userWins,
                aiScore = state.aiWins,
                onExit = onNavigateBack
            )

            // نشانگر برد دست‌ها
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    for (i in 1..3) {
                        val isWon = state.userWins >= i
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isWon) GoldPrimary else ArenaSurfaceBorder,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = "اولین به ۳ برد برنده است",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Row {
                    for (i in 1..3) {
                        val isWon = state.aiWins >= i
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isWon) SkySecondary else ArenaSurfaceBorder,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

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
                        .padding(horizontal = 20.dp, vertical = 38.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.18f))
                            .border(4.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.countdownSeconds.coerceAtLeast(0).toString(),
                            fontSize = 58.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            RpsChoice.PAPER,
                            RpsChoice.SCISSORS,
                            RpsChoice.ROCK
                        ).forEach { choice ->
                            val isSelected = state.userChoice == choice
                            RpsReferenceChoice(
                                choice = choice,
                                selected = isSelected,
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

@Composable
private fun RpsReferenceHeader(
    round: Int,
    userScore: Int,
    aiScore: Int,
    onExit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = 0.88f)),
        border = BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.72f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GoldPrimary.copy(alpha = 0.13f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.65f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = round.toString() + "/5",
                            color = GoldPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ARENA BATTLE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "سنگ کاغذ قیچی (بهترین از ۵)",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onExit),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "خروج",
                        tint = TextSecondary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RpsPlayerBadge(
                    name = "تایان هوشمند",
                    score = aiScore,
                    color = SkySecondary,
                    icon = Icons.Default.SmartToy,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ArenaBackground)
                        .border(1.dp, ArenaSurfaceBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("VS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                RpsPlayerBadge(
                    name = "شما",
                    score = userScore,
                    color = GoldPrimary,
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f),
                    rightAligned = true
                )
            }
        }
    }
}

@Composable
private fun RpsPlayerBadge(
    name: String,
    score: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    rightAligned: Boolean = false
) {
    Row(
        modifier = modifier.padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (rightAligned) Arrangement.End else Arrangement.Start
    ) {
        if (!rightAligned) {
            RpsPlayerIcon(icon, color)
        }
        Column(
            horizontalAlignment = if (rightAligned) Alignment.End else Alignment.Start,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(name, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(score.toString(), color = color, fontSize = 23.sp, fontWeight = FontWeight.Black)
        }
        if (rightAligned) {
            RpsPlayerIcon(icon, color)
        }
    }
}

@Composable
private fun RpsPlayerIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f))
            .border(2.dp, color.copy(alpha = 0.75f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(25.dp))
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

    Box(
        modifier = modifier
            .height(194.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (selected) GoldPrimary.copy(alpha = 0.16f)
                else Color(0xFF070B17).copy(alpha = 0.84f)
            )
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) GoldPrimary else ArenaSurfaceBorder,
                RoundedCornerShape(22.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .testTag("rps_choice_" + choice.name.lowercase()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 54.sp)
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

}
