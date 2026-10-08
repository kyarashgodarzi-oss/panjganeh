package com.panjganeh.game.ui.screens.home

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.data.local.datastore.AppSettingsDataStore
import com.panjganeh.game.data.local.entity.ChallengeItemEntity
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.components.TapsellBanner
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.DiceChallengeColor
import com.panjganeh.game.ui.theme.LocalAppLanguage
import com.panjganeh.game.ui.theme.LocalFontScale
import com.panjganeh.game.ui.theme.LocalPanjganehTheme
import com.panjganeh.game.ui.theme.MemoryChallengeColor
import com.panjganeh.game.ui.theme.PinkTertiary
import com.panjganeh.game.ui.theme.PurplePrimary
import com.panjganeh.game.ui.theme.RpsChallengeColor
import com.panjganeh.game.ui.theme.SentenceChallengeColor
import com.panjganeh.game.ui.theme.SkyBlue
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.VipGold
import com.panjganeh.game.ui.theme.WarmYellow
import com.panjganeh.game.ui.theme.WordChallengeColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userRepository: UserRepository,
    gameRepository: GameRepository,
    tapsellManager: TapsellManager,
    settingsDataStore: AppSettingsDataStore,
    onStartChallenge: (challengeId: String) -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fontScale = LocalFontScale.current
    val theme = LocalPanjganehTheme.current
    val lang = LocalAppLanguage.current
    val user by userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)
    val challenges by gameRepository.allChallenges.collectAsStateWithLifecycle(initialValue = emptyList())
    val appSettings by settingsDataStore.appSettingsFlow.collectAsStateWithLifecycle(initialValue = null)

    val isVip = vip?.isVip == true &&
        (vip?.expireTimestamp == 0L || (vip?.expireTimestamp ?: 0L) > System.currentTimeMillis())

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            now = System.currentTimeMillis()
        }
    }

    val lastClaim = appSettings?.lastTwoHourRewardClaim ?: 0L
    val remaining = (7_200_000L - (now - lastClaim)).coerceAtLeast(0L)
    val rewardReady = lastClaim == 0L || remaining == 0L

    Scaffold(
        containerColor = ArenaBackground,
        topBar = {
            ArenaTopBar(
                user = user,
                vip = vip,
                title = if (lang == "en") "PANJGANEH" else "پنجگانه",
                onCoinsClick = onNavigateToShop,
                onTicketsClick = onNavigateToShop,
                onProfileClick = onNavigateToProfile
            )
        },
        bottomBar = {
            ArenaBottomBar(lang, fontScale, onNavigateToShop, onNavigateToRewards, onNavigateToLeaderboard, onNavigateToProfile, onNavigateToSettings)
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(ArenaBackground, Color(0xFF0A1022), Color(0xFF080B17)))
            )
        ) {
            Box(Modifier.size(220.dp).align(Alignment.TopStart).padding(start = 0.dp).clip(CircleShape).background(PurplePrimary.copy(alpha = 0.08f)))
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                item {
                    ArenaHeroCard(
                        level = user?.level ?: 1,
                        xp = user?.xp ?: 0,
                        wins = user?.wins ?: 0,
                        tickets = user?.tickets ?: 0,
                        lang = lang,
                        accent = theme.primary,
                        onPlay = { challenges.firstOrNull()?.let { onStartChallenge(it.challengeId) } }
                    )
                }
                item {
                    QuickStats(
                        coins = user?.coins ?: 0,
                        tickets = user?.tickets ?: 0,
                        wins = user?.wins ?: 0,
                        best = challenges.maxOfOrNull { it.highscore } ?: 0,
                        lang = lang,
                        scale = fontScale
                    )
                }
                item {
                    Column(Modifier.padding(top = 4.dp)) {
                        Text(
                            if (lang == "en") "CHOOSE YOUR BATTLE" else "انتخاب نبرد",
                            color = TurquoiseSecondary,
                            fontSize = (10 * fontScale).sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            if (lang == "en") "Five arenas. One champion." else "پنج میدان؛ یک قهرمان",
                            color = TextPrimary,
                            fontSize = (21 * fontScale).sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                        Text(
                            if (lang == "en") "Pick a challenge and beat your best score." else "یک چالش را انتخاب کن و رکوردت را بشکن.",
                            color = TextSecondary,
                            fontSize = (11 * fontScale).sp,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
                items(challenges, key = { it.challengeId }) { challenge ->
                    ArenaChallengeCard(challenge, lang, fontScale) {
                        scope.launch {
                            if (isVip || userRepository.deductTickets(1)) {
                                onStartChallenge(challenge.challengeId)
                            } else {
                                Toast.makeText(
                                    context,
                                    if (lang == "en") "No tickets left. Visit the shop." else "بلیطت تمام شده؛ از فروشگاه بلیط بگیر.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onNavigateToShop()
                            }
                        }
                    }
                }
                if (!isVip) {
                    item {
                        RewardStrip(
                            ready = rewardReady,
                            remaining = remaining,
                            lang = lang,
                            onClick = {
                                val activity = context as? Activity ?: return@RewardStrip
                                tapsellManager.showRewardedVideo(
                                    activity = activity,
                                    rewardCoins = if (rewardReady) 200 else 150,
                                    onRewarded = { coins ->
                                        scope.launch {
                                            userRepository.addCoins(coins)
                                            if (rewardReady) {
                                                settingsDataStore.setLastTwoHourRewardClaim(System.currentTimeMillis())
                                                now = System.currentTimeMillis()
                                            }
                                            Toast.makeText(
                                                context,
                                                if (lang == "en") "+" + coins + " coins added!" else "+" + coins + " سکه به موجودی اضافه شد!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                                )
                            }
                        )
                    }
                    item { VipCard(onClick = onNavigateToShop, lang = lang) }
                    item { TapsellBanner(tapsellManager = tapsellManager) }
                }
                item { Spacer(Modifier.height(10.dp)) }
            }
        }
    }
}

@Composable
private fun ArenaHeroCard(
    level: Int,
    xp: Int,
    wins: Int,
    tickets: Int,
    lang: String,
    accent: Color,
    onPlay: () -> Unit
) {
    val progress = ((xp % 1000) / 1000f).coerceIn(0f, 1f)
    Card(
        Modifier.fillMaxWidth().testTag("arena_hero_card"),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.42f))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF251B5E), Color(0xFF171A45), Color(0xFF0D3B43))))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(TurquoiseSecondary))
                Text(
                    if (lang == "en") "  THE ARENA" else "  آرِنای پنجگانه",
                    color = TurquoiseSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.7.sp
                )
            }
            Text(
                if (lang == "en") "Five battles." else "پنج نبرد.",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 9.dp)
            )
            Text(
                if (lang == "en") "One champion." else "یک قهرمان.",
                color = VipGold,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
            Row(
                Modifier.fillMaxWidth().padding(top = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(Brush.linearGradient(listOf(VipGold, WarmYellow))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(level.toString(), color = Color(0xFF24180A), fontWeight = FontWeight.Black, fontSize = 17.sp)
                }
                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(
                        if (lang == "en") "LEVEL " + level + " • " + wins + " WINS" else "سطح " + level + " • " + wins + " برد",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        Modifier.fillMaxWidth().padding(top = 6.dp).height(7.dp).clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                    ) {
                        Box(
                            Modifier.fillMaxWidth(progress).height(7.dp).clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(PurplePrimary, TurquoiseSecondary)))
                        )
                    }
                    Text(
                        if (lang == "en") (xp % 1000).toString() + " / 1000 XP" else (xp % 1000).toString() + " از ۱۰۰۰ XP",
                        color = TextMuted,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Button(
                onClick = onPlay,
                Modifier.fillMaxWidth().height(54.dp).padding(top = 10.dp).testTag("start_run_button"),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.horizontalGradient(listOf(PurplePrimary, Color(0xFF5B4BE0))),
                        RoundedCornerShape(17.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                        Text(
                            if (lang == "en") "  START THE RUN" else "  شروع نبرد",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Text(
                if (lang == "en") tickets.toString() + " tickets available" else tickets.toString() + " بلیط آماده‌ی بازی",
                color = TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 7.dp)
            )
        }
    }
}

@Composable
private fun QuickStats(coins: Int, tickets: Int, wins: Int, best: Int, lang: String, scale: Float) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Stat(Icons.Default.Star, WarmYellow, coins.toString(), if (lang == "en") "COINS" else "سکه", Modifier.weight(1f), scale)
        Stat(Icons.Default.ConfirmationNumber, PinkTertiary, tickets.toString(), if (lang == "en") "TICKETS" else "بلیط", Modifier.weight(1f), scale)
        Stat(Icons.Default.EmojiEvents, TurquoiseSecondary, wins.toString(), if (lang == "en") "WINS" else "برد", Modifier.weight(1f), scale)
        Stat(Icons.Default.Timer, SkyBlue, best.toString(), if (lang == "en") "BEST" else "رکورد", Modifier.weight(1f), scale)
    }
}

