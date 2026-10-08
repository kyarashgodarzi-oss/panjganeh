package com.panjganeh.game.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

@Composable
fun BattleArenaBackground(
    challenge: String,
    modifier: Modifier = Modifier
) {
    val resource = when {
        challenge.contains("تاس") -> R.drawable.arena_hero_banner_1789948811846
        challenge.contains("کلمات") -> R.drawable.arena_clash_icon_1789948798958
        challenge.contains("حافظه") -> R.drawable.arena_hero_banner_1789948811846
        challenge.contains("قیچی") -> R.drawable.arena_clash_icon_1789948798958
        else -> R.drawable.arena_hero_banner_1789948811846
    }
    Box(modifier.background(ArenaBackground)) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.20f
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        ArenaBackground.copy(alpha = 0.72f),
                        ArenaBackground.copy(alpha = 0.92f),
                        ArenaBackground
                    )
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
    val urgent = remainingSeconds != null && remainingSeconds <= 10
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).testTag("battle_header"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(PurplePrimary.copy(alpha = .65f), TurquoiseSecondary.copy(alpha = .55f)))
        )
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onExitClick, modifier = Modifier.size(38.dp).testTag("exit_battle_button")) {
                    Icon(Icons.Default.Close, "خروج", tint = TextSecondary)
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ARENA BATTLE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
                    Text(challengeTitle, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(13.dp))
                        .background(if (urgent) ArenaError.copy(alpha=.16f) else GoldPrimary.copy(alpha=.12f))
                        .border(1.dp, if (urgent) ArenaError.copy(alpha=.45f) else GoldPrimary.copy(alpha=.35f), RoundedCornerShape(13.dp))
                        .padding(horizontal=9.dp, vertical=6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, tint = if (urgent) ArenaError else GoldPrimary, modifier=Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            remainingSeconds?.let { "$it" } ?: if (currentRound != null && maxRounds != null) "$currentRound/$maxRounds" else "LIVE",
                            color = if (urgent) ArenaError else GoldPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FighterBadge("شما", userScore, Icons.Default.Person, GoldPrimary, Modifier.weight(1f))
                Box(Modifier.size(34.dp).clip(CircleShape).background(ArenaBackground).border(1.dp, ArenaSurfaceBorder, CircleShape), contentAlignment=Alignment.Center) {
                    Text("VS", color=TextMuted, fontSize=10.sp, fontWeight=FontWeight.Black)
                }
                FighterBadge(aiProfile?.name ?: "حریف AI", aiScore, Icons.Default.SmartToy, SkySecondary, Modifier.weight(1f), true)
            }
            if (remainingSeconds != null) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { (remainingSeconds / 60f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = if (urgent) ArenaError else TurquoiseSecondary,
                    trackColor = ArenaSurfaceBorder.copy(alpha=.35f)
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
    Dialog(onDismissRequest = {}) {
        Card(
            Modifier.fillMaxWidth().padding(12.dp).testTag("battle_result_dialog"),
            shape=RoundedCornerShape(30.dp),
            colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),
            border=androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if(isWin) Brush.linearGradient(listOf(GoldPrimary, TurquoiseSecondary)) else Brush.linearGradient(listOf(ArenaError, PurplePrimary))
            )
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment=Alignment.CenterHorizontally) {
                Box(Modifier.size(82.dp).clip(CircleShape).background(if(isWin) GoldPrimary.copy(alpha=.15f) else ArenaError.copy(alpha=.13f)).border(2.dp, if(isWin) GoldPrimary else ArenaError, CircleShape), contentAlignment=Alignment.Center) {
                    Icon(if(isWin) Icons.Default.EmojiEvents else Icons.Default.Close, null, tint=if(isWin) GoldPrimary else ArenaError, modifier=Modifier.size(46.dp))
                }
                Spacer(Modifier.height(14.dp))
                Text(if(isWin) "پیروزی!" else "نبرد تمام شد", color=if(isWin) GoldLight else ArenaError, fontSize=26.sp, fontWeight=FontWeight.Black)
                Text(if(isWin) "تو کنترل میدان را در دست گرفتی." else "این راند را از دست دادی؛ دوباره وارد میدان شو.", color=TextSecondary, fontSize=12.sp, textAlign=TextAlign.Center, modifier=Modifier.padding(top=5.dp))
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
                    OutlinedButton(onClick=onBackHome, Modifier.weight(1f).height(48.dp).testTag("back_home_button"), shape=RoundedCornerShape(14.dp)) {
                        Text("خروج", color=TextSecondary, fontWeight=FontWeight.Bold)
                    }
                    Button(onClick=onPlayAgain, Modifier.weight(1.25f).height(48.dp).testTag("play_again_button"), shape=RoundedCornerShape(14.dp), colors=ButtonDefaults.buttonColors(containerColor=PurplePrimary)) {
                        Icon(Icons.Default.Replay, null, modifier=Modifier.size(18.dp)); Spacer(Modifier.width(5.dp)); Text("نبرد دوباره", fontWeight=FontWeight.Black)
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
