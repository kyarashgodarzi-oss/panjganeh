package com.panjganeh.game.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class PanjganehThemePreset(
    val id: Int,
    val nameFa: String,
    val nameEn: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val surfaceDark: Color,
    val surfaceLight: Color,
    val bgDark: Color,
    val bgLight: Color
)

val AvailableThemes = listOf(
    PanjganehThemePreset(0, "آرنا (پیشنهادی)", "Arena", Color(0xFF1677FF), Color(0xFF22D3EE), Color(0xFFFFD21F), Color(0xFF071B37), Color(0xFFFFFFFF), Color(0xFF030916), Color(0xFFF4F7FC)),
    PanjganehThemePreset(1, "بنفش سلطنتی", "Royal Purple", Color(0xFF9B7CFF), Color(0xFF6366F1), Color(0xFFFBBF24), Color(0xFF15132C), Color(0xFFFFFFFF), Color(0xFF090817), Color(0xFFF5F3FF)),
    PanjganehThemePreset(2, "اقیانوس نئون", "Neon Ocean", Color(0xFF38BDF8), Color(0xFF2DD4BF), Color(0xFF818CF8), Color(0xFF0D1B2A), Color(0xFFFFFFFF), Color(0xFF06111D), Color(0xFFF0F9FF)),
    PanjganehThemePreset(3, "زمرد تاکتیکی", "Tactical Emerald", Color(0xFF34D399), Color(0xFF14B8A6), Color(0xFFFBBF24), Color(0xFF0D201B), Color(0xFFFFFFFF), Color(0xFF06120F), Color(0xFFF0FDF9)),
    PanjganehThemePreset(4, "قرمز رقابتی", "Competitive Red", Color(0xFFFB7185), Color(0xFFF97316), Color(0xFFFBBF24), Color(0xFF241218), Color(0xFFFFFFFF), Color(0xFF11070B), Color(0xFFFFF1F2)),
    PanjganehThemePreset(5, "طلایی قهرمان", "Champion Gold", Color(0xFFFBBF24), Color(0xFFF59E0B), Color(0xFF7C5CFF), Color(0xFF211A0B), Color(0xFFFFFFFF), Color(0xFF120E04), Color(0xFFFFFBEB)),
    PanjganehThemePreset(6, "شب کلاسیک", "Midnight Classic", Color(0xFF818CF8), Color(0xFF64748B), Color(0xFF38BDF8), Color(0xFF111827), Color(0xFFFFFFFF), Color(0xFF050914), Color(0xFFF1F5F9))
)

val LocalFontScale = compositionLocalOf { 1.0f }
val LocalPanjganehTheme = compositionLocalOf { AvailableThemes[0] }
val LocalAppLanguage = compositionLocalOf { "fa" }
