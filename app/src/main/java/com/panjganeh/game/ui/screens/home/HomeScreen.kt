package com.panjganeh.game.ui.screens.home

import android.widget.Toast
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
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.panjganeh.game.data.local.entity.ChallengeItemEntity
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.components.TapsellBanner
import com.panjganeh.game.ui.theme.CardJoyfulGradient
import com.panjganeh.game.ui.theme.CreatorCardBorderGradient
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
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.VipCrownGradient
import com.panjganeh.game.ui.theme.VipGold
import com.panjganeh.game.ui.theme.WarmYellow
import com.panjganeh.game.ui.theme.WordChallengeColor
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userRepository: UserRepository,
    gameRepository: GameRepository,
    tapsellManager: TapsellManager,
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
    val currentTheme = LocalPanjganehTheme.current
    val currentLang = LocalAppLanguage.current

    val user by userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)
    val challenges by gameRepository.allChallenges.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by gameRepository.settings.collectAsStateWithLifecycle(initialValue = null)

    val isVip = vip?.isVip == true && (vip?.expireTimestamp == 0L || (vip?.expireTimestamp ?: 0L) > System.currentTimeMillis())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ArenaTopBar(
                user = user,
                vip = vip,
                title = if (currentLang == "en") "Panjganeh" else "پنجگانه",
                onCoinsClick = onNavigateToShop,
                onTicketsClick = onNavigateToShop,
                onProfileClick = onNavigateToProfile
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeNavButton(
                    title = if (currentLang == "en") "Shop" else "فروشگاه",
                    icon = Icons.Default.ShoppingCart,
                    tint = WarmYellow,
                    fontScale = fontScale,
                    onClick = onNavigateToShop,
                    tag = "nav_shop"
                )
                HomeNavButton(
                    title = if (currentLang == "en") "Rewards" else "جوایز",
                    icon = Icons.Default.CardGiftcard,
                    tint = PinkTertiary,
                    fontScale = fontScale,
                    onClick = onNavigateToRewards,
                    tag = "nav_rewards"
                )
                HomeNavButton(
                    title = if (currentLang == "en") "Leaderboard" else "برترین‌ها",
                    icon = Icons.Default.EmojiEvents,
                    tint = TurquoiseSecondary,
                    fontScale = fontScale,
                    onClick = onNavigateToLeaderboard,
                    tag = "nav_leaderboard"
                )
                HomeNavButton(
                    title = if (currentLang == "en") "Profile" else "پروفایل",
                    icon = Icons.Default.Person,
                    tint = PurplePrimary,
                    fontScale = fontScale,
                    onClick = onNavigateToProfile,
                    tag = "nav_profile"
                )
                HomeNavButton(
                    title = if (currentLang == "en") "Settings" else "تنظیمات",
                    icon = Icons.Default.Settings,
                    tint = SkyBlue,
                    fontScale = fontScale,
                    onClick = onNavigateToSettings,
                    tag = "nav_settings"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ═══════════════════════════════════════════════════════════
            // کارت تبلیغ جایزه‌دار تپسل (Rewarded Video)
            // ═══════════════════════════════════════════════════════════
            item {
                if (!isVip) {
                    RewardedAdCard(
                        fontScale = fontScale,
                        currentLang = currentLang,
                        onWatchClick = {
                            val activity = context as? android.app.Activity
                            if (activity != null) {
                                tapsellManager.showRewardedVideo(
                                    activity = activity,
                                    rewardCoins = 150,
                                    onRewarded = { coins ->
                                        scope.launch {
                                            userRepository.addCoins(coins)
                                            Toast.makeText(
                                                context,
                                                if (currentLang == "en") "+$coins coins added!" else "+$coins سکه به حساب شما اضافه شد!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    )
                }
            }

            // بنر ترویجی VIP
            item {
                if (!isVip) {
                    VipPromoBannerPanjganeh(fontScale = fontScale, onClick = onNavigateToShop)
                }
            }

            // کارت وضعیت هوش مصنوعی حریف
            item {
                CurrentOpponentBannerPanjganeh(
                    difficulty = settings?.aiDifficulty ?: "متوسط",
                    fontScale = fontScale,
                    lang = currentLang,
                    onSettingsClick = onNavigateToSettings
                )
            }

            // تیتر چالش‌ها
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = if (currentLang == "en") "5 Arena Challenges" else "چالش‌های پنجگانه بازی",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = currentTheme.primary,
                        fontSize = (16 * fontScale).sp
                    )
                    Text(
                        text = if (currentLang == "en") "Five Challenges, Five Battles, One Champion" else "پنج چالش، پنج نبرد، یک قهرمان",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (11 * fontScale).sp
                    )
                }
            }

            // لیست چالش‌ها
            items(challenges, key = { it.challengeId }) { challenge ->
                ChallengeCardPanjganeh(
                    challenge = challenge,
                    fontScale = fontScale,
                    lang = currentLang,
                    onPlayClick = {
                        scope.launch {
                            if (isVip) {
                                onStartChallenge(challenge.challengeId)
                            } else {
                                val hasTicket = userRepository.deductTickets(1)
                                if (hasTicket) {
                                    onStartChallenge(challenge.challengeId)
                                } else {
                                    Toast.makeText(
                                        context,
                                        if (currentLang == "en") "Out of tickets! Visit the shop." else "بلیط شما تمام شده! می‌توانید از فروشگاه بلیط تهیه کنید.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    onNavigateToShop()
                                }
                            }
                        }
                    }
                )
            }

            // ═══════════════════════════════════════════════════════════
            // بنر تبلیغاتی استاندارد تپسل
            // ═══════════════════════════════════════════════════════════
            item {
                if (!isVip) {
                    TapsellBanner(tapsellManager = tapsellManager)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * کارت تبلیغ جایزه‌دار تپسل
 */
@Composable
fun RewardedAdCard(
    fontScale: Float,
    currentLang: String,
    onWatchClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rewarded_ad_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x2610B981)),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF34D399)))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x3310B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.OndemandVideo,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (currentLang == "en") "Free Coins!" else "سکه طلا رایگان!",
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold,
                        fontSize = (14 * fontScale).sp
                    )
                    Text(
                        text = if (currentLang == "en") "Watch a video and get +150 coins" else "تماشای ویدیو و دریافت +۱۵۰ سکه",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (11 * fontScale).sp
                    )
                }
            }

            Button(
                onClick = onWatchClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("watch_ad_btn")
            ) {
                Text(
                    text = if (currentLang == "en") "Watch" else "تماشا",
                    fontWeight = FontWeight.Bold,
                    fontSize = (12 * fontScale).sp
                )
            }
        }
    }
}

