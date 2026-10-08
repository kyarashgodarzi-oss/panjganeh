package com.panjganeh.game.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.data.local.datastore.AppSettingsDataStore
import com.panjganeh.game.data.local.entity.ChallengeItemEntity
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.DiceChallengeColor
import com.panjganeh.game.ui.theme.LocalAppLanguage
import com.panjganeh.game.ui.theme.LocalFontScale
import com.panjganeh.game.ui.theme.MemoryChallengeColor
import com.panjganeh.game.ui.theme.PinkTertiary
import com.panjganeh.game.ui.theme.RpsChallengeColor
import com.panjganeh.game.ui.theme.SentenceChallengeColor
import com.panjganeh.game.ui.theme.SkyBlue
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.WarmYellow
import com.panjganeh.game.ui.theme.WordChallengeColor
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userRepository: UserRepository,
    gameRepository: GameRepository,
    tapsellManager: com.panjganeh.game.ads.TapsellManager,
    settingsDataStore: AppSettingsDataStore,
    onStartChallenge: (challengeId: String) -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToModes: () -> Unit
) {
    val user by userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)
    val challenges by gameRepository.allChallenges.collectAsStateWithLifecycle(initialValue = emptyList())
    val lang = LocalAppLanguage.current
    val scale = LocalFontScale.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val now = remember { mutableLongStateOf(System.currentTimeMillis()) }
    val isVip = vip?.isVip == true && (vip?.expireTimestamp == 0L || (vip?.expireTimestamp ?: 0L) > now.longValue)

    Scaffold(
        containerColor = ArenaBackground,
        bottomBar = {
            ArenaBottomBar(
                lang = lang,
                scale = scale,
                home = {},
                games = onNavigateToModes,
                shop = onNavigateToShop,
                profile = onNavigateToProfile
            )
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(ArenaBackground, Color(0xFF081426), Color(0xFF050914))
                )
            )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ArenaTopBar(
                        user = user,
                        vip = vip,
                        onCoinsClick = onNavigateToShop,
                        onTicketsClick = onNavigateToShop,
                        onProfileClick = onNavigateToProfile,
                        onSettingsClick = onNavigateToSettings
                    )
                }
                item {
                    HomeHero(
                        lang = lang,
                        level = user?.level ?: 1,
                        xp = user?.xp ?: 0,
                        onPlay = onNavigateToModes
                    )
                }
                item {
                    QuickModeRow(
                        lang = lang,
                        onOnline = onNavigateToModes,
                        onOffline = onNavigateToModes,
                        onDaily = onNavigateToRewards
                    )
                }
                item {
                    SectionTitle(
                        title = if (lang == "en") "CHOOSE YOUR BATTLE" else "انتخاب حالت بازی",
                        action = if (lang == "en") "ALL" else "همه",
                        onAction = onNavigateToModes
                    )
                }
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(challenges.take(5), key = { it.challengeId }) { challenge ->
                            HomeChallengeMiniCard(
                                challenge = challenge,
                                lang = lang,
                                onClick = {
                                    scope.launch {
                                        if (isVip || userRepository.deductTickets(1)) {
                                            onStartChallenge(challenge.challengeId)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                if (lang == "en") "No tickets left." else "بلیط بازی کافی نیست.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onNavigateToShop()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                item {
                    DailyChallengeStrip(
                        lang = lang,
                        onClick = onNavigateToRewards
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHero(lang: String, level: Int, xp: Int, onPlay: () -> Unit) {
    val progress = ((xp % 300) / 300f).coerceIn(0f, 1f)
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp).testTag("reference_home_hero"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.5.dp, TurquoiseSecondary.copy(alpha = 0.55f))
    ) {
        Box(Modifier.fillMaxWidth().height(285.dp)) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(com.panjganeh.game.R.drawable.arena_hero_banner_1789948811846),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF07111F).copy(alpha = 0.06f),
                            Color(0xFF07111F).copy(alpha = 0.34f),
                            Color(0xFF07111F).copy(alpha = 0.92f)
                        )
                    )
                )
            )
            Column(
                Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (lang == "en") "PANJGANEH" else "پنجگانه",
                    color = WarmYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    if (lang == "en") "FIVE CHALLENGES. ONE CHAMPION." else "۵ چالش، یک قهرمان",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Row(
                    Modifier.padding(top = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (lang == "en") "LEVEL $level" else "سطح $level",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier.width(100.dp).height(6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.16f))
                    ) {
                        Box(
                            Modifier.fillMaxWidth(progress).height(6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(WarmYellow, Color(0xFFFF8A00))))
                        )
                    }
                }
                Box(
                    Modifier.fillMaxWidth().padding(top = 9.dp).height(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Brush.horizontalGradient(listOf(WarmYellow, Color(0xFFFFA500))))
                        .clickable(onClick = onPlay)
                        .testTag("start_game_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (lang == "en") "START GAME" else "شروع بازی",
                            color = Color(0xFF241407),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Icon(Icons.Default.PlayArrow, null, tint = Color(0xFF241407), modifier = Modifier.padding(start = 5.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickModeRow(lang: String, onOnline: () -> Unit, onOffline: () -> Unit, onDaily: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        QuickMode(
            title = if (lang == "en") "ONLINE" else "آنلاین",
            subtitle = if (lang == "en") "Friends" else "دوستان",
            icon = Icons.Default.SportsKabaddi,
            tint = SkyBlue,
            modifier = Modifier.weight(1f),
            onClick = onOnline
        )
        QuickMode(
            title = if (lang == "en") "OFFLINE" else "آفلاین",
            subtitle = if (lang == "en") "vs AI" else "با هوش مصنوعی",
            icon = Icons.Default.Casino,
            tint = TurquoiseSecondary,
            modifier = Modifier.weight(1f),
            onClick = onOffline
        )
        QuickMode(
            title = if (lang == "en") "DAILY" else "روزانه",
            subtitle = if (lang == "en") "Rewards" else "جایزه",
            icon = Icons.Default.CardGiftcard,
            tint = PinkTertiary,
            modifier = Modifier.weight(1f),
            onClick = onDaily
        )
    }
}

@Composable
private fun QuickMode(title: String, subtitle: String, icon: ImageVector, tint: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.10f)),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.48f))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
            Text(title, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp))
            Text(subtitle, color = TextMuted, fontSize = 8.sp, maxLines = 1)
        }
    }
}

