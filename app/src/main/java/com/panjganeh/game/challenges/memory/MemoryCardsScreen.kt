package com.panjganeh.game.challenges.memory

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.ui.components.BattleHeader
import com.panjganeh.game.ui.components.BattleResultDialog
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaCardBack
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.EmeraldTertiary
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted

@Composable
fun MemoryCardsScreen(
    viewModel: MemoryCardsViewModel,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // هدر نبرد حافظه
            BattleHeader(
                challengeTitle = "حافظه کارت‌ها",
                userScore = state.userScore,
                aiScore = state.aiScore,
                remainingSeconds = state.remainingSeconds,
                aiProfile = state.aiProfile,
                onExitClick = onNavigateBack
            )

            // راهنمای وضعیت
            if (state.isInitialPreview) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "کارت‌ها را خوب به خاطر بسپارید! (۲ ثانیه فرصت)",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جفت‌های کشف شده: ${state.matchedPairsCount} از ۸",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "درست +۱۵ | خطا -۳",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // جدول 4x4 کارت‌های حافظه
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("memory_cards_grid"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.cards, key = { it.id }) { card ->
                    MemoryCardItem(
                        card = card,
                        onClick = { viewModel.onCardClick(card.id) }
                    )
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
fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "card_flip"
    )

    val isFront = rotation > 90f

    val borderColor = when {
        card.isMatched && card.matchedBy == "USER" -> GoldPrimary
        card.isMatched && card.matchedBy == "AI" -> SkySecondary
        card.isFaceUp -> EmeraldTertiary
        else -> ArenaSurfaceBorder
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(16.dp))
            .background(if (isFront) ArenaSurface else ArenaCardBack)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !card.isMatched && !card.isFaceUp) { onClick() }
            .testTag("card_${card.id}"),
        contentAlignment = Alignment.Center
    ) {
        if (isFront) {
            Text(
                text = card.emoji,
                fontSize = 32.sp,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            )
        } else {
            Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = null,
                tint = TextMuted.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
