package com.panjganeh.game.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.EmeraldTertiary
import com.panjganeh.game.ui.theme.GoldDark
import com.panjganeh.game.ui.theme.GoldLight
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import com.panjganeh.game.ui.theme.VipGold

data class LeaderboardPlayer(
    val rank: Int,
    val name: String,
    val title: String,
    val trophies: Int,
    val wins: Int,
    val isUser: Boolean = false,
    val isVip: Boolean = false
)

@Composable
fun LeaderboardScreen(
    userRepository: UserRepository,
    onNavigateBack: () -> Unit
) {
    val user by userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)

    val isUserVip = vip?.isVip == true

    // ترکیب هوش‌های مصنوعی لیدربورد با رتبه واقعی کاربر
    val userTrophies = 1000 + ((user?.wins ?: 0) * 30) - ((user?.losses ?: 0) * 10)
    val leaderboard = listOf(
        LeaderboardPlayer(1, "سایه شب", "افسانه زنده", 2450, 94, isVip = true),
        LeaderboardPlayer(2, "تایتان هوشمند", "فرمانده پنجگانه", 2180, 81),
        LeaderboardPlayer(3, "ققنوس طلایی", "استاد تاکتیک", 1920, 72, isVip = true),
        LeaderboardPlayer(4, "آریا بات", "استاد تاکتیک", 1680, 60),
        LeaderboardPlayer(5, user?.username ?: "قهرمان پنجگانه", user?.title ?: "تازه وارد", userTrophies, user?.wins ?: 0, isUser = true, isVip = isUserVip),
        LeaderboardPlayer(6, "سایبر سامورایی", "مبارز شجاع", 1250, 42),
        LeaderboardPlayer(7, "شبح دیجیتال", "مبارز شجاع", 1100, 35),
        LeaderboardPlayer(8, "سیروس ربات", "تازه وارد", 980, 28)
    ).sortedByDescending { it.trophies }
        .mapIndexed { idx, player -> player.copy(rank = idx + 1) }

    Scaffold(
        containerColor = ArenaBackground,
        topBar = {
            ArenaTopBar(
                user = user,
                vip = vip,
                title = "جدول برترین‌های پنجگانه",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("leaderboard_banner"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldPrimary, SkySecondary)))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "لیگ هفتگی جنگجویان", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "با پیروزی در نبردها جام کسب کنید و رتبه خود را ارتقا دهید", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            itemsIndexed(leaderboard) { _, player ->
                val isTop3 = player.rank <= 3
                val rankColor = when (player.rank) {
                    1 -> GoldPrimary
                    2 -> Color(0xFFCBD5E1)
                    3 -> Color(0xFFD97706)
                    else -> TextMuted
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(if (player.isUser) "user_rank_card" else "player_rank_${player.rank}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (player.isUser) GoldPrimary.copy(alpha = 0.15f) else ArenaSurface
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (player.isUser) GoldPrimary else if (isTop3) rankColor.copy(alpha = 0.5f) else ArenaSurfaceBorder
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // رتبه عددی یا جام
                            Box(
                                modifier = Modifier.size(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isTop3) {
                                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = rankColor, modifier = Modifier.size(24.dp))
                                } else {
                                    Text(text = "${player.rank}", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (player.isUser) GoldPrimary.copy(alpha = 0.2f) else SkySecondary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (player.isUser) Icons.Default.Person else Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = if (player.isUser) GoldPrimary else SkySecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (player.isUser) "${player.name} (شما)" else player.name,
                                        color = if (player.isUser) GoldLight else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (player.isVip) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "VIP", tint = VipGold, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text(text = player.title, color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        // تعداد جام‌ها و بردها
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${player.trophies}", color = GoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                            Text(text = "${player.wins} پیروزی", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
