package com.panjganeh.game.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.PanjganehApplication
import com.panjganeh.game.data.local.datastore.AppSettings

@Composable
fun PanjganehTheme(
    content: @Composable () -> Unit
) {
    val app = PanjganehApplication.instance
    val settings by app.settingsDataStore.appSettingsFlow.collectAsStateWithLifecycle(initialValue = AppSettings())

    val isDark = when (settings.darkMode) {
        "light" -> false
        "system" -> isSystemInDarkTheme()
        else -> true
    }

    val fontScale = when (settings.textSize) {
        "small" -> 0.88f
        "large" -> 1.18f
        else -> 1.0f
    }

    val themePreset = AvailableThemes.getOrNull(settings.themeId) ?: AvailableThemes[0]

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = themePreset.primary,
            onPrimary = Color.White,
            primaryContainer = themePreset.primary.copy(alpha = 0.3f),
            onPrimaryContainer = themePreset.primary,
            secondary = themePreset.secondary,
            onSecondary = Color(0xFF0F172A),
            secondaryContainer = themePreset.secondary.copy(alpha = 0.25f),
            onSecondaryContainer = themePreset.secondary,
            tertiary = themePreset.tertiary,
            onTertiary = Color.White,
            background = themePreset.bgDark,
            onBackground = Color.White,
            surface = themePreset.surfaceDark,
            onSurface = Color.White,
            surfaceVariant = themePreset.surfaceDark,
            onSurfaceVariant = Color(0xFFB4B2D8),
            error = CoralRed,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = themePreset.primary,
            onPrimary = Color.White,
            primaryContainer = themePreset.primary.copy(alpha = 0.15f),
            onPrimaryContainer = themePreset.primary,
            secondary = themePreset.secondary,
            onSecondary = Color.White,
            secondaryContainer = themePreset.secondary.copy(alpha = 0.15f),
            onSecondaryContainer = themePreset.secondary,
            tertiary = themePreset.tertiary,
            onTertiary = Color.White,
            background = themePreset.bgLight,
            onBackground = Color(0xFF1E152A),
            surface = themePreset.surfaceLight,
            onSurface = Color(0xFF1E152A),
            surfaceVariant = Color(0xFFEFEFF8),
            onSurfaceVariant = Color(0xFF5A5875),
            error = CoralRed,
            onError = Color.White
        )
    }

    val layoutDirection = if (settings.language == "en") LayoutDirection.Ltr else LayoutDirection.Rtl

    CompositionLocalProvider(
        LocalFontScale provides fontScale,
        LocalPanjganehTheme provides themePreset,
        LocalAppLanguage provides settings.language,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// برای سازگاری نام قبلی در جاهایی که استفاده شده است
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    PanjganehTheme(content = content)
}
