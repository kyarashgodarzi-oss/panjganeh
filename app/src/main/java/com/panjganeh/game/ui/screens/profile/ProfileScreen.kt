package com.panjganeh.game.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.data.local.entity.MatchHistoryEntity
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.components.ReferenceSimpleTopBar
import com.panjganeh.game.ui.components.TapsellBanner
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaError
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
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    userRepository: UserRepository,
    gameRepository: GameRepository,
    tapsellManager: TapsellManager,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val user by userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)
    val matchHistory by gameRepository.matchHistory.collectAsStateWithLifecycle(initialValue = emptyList())

    val isVip = vip?.isVip == true
    var showEditNameDialog by remember { mutableStateOf(false) }
    var newNameText by remember { mutableStateOf("") }

    val totalMatches = (user?.wins ?: 0) + (user?.losses ?: 0)
    val winRate = if (totalMatches > 0) ((user?.wins ?: 0) * 100) / totalMatches else 0
    val nextLevelXp = (user?.level ?: 1) * 200
    val xpProgress = (user?.xp ?: 0).toFloat() / nextLevelXp.toFloat()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ReferenceSimpleTopBar(title = "پروفایل", onBackClick = onNavigateBack)
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(ArenaBackground, Color(0xFF0B1024))))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // کارت پروفایل کاربر
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("profile_main_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = 1.5.dp,
                        brush = if (isVip) Brush.horizontalGradient(listOf(VipGold, GoldPrimary))
                        else Brush.horizontalGradient(listOf(Color(0xFF7C5CFF), Color(0xFF2DD4BF)))
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .background(
                                Brush.verticalGradient(listOf(Color(0xFF1B1740), ArenaSurface)),
                                RoundedCornerShape(24.dp)
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF7C5CFF), Color(0xFF2DD4BF))))
                                .border(2.5.dp, if (isVip) VipGold else GoldLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ArenaBackground, modifier = Modifier.size(44.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user?.username ?: "جنگجو",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            if (isVip) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "VIP", tint = VipGold, modifier = Modifier.size(20.dp))
                            }
                            IconButton(
                                onClick = {
                                    newNameText = user?.username ?: ""
                                    showEditNameDialog = true
                                },
                                modifier = Modifier.size(28.dp).testTag("edit_name_button")
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "ویرایش نام", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = "عنوان: ${user?.title ?: "تازه وارد"} | سطح ${user?.level ?: 1}",
                            color = GoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // نوار پیشرفت XP
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "پیشرفت تجربه سطح", color = TextMuted, fontSize = 11.sp)
                                Text(text = "${user?.xp ?: 0} / $nextLevelXp XP", color = SkySecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { xpProgress.coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = SkySecondary,
                                trackColor = ArenaBackground
                            )
                        }
                    }
                }
            }

            // آمار نبردها
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(modifier = Modifier.weight(1f), title = "امتیاز کل", value = "${user?.xp ?: 0}", color = SkySecondary)
                    StatBox(modifier = Modifier.weight(1f), title = "باخت‌ها", value = "${user?.losses ?: 0}", color = GoldPrimary)
                    StatBox(modifier = Modifier.weight(1f), title = "بردها", value = "${user?.wins ?: 0}", color = EmeraldTertiary)
                }
            }

            item {
                Card(
                    Modifier.fillMaxWidth().testTag("profile_achievements"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF071B35)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(SkySecondary.copy(alpha = .55f), GoldPrimary.copy(alpha = .45f)))
                    )
                ) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 15.dp)) {
                        Text("مدال‌ها", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(
                            Modifier.fillMaxWidth().padding(top = 14.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AchievementBadge(Icons.Default.Shield, "برنز", Color(0xFF2DD4BF))
                            AchievementBadge(Icons.Default.Star, "طلا", GoldPrimary)
                            AchievementBadge(Icons.Default.EmojiEvents, "نقره", Color(0xFFCBD5E1))
                            AchievementBadge(Icons.Default.MilitaryTech, "برتر", Color(0xFFEF8D32))
                        }
                    }
                }
            }

            item {
                Card(
                    Modifier.fillMaxWidth().testTag("profile_actions"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF071B35)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(SkySecondary.copy(alpha = .45f), Color(0xFF173C68)))
                    )
                ) {
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        ProfileActionRow(Icons.Default.Person, "آواتار", SkySecondary) {
                            Toast.makeText(context, user?.username ?: "پروفایل", Toast.LENGTH_SHORT).show()
                        }
                        ProfileActionRow(Icons.Default.Edit, "تغییر نام", Color(0xFF2DD4BF)) {
                            newNameText = user?.username ?: ""
                            showEditNameDialog = true
                        }
                        ProfileActionRow(Icons.Default.EmojiEvents, "آمار بازی", GoldPrimary) {
                            scope.launch { listState.animateScrollToItem(4) }
                        }
                        ProfileActionRow(Icons.Default.Settings, "تنظیمات حساب", SkySecondary, onNavigateToSettings)
                    }
                }
            }

            item {
                Text(
                    text = "تاریخچه مسابقات اخیر",
                    color = GoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (matchHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ArenaSurface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "هنوز مسابقه‌ای ثبت نشده است. وارد آرنا شوید و مبارزه کنید!", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                items(matchHistory, key = { it.id }) { match ->
                    MatchHistoryItem(match = match)
                }
            }

            // ═══════════════════════════════════════════════════════════
            // بنر تبلیغاتی تپسل (اگه کاربر VIP نباشه)
            // ═══════════════════════════════════════════════════════════
            item {
                if (!isVip) {
                    TapsellBanner(tapsellManager = tapsellManager)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // دیالوگ ویرایش نام
        if (showEditNameDialog) {
            Dialog(onDismissRequest = { showEditNameDialog = false }) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ArenaSurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "تغییر نام جنگجو", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = newNameText,
                            onValueChange = { newNameText = it },
                            placeholder = { Text("نام جدید...", color = TextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ArenaBackground,
                                unfocusedContainerColor = ArenaBackground,
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = {
                                    if (newNameText.isNotBlank()) {
                                        scope.launch {
                                            user?.let { u ->
                                                userRepository.updateUser(u.copy(username = newNameText.trim()))
                                            }
                                            showEditNameDialog = false
                                            Toast.makeText(context, "نام با موفقیت تغییر کرد", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "ذخیره")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Text(label, color = TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f).padding(horizontal = 10.dp))
        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(19.dp))
    }
}

@Composable
private fun AchievementBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(44.dp).clip(CircleShape)
                .background(tint.copy(alpha = .12f))
                .border(1.5.dp, tint.copy(alpha = .85f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Text(label, color = TextSecondary, fontSize = 9.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
fun StatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun MatchHistoryItem(match: MatchHistoryEntity) {
    val challengeTitle = when (match.gameType) {
        "word" -> "نبرد کلمات"
        "memory" -> "حافظه کارت‌ها"
        "dice" -> "نبرد تاس"
        "rps" -> "سنگ کاغذ قیچی"
        else -> "ساخت جملات"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (match.isWin) EmeraldTertiary.copy(alpha = 0.2f) else ArenaError.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (match.isWin) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (match.isWin) EmeraldTertiary else ArenaError,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = challengeTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "حریف: سطح ${match.aiDifficulty}", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${match.userScore} - ${match.aiScore}",
                    color = if (match.isWin) EmeraldTertiary else ArenaError,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(text = "+${match.rewardCoins} سکه", color = GoldLight, fontSize = 10.sp)
            }
        }
    }
}
