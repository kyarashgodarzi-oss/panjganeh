package com.panjganeh.game.challenges.dice

import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.ui.components.BattleHeader
import com.panjganeh.game.ui.components.BattleResultDialog
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.GoldLight
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary

@Composable
fun DiceBattleScreen(
    viewModel: DiceBattleViewModel,
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

    // انیمیشن چرخش و تکان در حین پرتاب
    val infiniteTransition = rememberInfiniteTransition(label = "dice_shake")
    val diceRotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    val diceScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(140, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        containerColor = ArenaBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // هدر نبرد تاس
            BattleHeader(
                challengeTitle = "نبرد تاس (بهترین از ۵)",
                userScore = state.userRoundWins,
                aiScore = state.aiRoundWins,
                currentRound = state.roundNumber,
                maxRounds = 5,
                aiProfile = state.aiProfile,
                onExitClick = onNavigateBack
            )

            // نشانگر تعداد بردهای دست
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    for (i in 1..3) {
                        val isWon = state.userRoundWins >= i
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isWon) GoldPrimary else ArenaSurfaceBorder,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = "اولین به ۳ برد برنده نهایی است",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Row {
                    for (i in 1..3) {
                        val isWon = state.aiRoundWins >= i
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

            // میدان پرتاب تاس‌ها
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ArenaSurfaceBorder)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // تاس کاربر
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "تاس شما", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            DiceVisual(
                                value = state.userDiceValue,
                                color = GoldPrimary,
                                modifier = Modifier
                                    .graphicsLayer {
                                        if (state.isRolling) {
                                            rotationZ = diceRotation
                                            scaleX = diceScale
                                            scaleY = diceScale
                                        }
                                    }
                            )
                        }

                        Text(
                            text = "VS",
                            color = TextMuted,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )

                        // تاس AI
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "تاس حریف", color = SkySecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            DiceVisual(
                                value = state.aiDiceValue,
                                color = SkySecondary,
                                modifier = Modifier
                                    .graphicsLayer {
                                        if (state.isRolling) {
                                            rotationZ = -diceRotation
                                            scaleX = diceScale
                                            scaleY = diceScale
                                        }
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ArenaBackground)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.roundResultMessage,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.rollDice() },
                        enabled = !state.isRolling && !state.isGameOver,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("roll_dice_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = ArenaSurfaceBorder,
                            disabledContentColor = TextMuted
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Casino, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isRolling) "در حال پرتاب تاس..." else "پرتاب تاس 🎲",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        if (showResultDialog && state.isGameOver) {
            BattleResultDialog(
                isWin = state.isWin,
                userScore = state.userRoundWins,
                aiScore = state.aiRoundWins,
                earnedCoins = state.earnedCoins,
                onPlayAgain = { viewModel.startGame(difficulty) },
                onBackHome = onNavigateBack
            )
        }
    }
}

/**
 * کامپوننت گرافیکی نمایش ۶ وجه تاس با نقاط (Pips)
 */
@Composable
fun DiceVisual(
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(90.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(ArenaBackground)
            .border(2.5.dp, color, RoundedCornerShape(18.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        when (value) {
            1 -> Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
            2 -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                }
            }
            3 -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(color))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(color))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(color))
                }
            }
            4 -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(color))
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(color))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(color))
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(color))
                }
            }
            5 -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                }
            }
            else -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(color))
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(color))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(color))
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(color))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(color))
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(color))
                }
            }
        }
    }
}
