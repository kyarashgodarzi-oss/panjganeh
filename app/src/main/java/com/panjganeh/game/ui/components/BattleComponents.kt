package com.panjganeh.game.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.panjganeh.game.R
import com.panjganeh.game.ai.AiProfile
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaError
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.EmeraldTertiary
import com.panjganeh.game.ui.theme.GoldLight
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.PurplePrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.WordChallengeColor
import com.panjganeh.game.ui.theme.MemoryChallengeColor
import com.panjganeh.game.ui.theme.DiceChallengeColor
import com.panjganeh.game.ui.theme.RpsChallengeColor
import com.panjganeh.game.ui.theme.SentenceChallengeColor

@Composable
fun BattleArenaBackground(
    challenge: String,
    modifier: Modifier = Modifier
) {
    val challengeAccent = when {
        challenge.contains("تاس") -> DiceChallengeColor
        challenge.contains("کلمات") -> WordChallengeColor
        challenge.contains("حافظه") -> MemoryChallengeColor
        challenge.contains("قیچی") -> RpsChallengeColor
        challenge.contains("جملات") || challenge.contains("جمله") -> SentenceChallengeColor
        else -> TurquoiseSecondary
    }
    Box(
        modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF030916), Color(0xFF06142B), ArenaBackground)
                )
            )
    ) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .size(240.dp)
                .background(
                    Brush.radialGradient(
                        listOf(challengeAccent.copy(alpha = 0.14f), Color.Transparent)
                    )
                )
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(280.dp)
                .background(
                    Brush.radialGradient(
                        listOf(challengeAccent.copy(alpha = 0.09f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun BattleHeader(
    challengeTitle: String,
    userScore: Int,
    aiScore: Int,
    remainingSeconds: Int? = null,
    currentRound: Int? = null,
    maxRounds: Int? = null,
    aiProfile: AiProfile?,
    onExitClick: () -> Unit
) {
    val roundText = if (currentRound != null && maxRounds != null) "$currentRound/$maxRounds" else "1/5"
    val urgent = remainingSeconds != null && remainingSeconds <= 10

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .testTag("battle_header"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = 0.88f)),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(
                    PurplePrimary.copy(alpha = .75f),
                    TurquoiseSecondary.copy(alpha = .65f),
                    GoldPrimary.copy(alpha = .55f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (urgent) ArenaError.copy(alpha = .14f)
                            else GoldPrimary.copy(alpha = .12f)
                        )
                        .border(
                            1.dp,
                            if (urgent) ArenaError.copy(alpha = .60f)
                            else GoldPrimary.copy(alpha = .60f),
                            RoundedCornerShape(13.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            roundText,
                            color = if (urgent) ArenaError else GoldPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (urgent) ArenaError else GoldPrimary,
                            modifier = Modifier
                                .size(17.dp)
                                .padding(start = 2.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "ARENA BATTLE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        challengeTitle,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                IconButton(
                    onClick = onExitClick,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("exit_battle_button")
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "خروج",
                        tint = TextSecondary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 3.dp, vertical = 4.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(GoldPrimary, TurquoiseSecondary, PurplePrimary)
                        )
                    )
                    .testTag("battle_reference_accent")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FighterBadge(
                    aiProfile?.name ?: "تایان هوشمند",
                    aiScore,
                    Icons.Default.SmartToy,
                    SkySecondary,
                    Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(ArenaBackground)
                        .border(1.dp, ArenaSurfaceBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("VS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }

                FighterBadge(
                    "شما",
                    userScore,
                    Icons.Default.Person,
                    GoldPrimary,
                    Modifier.weight(1f),
                    true
                )
            }
        }
    }
}

@Composable
fun BattleRoundStars(
    userScore: Int,
    aiScore: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            repeat(3) { index ->
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = if (userScore > index) GoldPrimary else ArenaSurfaceBorder,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            "اولین به ۳ برد برنده است",
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )

        Row {
            repeat(3) { index ->
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = if (aiScore > index) SkySecondary else ArenaSurfaceBorder,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun FighterBadge(name: String, score: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, modifier: Modifier, end: Boolean=false) {
    Row(modifier, horizontalArrangement=if(end) Arrangement.End else Arrangement.Start, verticalAlignment=Alignment.CenterVertically) {
        if (!end) {
            AvatarIcon(icon, tint)
            Spacer(Modifier.width(7.dp))
        }
        Column(horizontalAlignment=if(end) Alignment.End else Alignment.Start) {
            Text(name, color=TextSecondary, fontSize=10.sp, fontWeight=FontWeight.Bold)
            Text(score.toString(), color=tint, fontSize=20.sp, fontWeight=FontWeight.Black)
        }
        if (end) { Spacer(Modifier.width(7.dp)); AvatarIcon(icon, tint) }
    }
}

@Composable
private fun AvatarIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Box(Modifier.size(38.dp).clip(CircleShape).background(tint.copy(alpha=.13f)).border(1.5.dp, tint.copy(alpha=.6f), CircleShape), contentAlignment=Alignment.Center) {
        Icon(icon, null, tint=tint, modifier=Modifier.size(21.dp))
    }
}

@Composable
fun BattleResultDialog(
    isWin: Boolean,
    userScore: Int,
    aiScore: Int,
    earnedCoins: Int,
    onPlayAgain: () -> Unit,
    onBackHome: () -> Unit
) {
    val resultPulse = rememberInfiniteTransition(label = "result_pulse").animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "result_pulse_scale"
    )

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            Modifier.fillMaxSize().testTag("battle_result_dialog"),
            shape=RoundedCornerShape(0.dp),
            colors=CardDefaults.cardColors(containerColor=ArenaSurface),
            border=androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if(isWin) Brush.linearGradient(listOf(GoldPrimary, TurquoiseSecondary)) else Brush.linearGradient(listOf(ArenaError, PurplePrimary))
            )
        ) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackHome, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "بازگشت", tint = TextSecondary)
                    }
                    Text("نتیجه مرحله", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Image(
                    painter = painterResource(R.drawable.arena_hero_banner_1789948811846),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(220.dp)
                        .height(240.dp)
                        .border(2.dp, if (isWin) GoldPrimary else ArenaError, RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                )
                Spacer(Modifier.height(12.dp))
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (isWin) Brush.horizontalGradient(listOf(GoldPrimary, Color(0xFFFF8A00)))
                            else Brush.horizontalGradient(listOf(ArenaError, Color(0xFF991B1B)))
                        )
                        .padding(horizontal = 34.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isWin) "برد!" else "باخت!",
                        color = if (isWin) ArenaBackground else Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.height(9.dp))
                Text(
                    if (isWin) "شما در این مرحله پیروز شدید!" else "حریف این مرحله را برد!",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(ArenaBackground).padding(13.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ScoreColumn("شما", userScore, GoldPrimary)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("VS", color = TextMuted, fontWeight = FontWeight.Black)
                    }
                    ScoreColumn("حریف AI", aiScore, SkySecondary)
                }
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(ArenaBackground).padding(16.dp), horizontalArrangement=Arrangement.SpaceEvenly) {
                    ScoreColumn("شما", userScore, GoldPrimary)
                    Column(horizontalAlignment=Alignment.CenterHorizontally) {
                        Text("RESULT", color=TextMuted, fontSize=8.sp, fontWeight=FontWeight.Black, letterSpacing=1.sp)
                        Text("VS", color=TextMuted, fontWeight=FontWeight.Black)
                    }
                    ScoreColumn("حریف AI", aiScore, SkySecondary)
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(EmeraldTertiary.copy(alpha=.10f)).padding(12.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.Center) {
                    Icon(Icons.Default.MonetizationOn, null, tint=GoldPrimary, modifier=Modifier.size(20.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("+$earnedCoins سکه پاداش", color=EmeraldTertiary, fontWeight=FontWeight.Black, fontSize=13.sp)
                }
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                    OutlinedButton(onClick=onPlayAgain, Modifier.weight(1f).height(48.dp).testTag("play_again_button"), shape=RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.Replay, null, modifier=Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("دوباره بازی کن", color=TextSecondary, fontWeight=FontWeight.Bold)
                    }
                    Button(onClick=onBackHome, Modifier.weight(1.25f).height(48.dp).testTag("back_home_button"), shape=RoundedCornerShape(14.dp), colors=ButtonDefaults.buttonColors(containerColor=GoldPrimary, contentColor=ArenaBackground)) {
                        Text("ادامه", fontWeight=FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun TapsellBannerAdView(isVip: Boolean, onAdClick: () -> Unit = {}) {
    if (isVip) return
    Card(
        Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=6.dp).clickable { onAdClick() }.testTag("tapsell_banner_ad"),
        shape=RoundedCornerShape(16.dp), colors=CardDefaults.cardColors(containerColor=ArenaSurface),
        border=androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha=.18f))
    ) {
        Row(Modifier.fillMaxWidth().padding(11.dp), verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(GoldPrimary.copy(alpha=.13f)), contentAlignment=Alignment.Center) {
                Icon(Icons.Default.CardGiftcard, null, tint=GoldPrimary, modifier=Modifier.size(19.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("ویدیوی جایزه‌دار", color=TextPrimary, fontSize=11.sp, fontWeight=FontWeight.Bold)
                Text("تماشا کن و سکه رایگان بگیر", color=TextMuted, fontSize=10.sp)
            }
            Text("REWARD", color=GoldPrimary, fontSize=9.sp, fontWeight=FontWeight.Black)
        }
    }
}

@Composable
private fun ScoreColumn(label:String, score:Int, tint:Color) {
    Column(horizontalAlignment=Alignment.CenterHorizontally) {
        Text(label, color=TextSecondary, fontSize=11.sp)
        Text(score.toString(), color=tint, fontSize=28.sp, fontWeight=FontWeight.Black)
        Row {
            repeat(3) { Icon(Icons.Default.Star, null, tint=tint.copy(alpha=.8f), modifier=Modifier.size(11.dp)) }
        }
    }
}
