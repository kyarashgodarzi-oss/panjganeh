package com.panjganeh.game.ui.screens.home

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.R
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.data.local.datastore.AppSettingsDataStore
import com.panjganeh.game.data.local.entity.ChallengeItemEntity
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaReferencePrimaryButton
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
    onStartChallenge: (String) -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToOnline: () -> Unit,
    onNavigateToModes: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fontScale = LocalFontScale.current
    val lang = LocalAppLanguage.current
    LocalPanjganehTheme.current

    val user by userRepository.userProfile
        .collectAsStateWithLifecycle(initialValue = null)

    val vip by userRepository.vipState
        .collectAsStateWithLifecycle(initialValue = null)

    val settings by settingsDataStore.appSettingsFlow
        .collectAsStateWithLifecycle(initialValue = null)


    var now by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30000)
            now = System.currentTimeMillis()
        }
    }


    val lastReward = settings?.lastTwoHourRewardClaim ?: 0L

    val remaining =
        (7200000L - (now - lastReward))
            .coerceAtLeast(0)

    val rewardReady =
        lastReward == 0L || remaining == 0L


    Scaffold(
        containerColor = ArenaBackground,
        bottomBar = {
            ArenaBottomBar(
                lang = lang,
                scale = fontScale,
                shop = onNavigateToShop,
                rank = onNavigateToLeaderboard,
                profile = onNavigateToProfile,
                game = onNavigateToModes
            )
        }
    ) { padding ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            ArenaBackground,
                            Color(0xFF0A1022),
                            Color(0xFF080B17)
                        )
                    )
                )
        ) {


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 10.dp
                ),

                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {


                item {
                    ReferenceHeader(
                        username =
                        user?.username
                            ?: if (lang == "en")
                                "Panjganeh Player"
                            else
                                "قهرمان پنجگانه",

                        level = user?.level ?: 1,
                        coins = user?.coins ?: 0,
                        tickets = user?.tickets ?: 0,

                        onProfile = onNavigateToProfile,
                        onSettings = onNavigateToSettings,
                        onShop = onNavigateToShop,

                        lang = lang,
                        scale = fontScale
                    )
                }


                item {
                    ReferenceHero(
                        level = user?.level ?: 1,
                        xp = user?.xp ?: 0,
                        lang = lang,
                        onPlay = onNavigateToModes
                    )
                }


                item {
                    ReferenceModeRow(
                        lang = lang,
                        scale = fontScale,
                        onOnline = onNavigateToOnline,
                        onOffline = onNavigateToModes
                    )
                }


                item {

                    RewardCard(
                        lang = lang,
                        rewardReady = rewardReady,
                        onClick = {

                            val activity =
                                context as? Activity

                            activity?.let {

                                tapsellManager.showRewardedVideo(
                                    activity = it,
                                    rewardCoins =
                                    if (rewardReady)
                                        200
                                    else
                                        150,

                                    onRewarded = { coins ->

                                        scope.launch {

                                            userRepository.addCoins(coins)

                                            if (rewardReady) {

                                                settingsDataStore
                                                    .setLastTwoHourRewardClaim(
                                                        System.currentTimeMillis()
                                                    )

                                                now =
                                                    System.currentTimeMillis()
                                            }


                                            Toast.makeText(
                                                context,
                                                "+$coins coins",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },

                                    onError = { msg ->

                                        Toast.makeText(
                                            context,
                                            msg,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun ReferenceHeader(
    username: String,
    level: Int,
    coins: Int,
    tickets: Int,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onShop: () -> Unit,
    lang: String,
    scale: Float
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            TurquoiseSecondary,
                            PurplePrimary
                        )
                    )
                )
                .border(
                    2.dp,
                    WarmYellow.copy(alpha = 0.8f),
                    CircleShape
                )
                .clickable(onClick = onProfile),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {

            Text(
                text = username,
                color = TextPrimary,
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text =
                if (lang == "en")
                    "Level $level • Ready to play"
                else
                    "سطح $level • آماده‌ی بازی",

                color = TextSecondary,
                fontSize = (9 * scale).sp
            )
        }


        CurrencyChip(
            value = tickets.toString(),
            icon = Icons.Default.ConfirmationNumber,
            color = TurquoiseSecondary,
            onClick = onShop
        )


        Spacer(
            modifier = Modifier.width(5.dp)
        )


        CurrencyChip(
            value = coins.toString(),
            icon = Icons.Default.Star,
            color = WarmYellow,
            onClick = onShop
        )


        IconButton(
            onClick = onSettings,
            modifier = Modifier.size(38.dp)
        ) {

            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}



@Composable
private fun CurrencyChip(
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .border(
                1.dp,
                color.copy(alpha = 0.35f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 7.dp,
                vertical = 6.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(15.dp)
        )


        Text(
            value,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(start = 3.dp)
        )
    }
}



@Composable
private fun ReferenceHero(
    level: Int,
    xp: Int,
    lang: String,
    onPlay: () -> Unit
) {

    val animation =
        rememberInfiniteTransition(
            label = "hero"
        )


    val offset by animation.animateFloat(
        initialValue = -4f,
        targetValue = 4f,

        animationSpec =
        infiniteRepeatable(
            tween(1800),
            RepeatMode.Reverse
        ),

        label = "hero_move"
    )


    val progress =
        ((xp % 1000) / 1000f)
            .coerceIn(0f, 1f)



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reference_hero"),

        shape = RoundedCornerShape(22.dp),

        colors =
        CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),

        border =
        BorderStroke(
            1.dp,
            WarmYellow.copy(alpha = 0.5f)
        )
    ) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {


            Image(

                painter =
                painterResource(
                    R.drawable.panjganeh_hero_character
                ),

                contentDescription =
                if (lang == "en")
                    "Hero"
                else
                    "قهرمان",

                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(205.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = offset.dp),

                contentScale =
                ContentScale.Fit
            )



            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFF07101E)
                            )
                        )
                    )
            )



            Column(
                modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(18.dp),

                horizontalAlignment =
                Alignment.CenterHorizontally
            ) {


                Text(
                    text =
                    if (lang == "en")
                        "PANJGANEH"
                    else
                        "پنجگانه",

                    color = WarmYellow,

                    fontSize = 19.sp,

                    fontWeight =
                    FontWeight.Black
                )



                Text(
                    text =
                    if (lang == "en")
                        "FIVE CHALLENGES. ONE CHAMPION."
                    else
                        "۵ چالش؛ یک قهرمان",

                    color = Color.White,

                    fontSize = 21.sp,

                    fontWeight =
                    FontWeight.Black,

                    textAlign =
                    TextAlign.Center
                )



                Box(
                    modifier =
                    Modifier
                        .padding(top = 8.dp)
                        .width(120.dp)
                        .height(7.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            Color.White.copy(alpha = .15f)
                        )
                ) {

                    Box(
                        modifier =
                        Modifier
                            .fillMaxWidth(progress)
                            .height(7.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        WarmYellow,
                                        Color(0xFFFF8A00)
                                    )
                                )
                            )
                    )
                }



                ArenaReferencePrimaryButton(

                    text =
                    if (lang == "en")
                        "START GAME"
                    else
                        "شروع بازی",

                    onClick = onPlay,

                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),

                    testTag = "start_game_button"
                )
            }
        }
    }
}
@Composable
private fun ReferenceModeRow(
    lang: String,
    scale: Float,
    onOnline: () -> Unit,
    onOffline: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        ModeTile(
            title =
            if (lang == "en")
                "ONLINE"
            else
                "آنلاین",

            subtitle =
            if (lang == "en")
                "Coming soon"
            else
                "به‌زودی",

            icon = Icons.Default.SportsKabaddi,

            color = Color(0xFF35B7FF),

            scale = scale,

            modifier = Modifier.weight(1f),

            onClick = onOnline
        )


        ModeTile(
            title =
            if (lang == "en")
                "OFFLINE"
            else
                "آفلاین",

            subtitle =
            if (lang == "en")
                "Play vs AI"
            else
                "بازی با هوش مصنوعی",

            icon = Icons.Default.Casino,

            color = TurquoiseSecondary,

            scale = scale,

            modifier = Modifier.weight(1f),

            onClick = onOffline
        )
    }
}



