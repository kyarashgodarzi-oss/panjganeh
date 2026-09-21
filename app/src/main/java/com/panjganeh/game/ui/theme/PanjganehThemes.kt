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
    PanjganehThemePreset(
        id = 0,
        nameFa = "پنجگانه شاد (پیش‌فرض)",
        nameEn = "Default Joyful",
        primary = Color(0xFF6C5CE7),
        secondary = Color(0xFF00D2D3),
        tertiary = Color(0xFFFD79A8),
        surfaceDark = Color(0xFF16152B),
        surfaceLight = Color(0xFFFFFFFF),
        bgDark = Color(0xFF0D0C1D),
        bgLight = Color(0xFFF7F8FC)
    ),
    PanjganehThemePreset(
        id = 1,
        nameFa = "رویای صورتی (Pink Dream)",
        nameEn = "Pink Dream",
        primary = Color(0xFFFD79A8),
        secondary = Color(0xFFA29BFE),
        tertiary = Color(0xFFFF7675),
        surfaceDark = Color(0xFF261220),
        surfaceLight = Color(0xFFFFF5F8),
        bgDark = Color(0xFF140811),
        bgLight = Color(0xFFFFF0F5)
    ),
    PanjganehThemePreset(
        id = 2,
        nameFa = "اقیانوس آبی (Ocean Blue)",
        nameEn = "Ocean Blue",
        primary = Color(0xFF74B9FF),
        secondary = Color(0xFF00CEC9),
        tertiary = Color(0xFF0984E3),
        surfaceDark = Color(0xFF0F2033),
        surfaceLight = Color(0xFFF0F7FF),
        bgDark = Color(0xFF07121E),
        bgLight = Color(0xFFE8F3FF)
    ),
    PanjganehThemePreset(
        id = 3,
        nameFa = "جنگل زمرد (Forest Green)",
        nameEn = "Forest Green",
        primary = Color(0xFF00B894),
        secondary = Color(0xFF55EFC4),
        tertiary = Color(0xFFFDCB6E),
        surfaceDark = Color(0xFF0C241D),
        surfaceLight = Color(0xFFF0FFF8),
        bgDark = Color(0xFF061510),
        bgLight = Color(0xFFE6FAF2)
    ),
    PanjganehThemePreset(
        id = 4,
        nameFa = "غروب سرخ (Sunset Red)",
        nameEn = "Sunset Red",
        primary = Color(0xFFFF7675),
        secondary = Color(0xFFFAB1A0),
        tertiary = Color(0xFFE17055),
        surfaceDark = Color(0xFF2B1414),
        surfaceLight = Color(0xFFFFF5F4),
        bgDark = Color(0xFF170909),
        bgLight = Color(0xFFFFEEEE)
    ),
    PanjganehThemePreset(
        id = 5,
        nameFa = "خورشید طلایی (Golden Sun)",
        nameEn = "Golden Sun",
        primary = Color(0xFFFDCB6E),
        secondary = Color(0xFFFFEAA7),
        tertiary = Color(0xFFE17055),
        surfaceDark = Color(0xFF292211),
        surfaceLight = Color(0xFFFFFDF5),
        bgDark = Color(0xFF171308),
        bgLight = Color(0xFFFFF9E6)
    ),
    PanjganehThemePreset(
        id = 6,
        nameFa = "کلاسیک شب (Dark Classic)",
        nameEn = "Dark Classic",
        primary = Color(0xFFA29BFE),
        secondary = Color(0xFF6C5CE7),
        tertiary = Color(0xFF00D2D3),
        surfaceDark = Color(0xFF1A1A24),
        surfaceLight = Color(0xFFF4F4F8),
        bgDark = Color(0xFF0F0F16),
        bgLight = Color(0xFFEBEBF2)
    )
)

val LocalFontScale = compositionLocalOf { 1.0f }
val LocalPanjganehTheme = compositionLocalOf { AvailableThemes[0] }
val LocalAppLanguage = compositionLocalOf { "fa" }
