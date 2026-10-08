package com.panjganeh.game.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium game palette — dark-first, competitive, readable and RTL friendly.
val PurplePrimary = Color(0xFF7C5CFF)
val TurquoiseSecondary = Color(0xFF2DD4BF)
val PinkTertiary = Color(0xFFF472B6)
val WarmYellow = Color(0xFFFBBF24)
val CoralRed = Color(0xFFFB7185)
val SkyBlue = Color(0xFF60A5FA)

val WordChallengeColor = Color(0xFF8B7CFF)
val MemoryChallengeColor = Color(0xFF2DD4BF)
val DiceChallengeColor = Color(0xFFFBBF24)
val RpsChallengeColor = Color(0xFFF472B6)
val SentenceChallengeColor = Color(0xFF60A5FA)

val PanjganehBgDark = Color(0xFF070B18)
val PanjganehSurfaceDark = Color(0xFF0F172A)
val PanjganehGlassDark = Color(0x331E2A4A)
val PanjganehGlassBorderDark = Color(0x667C5CFF)

val PanjganehBgLight = Color(0xFFF4F6FB)
val PanjganehSurfaceLight = Color(0xFFFFFFFF)
val PanjganehGlassLight = Color(0xD9FFFFFF)
val PanjganehGlassBorderLight = Color(0x4D7C5CFF)

val GoldPrimary = WarmYellow
val GoldLight = Color(0xFFFDE68A)
val GoldDark = Color(0xFFD97706)
val SkySecondary = TurquoiseSecondary
val EmeraldTertiary = Color(0xFF34D399)
val ArenaBackground = PanjganehBgDark
val ArenaSurface = Color(0xE60F172A)
val ArenaSurfaceElevated = Color(0xEE18213A)
val ArenaCardBack = Color(0xE6141D33)
val ArenaSurfaceBorder = Color(0x3D94A3C8)
val ArenaError = CoralRed
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFFB8C2D9)
val TextMuted = Color(0xFF71809B)
val VipGold = Color(0xFFFDE68A)

val SplashGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF151238), Color(0xFF0C1024), PanjganehBgDark)
)

val HeroJoyfulGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF7C5CFF), Color(0xFF3B82F6), Color(0xFF2DD4BF))
)

val CardJoyfulGradient = Brush.linearGradient(
    colors = listOf(Color(0x337C5CFF), Color(0x202DD4BF), Color(0x14FBBF24))
)

val ButtonJoyfulGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF7C5CFF), Color(0xFF5B4BE0))
)

val VipCrownGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFDE68A), Color(0xFFFBBF24), Color(0xFFD97706))
)

val CreatorCardBorderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF2DD4BF), Color(0xFF7C5CFF), Color(0xFFFBBF24))
)