package com.panjganeh.game.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Reference palette: deep navy surfaces, electric-blue panels, cyan highlights and
// the warm yellow CTA treatment used throughout the supplied Panjganeh screens.
val PurplePrimary = Color(0xFF1677FF)
val TurquoiseSecondary = Color(0xFF22D3EE)
val PinkTertiary = Color(0xFFB46CFF)
val WarmYellow = Color(0xFFFFD21F)
val CoralRed = Color(0xFFFF647C)
val SkyBlue = Color(0xFF42A5FF)

val WordChallengeColor = Color(0xFF42A5FF)
val MemoryChallengeColor = Color(0xFF20C8E8)
val DiceChallengeColor = Color(0xFFFFD21F)
val RpsChallengeColor = Color(0xFFB46CFF)
val SentenceChallengeColor = Color(0xFF168BFF)

val PanjganehBgDark = Color(0xFF030916)
val PanjganehSurfaceDark = Color(0xFF071B37)
val PanjganehGlassDark = Color(0xCC071B37)
val PanjganehGlassBorderDark = Color(0x6652B8FF)

val PanjganehBgLight = Color(0xFFF4F7FC)
val PanjganehSurfaceLight = Color(0xFFFFFFFF)
val PanjganehGlassLight = Color(0xD9FFFFFF)
val PanjganehGlassBorderLight = Color(0x4D1677FF)

val GoldPrimary = WarmYellow
val GoldLight = Color(0xFFFFE680)
val GoldDark = Color(0xFFB98500)
val SkySecondary = TurquoiseSecondary
val EmeraldTertiary = Color(0xFF20D6B5)
val ArenaBackground = PanjganehBgDark
val ArenaSurface = Color(0xF0071B37)
val ArenaSurfaceElevated = Color(0xF00A2548)
val ArenaCardBack = Color(0xF0082042)
val ArenaSurfaceBorder = Color(0x6652B8FF)
val ArenaError = CoralRed
val TextPrimary = Color(0xFFF7FAFF)
val TextSecondary = Color(0xFFB5C9E6)
val TextMuted = Color(0xFF7189AA)
val VipGold = Color(0xFFFFE680)

val SplashGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF071B37), Color(0xFF041127), PanjganehBgDark)
)

val HeroJoyfulGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0D4DA8), Color(0xFF1677FF), Color(0xFF22D3EE))
)

val CardJoyfulGradient = Brush.linearGradient(
    colors = listOf(Color(0x331677FF), Color(0x2022D3EE), Color(0x14FFD21F))
)

val ButtonJoyfulGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFD21F), Color(0xFFFFA800))
)

val VipCrownGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFE680), Color(0xFFFFD21F), Color(0xFFB98500))
)

val CreatorCardBorderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF22D3EE), Color(0xFF1677FF), Color(0xFFFFD21F))
)