@Composable
private fun ModeTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    scale: Float,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Card(

        modifier =
        modifier.clickable(onClick = onClick),

        shape =
        RoundedCornerShape(20.dp),

        colors =
        CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),

        border =
        BorderStroke(
            1.dp,
            color.copy(alpha = .7f)
        )
    ) {


        Column(

            modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            color.copy(alpha = .95f),
                            color.copy(alpha = .65f)
                        )
                    )
                )
                .padding(
                    vertical = 12.dp,
                    horizontal = 8.dp
                ),

            horizontalAlignment =
            Alignment.CenterHorizontally
        ) {


            Box(

                modifier =
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = .18f)
                    ),

                contentAlignment =
                Alignment.Center
            ) {

                Icon(

                    icon,

                    contentDescription = null,

                    tint = Color.White,

                    modifier =
                    Modifier.size(23.dp)
                )
            }



            Text(

                text = title,

                color = Color.White,

                fontSize = (12 * scale).sp,

                fontWeight = FontWeight.Black,

                modifier =
                Modifier.padding(top = 5.dp)
            )



            Text(

                text = subtitle,

                color =
                Color.White.copy(alpha = .85f),

                fontSize = (8 * scale).sp,

                maxLines = 1
            )
        }
    }
}




@Composable
private fun RewardCard(
    lang: String,
    rewardReady: Boolean,
    onClick: () -> Unit
) {

    Card(

        modifier =
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("reward_card"),

        shape =
        RoundedCornerShape(18.dp),

        colors =
        CardDefaults.cardColors(
            containerColor = Color(0xFF0A2140)
        ),

        border =
        BorderStroke(
            1.dp,
            WarmYellow.copy(alpha = .7f)
        )
    ) {


        Row(

            modifier =
            Modifier.padding(13.dp),

            verticalAlignment =
            Alignment.CenterVertically
        ) {


            Box(

                modifier =
                Modifier
                    .size(45.dp)
                    .clip(
                        RoundedCornerShape(14.dp)
                    )
                    .background(
                        Brush.linearGradient(
                            listOf(
                                WarmYellow,
                                Color(0xFFE64AD4)
                            )
                        )
                    ),

                contentAlignment =
                Alignment.Center
            ) {

                Icon(

                    Icons.Default.CardGiftcard,

                    contentDescription = null,

                    tint = Color.Black
                )
            }



            Column(

                modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)

            ) {

                Text(

                    text =
                    if (rewardReady)

                        if (lang == "en")
                            "FREE REWARD READY"
                        else
                            "جایزه آماده است"

                    else

                        if (lang == "en")
                            "NEXT REWARD"
                        else
                            "جایزه بعدی",

                    color = WarmYellow,

                    fontWeight = FontWeight.Black,

                    fontSize = 12.sp
                )



                Text(

                    text =
                    if (lang == "en")
                        "Watch video and earn coins"
                    else
                        "ویدئو ببین و سکه بگیر",

                    color = TextSecondary,

                    fontSize = 10.sp
                )
            }



            Icon(

                Icons.Default.PlayArrow,

                contentDescription = null,

                tint = WarmYellow
            )
        }
    }
}
@Composable
private fun ArenaChallengeCard(
    challenge: ChallengeItemEntity,
    lang: String,
    scale: Float,
    onPlay: () -> Unit
) {

    val meta =
        challengeMeta(
            challenge.challengeId,
            lang
        )


    Card(

        modifier =
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
            .testTag(
                "challenge_${challenge.challengeId}"
            ),

        shape =
        RoundedCornerShape(17.dp),

        colors =
        CardDefaults.cardColors(
            containerColor =
            ArenaSurface.copy(alpha = .9f)
        ),

        border =
        BorderStroke(
            1.dp,
            meta.color.copy(alpha = .35f)
        )
    ) {


        Row(

            modifier =
            Modifier
                .fillMaxWidth()
                .padding(10.dp),

            verticalAlignment =
            Alignment.CenterVertically
        ) {


            Box(

                modifier =
                Modifier
                    .size(44.dp)
                    .clip(
                        RoundedCornerShape(14.dp)
                    )
                    .background(
                        meta.color.copy(alpha = .15f)
                    ),

                contentAlignment =
                Alignment.Center
            ) {

                Icon(

                    meta.icon,

                    contentDescription = null,

                    tint = meta.color
                )
            }



            Column(

                modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)

            ) {


                Text(

                    text = meta.title,

                    color = TextPrimary,

                    fontSize = (13 * scale).sp,

                    fontWeight = FontWeight.Black
                )



                Text(

                    text =
                    if (lang == "en")
                        meta.subtitle
                    else
                        challenge.descriptionFa,

                    color = TextSecondary,

                    fontSize = (9 * scale).sp,

                    maxLines = 1
                )



                Row(

                    modifier =
                    Modifier.padding(top = 5.dp),

                    verticalAlignment =
                    Alignment.CenterVertically
                ) {


                    repeat(3) { index ->

                        Icon(

                            Icons.Default.Star,

                            contentDescription = null,

                            tint =
                            if (index < challenge.stars)
                                WarmYellow
                            else
                                TextMuted.copy(alpha = .3f),

                            modifier =
                            Modifier.size(12.dp)
                        )
                    }


                    Text(

                        text =
                        "  ${challenge.highscore}",

                        color = TextMuted,

                        fontSize = 9.sp
                    )
                }
            }



            Icon(

                Icons.Default.PlayArrow,

                contentDescription = null,

                tint = meta.color
            )
        }
    }
}




