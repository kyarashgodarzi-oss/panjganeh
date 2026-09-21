package com.panjganeh.game.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// 🎨 پالت رنگی شاد و پرانرژی بازی پنجگانه
// ==========================================

// رنگ‌های اصلی خواسته شده
val PurplePrimary = Color(0xFF6C5CE7)
val TurquoiseSecondary = Color(0xFF00D2D3)
val PinkTertiary = Color(0xFFFD79A8)
val WarmYellow = Color(0xFFFDCB6E)
val CoralRed = Color(0xFFFF7675)
val SkyBlue = Color(0xFF74B9FF)

// رنگ‌های اختصاصی چالش‌های پنجگانه
val WordChallengeColor = Color(0xFF6C5CE7)       // بنفش
val MemoryChallengeColor = Color(0xFF00D2D3)     // فیروزه‌ای
val DiceChallengeColor = Color(0xFFFDCB6E)       // زرد
val RpsChallengeColor = Color(0xFFFD79A8)        // صورتی
val SentenceChallengeColor = Color(0xFF74B9FF)   // آبی

// رنگ‌های پس‌زمینه و سطوح گلس‌مورفیسم (حالت تیره شاداب)
val PanjganehBgDark = Color(0xFF0D0C1D)
val PanjganehSurfaceDark = Color(0xFF16152B)
val PanjganehGlassDark = Color(0x332E285F)
val PanjganehGlassBorderDark = Color(0x666C5CE7)

// رنگ‌های پس‌زمینه و سطوح (حالت روشن جذاب)
val PanjganehBgLight = Color(0xFFF7F8FC)
val PanjganehSurfaceLight = Color(0xFFFFFFFF)
val PanjganehGlassLight = Color(0xCCFFFFFF)
val PanjganehGlassBorderLight = Color(0x4D6C5CE7)

// سازگاری برای اسامی عمومی قبلی
val GoldPrimary = WarmYellow
val GoldLight = Color(0xFFFFEAA7)
val GoldDark = Color(0xFFE17055)
val SkySecondary = TurquoiseSecondary
val EmeraldTertiary = Color(0xFF00B894)
val ArenaBackground = PanjganehBgDark
val ArenaSurface = PanjganehSurfaceDark
val ArenaSurfaceElevated = Color(0xFF222044)
val ArenaCardBack = Color(0xFF1E1B4B)
val ArenaSurfaceBorder = Color(0x406C5CE7)
val ArenaError = CoralRed
val TextPrimary = Color(0xFFFDFDFD)
val TextSecondary = Color(0xFFB4B2D8)
val TextMuted = Color(0xFF7D7A9F)
val VipGold = Color(0xFFFFD700)

// ==========================================
// 🌈 گرادیان‌های رنگارنگ و نئونی
// ==========================================
val SplashGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2E1C4D),
        Color(0xFF15102A),
        Color(0xFF0D0C1D)
    )
)

val HeroJoyfulGradient = Brush.linearGradient(
    colors = listOf(PurplePrimary, TurquoiseSecondary, SkyBlue)
)

val CardJoyfulGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x336C5CE7),
        Color(0x2600D2D3),
        Color(0x1AFD79A8)
    )
)

val ButtonJoyfulGradient = Brush.horizontalGradient(
    colors = listOf(PurplePrimary, TurquoiseSecondary)
)

val VipCrownGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFEAA7), Color(0xFFFDCB6E), Color(0xFFE17055))
)

val CreatorCardBorderGradient = Brush.linearGradient(
    colors = listOf(TurquoiseSecondary, PinkTertiary, PurplePrimary)
)