@Composable
fun ChallengeCardPanjganeh(
    challenge: ChallengeItemEntity,
    fontScale: Float,
    lang: String,
    onPlayClick: () -> Unit
) {
    val iconVector: ImageVector = when (challenge.challengeId) {
        "word" -> Icons.Default.Spellcheck
        "memory" -> Icons.Default.Style
        "dice" -> Icons.Default.Casino
        "rps" -> Icons.Default.SportsKabaddi
        else -> Icons.Default.TextFields
    }

    val accentColor = when (challenge.challengeId) {
        "word" -> WordChallengeColor
        "memory" -> MemoryChallengeColor
        "dice" -> DiceChallengeColor
        "rps" -> RpsChallengeColor
        else -> SentenceChallengeColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("challenge_card_${challenge.challengeId}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(accentColor, accentColor.copy(alpha = 0.4f))
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(accentColor.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(accentColor.copy(alpha = 0.18f))
                                .border(2.dp, accentColor, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = challenge.nameFa,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = (16 * fontScale).sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 3.dp)
                            ) {
                                for (i in 1..3) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (challenge.stars >= i) WarmYellow else Color(0x33FFFFFF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == "en") "Record: ${challenge.highscore}" else "رکورد: ${challenge.highscore}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = (11 * fontScale).sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = challenge.descriptionFa,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = (12 * fontScale).sp,
                    lineHeight = (18 * fontScale).sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = TurquoiseSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (lang == "en") "Entry: 1 Ticket" else "ورود: ۱ بلیط",
                            color = TurquoiseSecondary,
                            fontSize = (12 * fontScale).sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Button(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("play_btn_${challenge.challengeId}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == "en") "Start Battle" else "شروع نبرد",
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * fontScale).sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VipPromoBannerPanjganeh(fontScale: Float, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("vip_promo_banner"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.5.dp,
            brush = VipCrownGradient
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0x26FDCB6E), Color(0x13FD79A8))))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(VipCrownGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = Color(0xFF1E152A),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "عضویت در نخبگان ویژه پنجگانه",
                        color = WarmYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = (14 * fontScale).sp
                    )
                    Text(
                        text = "حذف تبلیغات + پاداش ۲ برابری + بلیط نامحدود",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (11 * fontScale).sp
                    )
                }
            }
            Text(
                text = "ارتقا ›",
                color = WarmYellow,
                fontWeight = FontWeight.Bold,
                fontSize = (13 * fontScale).sp
            )
        }
    }
}

@Composable
fun CurrentOpponentBannerPanjganeh(
    difficulty: String,
    fontScale: Float,
    lang: String,
    onSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSettingsClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = Brush.horizontalGradient(listOf(PurplePrimary, TurquoiseSecondary))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = TurquoiseSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == "en") "AI Opponent Level: $difficulty" else "سطح هوش مصنوعی حریف: $difficulty",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = (12 * fontScale).sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = if (lang == "en") "Change Level ⚙️" else "تغییر سطح ⚙️",
                color = TurquoiseSecondary,
                fontSize = (11 * fontScale).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HomeNavButton(
    title: String,
    icon: ImageVector,
    tint: Color,
    fontScale: Float,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = title, color = tint, fontSize = (10 * fontScale).sp, fontWeight = FontWeight.Bold)
    }
}