@Composable
private fun Stat(icon: ImageVector, color: Color, value: String, label: String, modifier: Modifier, scale: Float) {
    Card(
        modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = 0.96f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.20f))
    ) {
        Column(Modifier.padding(vertical = 10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            Text(value, color = TextPrimary, fontSize = (14 * scale).sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 2.dp))
            Text(label, color = TextMuted, fontSize = (8 * scale).sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ArenaChallengeCard(challenge: ChallengeItemEntity, lang: String, scale: Float, onPlay: () -> Unit) {
    val meta = challengeMeta(challenge.challengeId, lang)
    Card(
        Modifier.fillMaxWidth().testTag("challenge_" + challenge.challengeId).clickable(onClick = onPlay),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = 0.97f)),
        border = BorderStroke(1.dp, meta.color.copy(alpha = 0.30f))
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).clip(RoundedCornerShape(17.dp))
                    .background(meta.color.copy(alpha = 0.14f))
                    .border(1.dp, meta.color.copy(alpha = 0.35f), RoundedCornerShape(17.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(meta.icon, null, tint = meta.color, modifier = Modifier.size(27.dp))
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(meta.title, color = TextPrimary, fontSize = (16 * scale).sp, fontWeight = FontWeight.Black)
                Text(
                    if (lang == "en") meta.subtitle else challenge.descriptionFa,
                    color = TextSecondary,
                    fontSize = (10 * scale).sp,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 5.dp)) {
                    repeat(3) { i ->
                        Icon(Icons.Default.Star, null, tint = if (i < challenge.stars) WarmYellow else TextMuted.copy(alpha = 0.35f), modifier = Modifier.size(12.dp))
                    }
                    Text("  " + challenge.highscore, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(Modifier.size(38.dp).clip(CircleShape).background(meta.color.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayArrow, null, tint = meta.color, modifier = Modifier.size(22.dp))
            }
        }
    }
}

private data class ChallengeMeta(val title: String, val subtitle: String, val color: Color, val icon: ImageVector)

private fun challengeMeta(id: String, lang: String): ChallengeMeta = when (id) {
    "memory" -> ChallengeMeta(if (lang == "en") "MEMORY" else "حافظه", if (lang == "en") "Match the hidden pairs." else "جفت‌های پنهان را پیدا کن.", MemoryChallengeColor, Icons.Default.Memory)
    "dice" -> ChallengeMeta(if (lang == "en") "DICE DUEL" else "دوئل تاس", if (lang == "en") "Roll smarter than the AI." else "هوشمندانه‌تر از حریف تاس بریز.", DiceChallengeColor, Icons.Default.Casino)
    "rps" -> ChallengeMeta(if (lang == "en") "RPS ARENA" else "سنگ، کاغذ، قیچی", if (lang == "en") "Read your opponent." else "حرکت حریف را بخوان.", RpsChallengeColor, Icons.Default.SportsKabaddi)
    "sentence" -> ChallengeMeta(if (lang == "en") "SENTENCE" else "جمله‌ساز", if (lang == "en") "Build the perfect sentence." else "جمله‌ی درست را بساز.", SentenceChallengeColor, Icons.Default.TextFields)
    else -> ChallengeMeta(if (lang == "en") "WORD BATTLE" else "نبرد کلمات", if (lang == "en") "Find the answer before time runs out." else "قبل از تمام شدن زمان جواب را پیدا کن.", WordChallengeColor, Icons.Default.Spellcheck)
}

@Composable
private fun RewardStrip(ready: Boolean, remaining: Long, lang: String, onClick: () -> Unit) {
    val hours = remaining / 3_600_000
    val minutes = (remaining / 60_000) % 60
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101C2A)),
        border = BorderStroke(1.dp, TurquoiseSecondary.copy(alpha = 0.24f))
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(TurquoiseSecondary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CardGiftcard, null, tint = TurquoiseSecondary)
            }
            Column(Modifier.weight(1f).padding(horizontal = 11.dp)) {
                Text(
                    if (ready) (if (lang == "en") "FREE COINS READY" else "جایزه‌ی رایگان آماده است") else (if (lang == "en") "NEXT REWARD" else "جایزه‌ی بعدی"),
                    color = TurquoiseSecondary,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
                Text(
                    if (ready) (if (lang == "en") "Watch a short video • +200 coins" else "یک ویدئو ببین • +۲۰۰ سکه") else
                        String.format(if (lang == "en") "%02dh %02dm remaining" else "%02dساعت %02dدقیقه باقی‌مانده", hours, minutes),
                    color = TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Icon(Icons.Default.PlayArrow, null, tint = TurquoiseSecondary)
        }
    }
}

