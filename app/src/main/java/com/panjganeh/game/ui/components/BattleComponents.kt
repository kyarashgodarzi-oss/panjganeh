package com.panjganeh.game.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.panjganeh.game.ai.AiProfile
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

/**
 * هدر استاندارد نبرد با نمایش امتیاز کاربر و AI، تایمر و اطلاعات مبارزه
 */
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("battle_header"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldPrimary.copy(alpha = 0.5f), SkySecondary.copy(alpha = 0.5f))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // ردیف عنوان بازی و خروج
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExitClick, modifier = Modifier.size(36.dp).testTag("exit_battle_button")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "خروج", tint = TextSecondary)
                }

                Text(
                    text = challengeTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )

                // تایمر معکوس یا شمارنده دست
                if (remainingSeconds != null) {
                    val isUrgent = remainingSeconds <= 10
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isUrgent) ArenaError.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "زمان باقی‌مانده",
                            tint = if (isUrgent) ArenaError else GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${remainingSeconds}s",
                            color = if (isUrgent) ArenaError else GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else if (currentRound != null && maxRounds != null) {
                    Text(
                        text = "دست $currentRound از $maxRounds",
                        color = SkySecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SkySecondary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ردیف مبارزه‌کنندگان (کاربر در مقابل AI)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // بازیکن (سمت چپ/راست متناسب با RTL)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f))
                            .border(2.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "شما", tint = GoldPrimary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "شما", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "امتیاز: $userScore", color = GoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }

                // آیکون وسط VS
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ArenaBackground)
                        .border(1.dp, ArenaSurfaceBorder, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "VS", color = TextMuted, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }

                // حریف هوش مصنوعی
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = aiProfile?.name ?: "هوش مصنوعی", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "امتیاز: $aiScore", color = SkySecondary, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SkySecondary.copy(alpha = 0.2f))
                            .border(2.dp, SkySecondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = "AI", tint = SkySecondary, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

/**
 * دیالوگ نمایش نتیجه مسابقه (پیروزی یا شکست) همراه با پاداش و دکمه بازگشت
 */
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("battle_result_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = ArenaSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
                if (isWin) listOf(GoldPrimary, EmeraldTertiary) else listOf(ArenaError, ArenaSurfaceBorder)
            ))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // آیکون جام یا مدال
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (isWin) GoldPrimary.copy(alpha = 0.2f) else ArenaError.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWin) Icons.Default.EmojiEvents else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (isWin) GoldPrimary else ArenaError,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isWin) "🎉 پیروزی درخشان!" else " شکستی در میدان پنجگانه!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isWin) GoldLight else ArenaError,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isWin) "حریف هوش مصنوعی مغلوب توانایی شما شد" else "حریف این بار دست بالا را گرفت، دوباره تلاش کن!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // باکس مقایسه امتیاز
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ArenaBackground)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "شما", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "$userScore", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                    Text(text = "-", color = TextMuted, fontSize = 24.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "حریف AI", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "$aiScore", color = SkySecondary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // پاداش مسابقه
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldTertiary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "پاداش دریافت شده: +$earnedCoins سکه طلا",
                        color = EmeraldTertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // دکمه‌های اقدام
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("play_again_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "نبرد دوباره", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onBackHome,
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("back_home_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "بازگشت به منوی اصلی", color = TextSecondary)
                }
            }
        }
    }
}

/**
 * بنر ویدیوی جایزه‌دار تپسل (در صورتی که کاربر VIP نباشد)
 */
@Composable
fun TapsellBannerAdView(
    isVip: Boolean,
    onAdClick: () -> Unit = {}
) {
    if (isVip) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onAdClick() }
            .testTag("tapsell_banner_ad"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ویدیوی جایزه‌دار تپسل",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "مشاهده ویدیو و دریافت سکه رایگان",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ArenaBackground)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = "پاداش", color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}