private data class ChallengeMeta(
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: ImageVector
)



private fun challengeMeta(
    id: String,
    lang: String
): ChallengeMeta {

    return when(id) {


        "memory" ->

            ChallengeMeta(
                if(lang=="en")
                    "MEMORY"
                else
                    "حافظه",

                if(lang=="en")
                    "Match hidden pairs"
                else
                    "جفت‌ها را پیدا کن",

                MemoryChallengeColor,

                Icons.Default.Memory
            )


        "dice" ->

            ChallengeMeta(
                if(lang=="en")
                    "DICE DUEL"
                else
                    "دوئل تاس",

                if(lang=="en")
                    "Beat the AI"
                else
                    "حریف را شکست بده",

                DiceChallengeColor,

                Icons.Default.Casino
            )


        "rps" ->

            ChallengeMeta(
                if(lang=="en")
                    "RPS ARENA"
                else
                    "سنگ کاغذ قیچی",

                if(lang=="en")
                    "Read opponent"
                else
                    "حرکت حریف را بخوان",

                RpsChallengeColor,

                Icons.Default.SportsKabaddi
            )


        "sentence" ->

            ChallengeMeta(
                if(lang=="en")
                    "SENTENCE"
                else
                    "جمله‌ساز",

                if(lang=="en")
                    "Build sentence"
                else
                    "جمله درست بساز",

                SentenceChallengeColor,

                Icons.Default.TextFields
            )


        else ->

            ChallengeMeta(

                if(lang=="en")
                    "WORD BATTLE"
                else
                    "نبرد کلمات",

                if(lang=="en")
                    "Find answer"
                else
                    "جواب را پیدا کن",

                WordChallengeColor,

                Icons.Default.Spellcheck
            )
    }
}




