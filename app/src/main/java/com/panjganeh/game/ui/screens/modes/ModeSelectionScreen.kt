package com.panjganeh.game.ui.screens.modes

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panjganeh.game.R
import com.panjganeh.game.data.local.entity.ChallengeItemEntity
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaReferencePrimaryButton
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.DiceChallengeColor
import com.panjganeh.game.ui.theme.MemoryChallengeColor
import com.panjganeh.game.ui.theme.PinkTertiary
import com.panjganeh.game.ui.theme.PurplePrimary
import com.panjganeh.game.ui.theme.SkyBlue
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.WarmYellow
import com.panjganeh.game.ui.theme.WordChallengeColor
import kotlinx.coroutines.launch

@Composable
fun ModeSelectionScreen(
    userRepository: UserRepository,
    gameRepository: GameRepository,
    onStartChallenge: (String) -> Unit,
    onNavigateToOnline: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showOfflineChallenges by remember { mutableStateOf(false) }
    val challenges by gameRepository.allChallenges.collectAsState(initial = emptyList())
    val vip by userRepository.vipState.collectAsState(initial = null)
    val isVip = vip?.isVip == true &&
        (vip?.expireTimestamp == 0L || (vip?.expireTimestamp ?: 0L) > System.currentTimeMillis())

    fun play(id: String) {
        scope.launch {
            if (isVip || userRepository.deductTickets(1)) {
                onStartChallenge(id)
            } else {
                Toast.makeText(context, "بلیطت تمام شده؛ از فروشگاه بلیط بگیر.", Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(Modifier.fillMaxSize().background(ArenaBackground)) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color(0xFF091426), Color(0xFF07101E), ArenaBackground)
                )
            )
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (showOfflineChallenges) showOfflineChallenges = false else onNavigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = TextPrimary)
                    }
                    Column(Modifier.weight(1f).padding(start = 4.dp)) {
                        Text(if (showOfflineChallenges) "پنج چالش آفلاین" else "انتخاب حالت بازی", color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Black)
                        Text("وارد میدان شو؛ قهرمانی منتظر توست", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
            if (!showOfflineChallenges) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModeChoiceCard(
                        title = "بازی آنلاین",
                        subtitle = "رقابت با دوستان",
                        icon = Icons.Default.SportsKabaddi,
                        accent = TurquoiseSecondary,
                        onClick = onNavigateToOnline,
                        modifier = Modifier.weight(1f)
                    )
                    ModeChoiceCard(
                        title = "بازی آفلاین",
                        subtitle = "پنج چالش با هوش مصنوعی",
                        icon = Icons.Default.Casino,
                        accent = SkyBlue,
                        onClick = { showOfflineChallenges = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Card(
                    Modifier.fillMaxWidth().clickable(onClick = onNavigateToRewards).testTag("mode_daily_rewards"),
                    shape = RoundedCornerShape(17.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF092345)),
                    border = BorderStroke(1.dp, WarmYellow.copy(alpha = .52f))
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = WarmYellow, modifier = Modifier.size(28.dp))
                        Column(Modifier.weight(1f).padding(horizontal = 11.dp)) {
                            Text("جایزه‌های روزانه", color = WarmYellow, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            Text("هر روز جایزه بگیر و برای نبرد بعدی آماده شو", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
                        }
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = WarmYellow)
                    }
                }
            }
            }
            if (showOfflineChallenges) {
            item {
                Text(
                    "پنج چالش آفلاین",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp)
                )
            }
            items(challenges.take(5), key = { it.challengeId }) { challenge ->
                SelectionCard(challenge, ::play)
            }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Text("هر نبرد با یک بلیط شروع می‌شود • VIP بدون محدودیت", color = TextMuted, fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ModeChoiceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.975f else 1f,
        animationSpec = tween(durationMillis = 130),
        label = "mode_choice_press_scale"
    )
    Card(
        modifier = modifier
            .height(156.dp)
            .scale(cardScale)
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ) else Modifier
            )
            .testTag("mode_choice_" + title),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface),
        border = BorderStroke(1.2.dp, accent.copy(alpha = .65f))
    ) {
        Box(Modifier.fillMaxSize()) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(R.drawable.arena_hero_banner_1789948811846),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF06142B).copy(alpha = .35f),
                            Color(0xFF06142B).copy(alpha = .78f),
                            Color(0xFF06142B).copy(alpha = .97f)
                        )
                    )
                )
            )
            Column(
                Modifier.fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(26.dp))
                Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 7.dp))
                Text(subtitle, color = TextSecondary, fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 3.dp))
                if (onClick != null) {
                    Text("ورود به میدان  ›", color = WarmYellow, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 7.dp))
                } else {
                    Text("یک بازی را از پایین انتخاب کن", color = SkyBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 7.dp))
                }
            }
        }
    }
}

@Composable
private fun ModePill(title: String, subtitle: String, color: Color, onClick: (() -> Unit)?, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(15.dp))
            .background(color.copy(alpha = .10f))
            .border(1.dp, color.copy(alpha = .30f), RoundedCornerShape(15.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 9.dp, horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = TextMuted, fontSize = 8.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun SelectionCard(challenge: ChallengeItemEntity, onPlay: (String) -> Unit) {
    val meta = when (challenge.challengeId) {
        "memory" -> Meta("حافظه", "جفت‌های پنهان را پیدا کن.", MemoryChallengeColor, Icons.Default.Memory)
        "dice" -> Meta("دوئل تاس", "هوشمندانه‌تر از حریف تاس بریز.", DiceChallengeColor, Icons.Default.Casino)
        "rps" -> Meta("سنگ، کاغذ، قیچی", "حرکت حریف را بخوان.", PinkTertiary, Icons.Default.SportsKabaddi)
        "sentence" -> Meta("جمله‌ساز", "جمله‌ی درست را بساز.", SkyBlue, Icons.Default.TextFields)
        else -> Meta("نبرد کلمات", "قبل از تمام شدن زمان جواب را پیدا کن.", WordChallengeColor, Icons.Default.Spellcheck)
    }
    Card(
        Modifier.fillMaxWidth().clickable { onPlay(challenge.challengeId) },
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = .82f)),
        border = BorderStroke(1.dp, meta.color.copy(alpha = .34f))
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).clip(RoundedCornerShape(15.dp))
                    .background(meta.color.copy(alpha = .13f))
                    .border(1.dp, meta.color.copy(alpha = .32f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(meta.icon, null, tint = meta.color, modifier = Modifier.size(27.dp))
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(meta.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                Text(meta.subtitle, color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
                Row(Modifier.padding(top = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { i ->
                        Text("★", color = if (i < challenge.stars) WarmYellow else TextMuted.copy(alpha = .35f), fontSize = 10.sp)
                    }
                    Text("  رکورد " + challenge.highscore, color = TextMuted, fontSize = 9.sp)
                }
            }
            Box(Modifier.size(40.dp).clip(CircleShape).background(meta.color.copy(alpha = .14f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayArrow, null, tint = meta.color)
            }
        }
    }
}

private data class Meta(val title: String, val subtitle: String, val color: Color, val icon: ImageVector)
