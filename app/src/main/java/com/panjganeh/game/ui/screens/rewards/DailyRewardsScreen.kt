package com.panjganeh.game.ui.screens.rewards

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.data.local.entity.RewardItemEntity
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.components.TapsellBanner
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.EmeraldTertiary
import com.panjganeh.game.ui.theme.GoldLight
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun DailyRewardsScreen(
    userRepository: UserRepository,
    tapsellManager: TapsellManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    val user by userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)
    val rewards by userRepository.dailyRewards.collectAsStateWithLifecycle(initialValue = emptyList())

    val isVip = vip?.isVip == true

    Scaffold(
        containerColor = ArenaBackground,
        topBar = {
            ArenaTopBar(
                user = user,
                vip = vip,
                title = "جوایز ورود روزانه",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "تقویم ۷ روزه جوایز", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "هر روز با تماشای ویدیو، پاداش رایگان دریافت کنید!", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f).testTag("rewards_grid"),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rewards, key = { it.day }) { reward ->
                    val firstUnclaimedDay = rewards.filter { !it.isClaimed }.minOfOrNull { it.day } ?: 8
                    val isCurrentDay = reward.day == firstUnclaimedDay
                    val isLocked = !reward.isClaimed && !isCurrentDay

                    RewardCard(
                        reward = reward,
                        isLocked = isLocked,
                        onClaimClick = {
                            if (activity != null) {
                                tapsellManager.showRewardedVideo(
                                    activity = activity,
                                    rewardCoins = 0,
                                    onRewarded = {
                                        scope.launch {
                                            userRepository.claimDailyReward(reward.day)
                                            Toast.makeText(
                                                context,
                                                "پاداش روز ${reward.day} دریافت شد!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, "خطا در نمایش تبلیغ: $error", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    )
                }
            }

            // ═══════════════════════════════════════════════════════════
            // بنر تبلیغاتی تپسل (اگه کاربر VIP نباشه)
            // ═══════════════════════════════════════════════════════════
            if (!isVip) {
                Spacer(modifier = Modifier.height(8.dp))
                TapsellBanner(tapsellManager = tapsellManager)
            }
        }
    }
}

@Composable
fun RewardCard(
    reward: RewardItemEntity,
    isLocked: Boolean,
    onClaimClick: () -> Unit
) {
    val isDay7 = reward.day == 7
    val icon = when (reward.rewardType) {
        "COINS" -> Icons.Default.MonetizationOn
        "TICKETS" -> Icons.Default.ConfirmationNumber
        else -> Icons.Default.Face
    }
    val iconColor = when (reward.rewardType) {
        "COINS" -> GoldPrimary
        "TICKETS" -> SkySecondary
        else -> EmeraldTertiary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reward_day_${reward.day}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) ArenaSurface.copy(alpha = 0.5f) else ArenaSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (reward.isClaimed)
                androidx.compose.ui.graphics.SolidColor(ArenaSurfaceBorder.copy(alpha = 0.5f))
            else if (isLocked)
                androidx.compose.ui.graphics.SolidColor(ArenaSurfaceBorder.copy(alpha = 0.3f))
            else
                androidx.compose.ui.graphics.SolidColor(if (isDay7) GoldPrimary else ArenaSurfaceBorder)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "روز ${reward.day}",
                color = if (isLocked) TextMuted else (if (isDay7) GoldLight else TextSecondary),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLocked) TextMuted.copy(alpha = 0.1f)
                        else iconColor.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else icon,
                    contentDescription = null,
                    tint = if (isLocked) TextMuted else iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val rewardTitle = when (reward.rewardType) {
                "COINS" -> "${reward.amount} سکه"
                "TICKETS" -> "${reward.amount} بلیط"
                else -> "آواتار اختصاصی"
            }
            Text(
                text = rewardTitle,
                color = if (isLocked) TextMuted else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            when {
                reward.isClaimed -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "دریافت شده", color = EmeraldTertiary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                isLocked -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "قفل", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                else -> {
                    Button(
                        onClick = onClaimClick,
                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("claim_btn_${reward.day}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "تماشا و دریافت", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
