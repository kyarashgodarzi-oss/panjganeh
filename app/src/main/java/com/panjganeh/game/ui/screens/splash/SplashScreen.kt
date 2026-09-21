package com.panjganeh.game.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panjganeh.game.R
import com.panjganeh.game.ui.theme.CoralRed
import com.panjganeh.game.ui.theme.CreatorCardBorderGradient
import com.panjganeh.game.ui.theme.LocalFontScale
import com.panjganeh.game.ui.theme.PanjganehBgDark
import com.panjganeh.game.ui.theme.PinkTertiary
import com.panjganeh.game.ui.theme.PurplePrimary
import com.panjganeh.game.ui.theme.SkyBlue
import com.panjganeh.game.ui.theme.SplashGradient
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.WarmYellow
import kotlinx.coroutines.delay

/**
 * صفحه اسپلش اسکرین بازی پنجگانه
 * دارای مدت زمان ۳ ثانیه‌ای با کادر ویژه نام سازنده (سیدحمید موسوی زاده)
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    val fontScale = LocalFontScale.current
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    LaunchedEffect(Unit) {
        // انیمیشن ورود فید و اسکیل
        alpha.animateTo(1f, animationSpec = tween(600))
        scale.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        
        // پر شدن روان نوار پیشرفت در طول ۳ ثانیه
        progress.animateTo(1f, animationSpec = tween(2800, easing = LinearEasing))
        
        delay(200L)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashGradient)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // ۵ جرقه نماد ۵ چالش بازی (⚡ ⚡ ⚡ ⚡ ⚡)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(PurplePrimary, TurquoiseSecondary, WarmYellow, PinkTertiary, SkyBlue).forEach { dotColor ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // آیکون اصلی بازی: نماد پنج‌گانه (۵ دایره رنگی هماهنگ با ۵ چالش)
            PanjganehFiveCirclesEmblem(
                modifier = Modifier.scale(glowScale),
                size = 120.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // نام فارسی بازی
            Text(
                text = "پنجگانه",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = (38 * fontScale).sp
                ),
                color = Color.White
            )

            // نام انگلیسی بازی
            Text(
                text = "PANJGANEH",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    fontSize = (16 * fontScale).sp
                ),
                color = TurquoiseSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )

            // شعار بازی: پنج چالش، پنج نبرد، یک قهرمان
            Text(
                text = "پنج چالش، پنج نبرد، یک قهرمان",
                color = WarmYellow,
                fontWeight = FontWeight.SemiBold,
                fontSize = (13 * fontScale).sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ==========================================
            // 👨‍💻 کادر اختصاصی سازنده: سیدحمید موسوی زاده (Glassmorphism)
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .testTag("creator_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x332E1C4D)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = 1.5.dp,
                    brush = CreatorCardBorderGradient
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(TurquoiseSecondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = TurquoiseSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سازنده و برنامه‌نویس",
                            color = TurquoiseSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = (11 * fontScale).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        thickness = 1.dp,
                        color = Color(0x33FFFFFF)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "سیدحمید موسوی زاده",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (15 * fontScale).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // نوار بارگذاری ۳ ثانیه‌ای
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = TurquoiseSecondary,
                    trackColor = Color(0x26FFFFFF)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "آماده‌سازی میدان‌های پنجگانه...",
                    color = Color(0x99FFFFFF),
                    fontSize = (11 * fontScale).sp
                )
            }
        }
    }
}

/**
 * نماد اختصاصی و پرانرژی پنج‌گانه (۵ دایره رنگی نماد ۵ چالش بازی)
 * دایره ۱: بنفش (#6C5CE7) — کلمات 📝
 * دایره ۲: فیروزه‌ای (#00D2D3) — حافظه 🃏
 * دایره ۳: زرد (#FDCB6E) — تاس 🎲
 * دایره ۴: صورتی (#FD79A8) — سنگ‌کاغذقیچی ✊
 * دایره ۵: آبی آسمانی (#74B9FF) — جملات 📖
 */
@Composable
fun PanjganehFiveCirclesEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF26235C),
                        Color(0xFF14122C)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        PurplePrimary,
                        TurquoiseSecondary,
                        WarmYellow,
                        PinkTertiary,
                        SkyBlue,
                        PurplePrimary
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // حلقه اتصال‌دهنده مرکزی
        Box(
            modifier = Modifier
                .size(size * 0.58f)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = CircleShape
                )
        )

        // ۵ دایره رنگی در قالب پنج‌ضلعی متقارن
        val radius = size * 0.29f
        val circleSize = size * 0.32f
        val circles = listOf(
            Triple(PurplePrimary, "📝", -90.0),       // ۱. کلمات
            Triple(TurquoiseSecondary, "🃏", -18.0),   // ۲. حافظه
            Triple(WarmYellow, "🎲", 54.0),            // ۳. تاس
            Triple(PinkTertiary, "✊", 126.0),          // ۴. سنگ‌کاغذقیچی
            Triple(SkyBlue, "📖", 198.0)               // ۵. جملات
        )

        circles.forEach { (color, emoji, angleDeg) ->
            val angleRad = Math.toRadians(angleDeg)
            val offsetX = (radius.value * Math.cos(angleRad)).dp
            val offsetY = (radius.value * Math.sin(angleRad)).dp

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = 0.95f),
                                color
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = 0.75f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = (circleSize.value * 0.44f).sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // هسته مرکزی نشانگر عدد ۵
        Box(
            modifier = Modifier
                .size(size * 0.24f)
                .clip(CircleShape)
                .background(Color(0xFF1E1B4B))
                .border(1.2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "۵",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = (size.value * 0.13f).sp
            )
        }
    }
}