@Composable
private fun ArenaBottomBar(
    lang: String,
    scale: Float,
    shop: () -> Unit,
    rank: () -> Unit,
    profile: () -> Unit,
    game: () -> Unit
) {

    Row(

        modifier =
        Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF030812)
            )
            .border(
                1.dp,
                Color(0xFF12233B)
            )
            .padding(vertical = 6.dp),

        horizontalArrangement =
        Arrangement.SpaceEvenly,

        verticalAlignment =
        Alignment.CenterVertically
    ) {


        BottomItem(
            if(lang=="en")
                "Home"
            else
                "خانه",

            Icons.Default.Home,

            WarmYellow,

            scale
        )



        BottomItem(
            if(lang=="en")
                "Shop"
            else
                "فروشگاه",

            Icons.Default.ShoppingCart,

            WarmYellow,

            scale,

            shop
        )



        BottomItem(
            if(lang=="en")
                "Game"
            else
                "بازی",

            Icons.Default.SportsEsports,

            TurquoiseSecondary,

            scale,

            game
        )



        BottomItem(
            if(lang=="en")
                "Rank"
            else
                "رتبه",

            Icons.Default.EmojiEvents,

            WarmYellow,

            scale,

            rank
        )



        BottomItem(
            if(lang=="en")
                "Profile"
            else
                "پروفایل",

            Icons.Default.Person,

            SkyBlue,

            scale,

            profile
        )
    }
}




@Composable
private fun BottomItem(
    title: String,
    icon: ImageVector,
    color: Color,
    scale: Float,
    onClick: () -> Unit = {}
) {


    Column(

        modifier =
        Modifier
            .clip(
                RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 13.dp,
                vertical = 4.dp
            ),

        horizontalAlignment =
        Alignment.CenterHorizontally
    ) {


        Icon(

            icon,

            contentDescription = null,

            tint = color,

            modifier =
            Modifier.size(22.dp)
        )


        Text(

            title,

            color = TextMuted,

            fontSize = (8 * scale).sp,

            fontWeight = FontWeight.Bold
        )
    }
}