@Composable
private fun VipCard(onClick: () -> Unit, lang: String) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF201B0E)),
        border = BorderStroke(1.dp, VipGold.copy(alpha = 0.32f))
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(45.dp).clip(CircleShape).background(VipGold.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.WorkspacePremium, null, tint = VipGold, modifier = Modifier.size(26.dp))
            }
            Column(Modifier.weight(1f).padding(horizontal = 11.dp)) {
                Text(if (lang == "en") "GO VIP" else "عضویت VIP", color = VipGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Text(if (lang == "en") "Unlimited battles + exclusive perks" else "نبرد نامحدود + مزایای ویژه", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Icon(Icons.Default.PlayArrow, null, tint = VipGold)
        }
    }
}

@Composable
private fun ArenaBottomBar(
    lang: String,
    scale: Float,
    shop: () -> Unit,
    rewards: () -> Unit,
    rank: () -> Unit,
    profile: () -> Unit,
    settings: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().background(Color(0xFF0A0F20)).border(1.dp, Color.White.copy(alpha = 0.06f)).padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem(if (lang == "en") "Shop" else "فروشگاه", Icons.Default.ShoppingCart, WarmYellow, scale, shop)
        BottomItem(if (lang == "en") "Rewards" else "جوایز", Icons.Default.CardGiftcard, PinkTertiary, scale, rewards)
        BottomItem(if (lang == "en") "Rank" else "رتبه‌ها", Icons.Default.EmojiEvents, TurquoiseSecondary, scale, rank)
        BottomItem(if (lang == "en") "Profile" else "پروفایل", Icons.Default.Person, PurplePrimary, scale, profile)
        BottomItem(if (lang == "en") "More" else "بیشتر", Icons.Default.Settings, SkyBlue, scale, settings)
    }
}

@Composable
private fun BottomItem(title: String, icon: ImageVector, color: Color, scale: Float, onClick: () -> Unit) {
    Column(
        Modifier.clip(RoundedCornerShape(13.dp)).clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Text(title, color = TextMuted, fontSize = (8 * scale).sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
    }
}