@Composable
private fun SectionTitle(title: String, action: String, onAction: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(4.dp).height(22.dp).clip(RoundedCornerShape(4.dp)).background(TurquoiseSecondary))
        Text(title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
        Text(action, color = TurquoiseSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.clickable(onClick = onAction))
    }
}

@Composable
private fun HomeChallengeMiniCard(challenge: ChallengeItemEntity, lang: String, onClick: () -> Unit) {
    val meta = challengeMeta(challenge.challengeId, lang)
    Card(
        modifier = Modifier.width(70.dp).height(78.dp).clickable(onClick = onClick).testTag("home_challenge_\${challenge.challengeId}"),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = 0.95f)),
        border = BorderStroke(1.dp, meta.color.copy(alpha = 0.65f))
    ) {
        Column(Modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(10.dp))
                    .background(meta.color.copy(alpha = 0.16f))
                    .border(1.dp, meta.color.copy(alpha = 0.42f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(meta.icon, null, tint = meta.color, modifier = Modifier.size(20.dp))
            }
            Text(meta.title, color = TextPrimary, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp), maxLines = 1)
        }
    }
}

@Composable
private fun DailyChallengeStrip(lang: String, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp).clickable(onClick = onClick).testTag("daily_challenge_home"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18102A)),
        border = BorderStroke(1.dp, PinkTertiary.copy(alpha = 0.42f))
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(12.dp))
                    .background(PinkTertiary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CardGiftcard, null, tint = PinkTertiary, modifier = Modifier.size(24.dp))
            }
            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text(if (lang == "en") "DAILY CHALLENGE" else "چالش روزانه", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Black)
                Text(if (lang == "en") "Special reward • Claim now" else "هر روز یک جایزه ویژه • دریافت کن", color = TextSecondary, fontSize = 9.sp)
            }
            Box(
                Modifier.clip(RoundedCornerShape(11.dp))
                    .background(WarmYellow)
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(if (lang == "en") "CLAIM" else "دریافت", color = Color(0xFF241407), fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

private data class ChallengeMeta(val title: String, val color: Color, val icon: ImageVector)

private fun challengeMeta(id: String, lang: String): ChallengeMeta = when (id) {
    "word" -> ChallengeMeta(if (lang == "en") "WORD" else "کلمه", WordChallengeColor, Icons.Default.TextFields)
    "memory" -> ChallengeMeta(if (lang == "en") "MEMORY" else "حافظه", MemoryChallengeColor, Icons.Default.Memory)
    "dice" -> ChallengeMeta(if (lang == "en") "DICE" else "تاس", DiceChallengeColor, Icons.Default.Casino)
    "rps" -> ChallengeMeta(if (lang == "en") "RPS" else "قیچی", RpsChallengeColor, Icons.Default.SportsKabaddi)
    else -> ChallengeMeta(if (lang == "en") "SENTENCE" else "جمله", SentenceChallengeColor, Icons.Default.Spellcheck)
}

@Composable
private fun ArenaBottomBar(lang: String, scale: Float, home: () -> Unit, games: () -> Unit, shop: () -> Unit, profile: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Color(0xFF06101D)).border(1.dp, TurquoiseSecondary.copy(alpha = 0.14f)).padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem(if (lang == "en") "Home" else "خانه", Icons.Default.Person, TurquoiseSecondary, scale, home, true)
        BottomItem(if (lang == "en") "Games" else "بازی‌ها", Icons.Default.Casino, WarmYellow, scale, games)
        BottomItem(if (lang == "en") "Shop" else "فروشگاه", Icons.Default.ShoppingCart, SkyBlue, scale, shop)
        BottomItem(if (lang == "en") "Profile" else "پروفایل", Icons.Default.Person, PinkTertiary, scale, profile)
    }
}

@Composable
private fun BottomItem(title: String, icon: ImageVector, color: Color, scale: Float, onClick: () -> Unit, active: Boolean = false) {
    Column(
        Modifier.clip(RoundedCornerShape(13.dp)).clickable(onClick = onClick).padding(horizontal = 13.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = if (active) color else TextMuted, modifier = Modifier.size(21.dp))
        Text(title, color = if (active) color else TextMuted, fontSize = (8 * scale).sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
    }
}
