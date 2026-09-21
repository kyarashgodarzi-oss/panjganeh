package com.panjganeh.game.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "panjganeh_preferences")

data class AppSettings(
    val themeId: Int = 0,
    val textSize: String = "medium", // "small", "medium", "large"
    val darkMode: String = "dark",   // "light", "dark", "system"
    val language: String = "fa",     // "fa", "en"
    val soundEffects: Boolean = true,
    val music: Boolean = true,
    val vibration: Boolean = true,
    val notifications: Boolean = true
)

class AppSettingsDataStore(private val context: Context) {

    companion object {
        val THEME_ID_KEY = intPreferencesKey("theme_id")
        val TEXT_SIZE_KEY = stringPreferencesKey("text_size")
        val DARK_MODE_KEY = stringPreferencesKey("dark_mode")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val SOUND_EFFECTS_KEY = booleanPreferencesKey("sound_effects")
        val MUSIC_KEY = booleanPreferencesKey("music")
        val VIBRATION_KEY = booleanPreferencesKey("vibration")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")
    }

    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeId = prefs[THEME_ID_KEY] ?: 0,
            textSize = prefs[TEXT_SIZE_KEY] ?: "medium",
            darkMode = prefs[DARK_MODE_KEY] ?: "dark",
            language = prefs[LANGUAGE_KEY] ?: "fa",
            soundEffects = prefs[SOUND_EFFECTS_KEY] ?: true,
            music = prefs[MUSIC_KEY] ?: true,
            vibration = prefs[VIBRATION_KEY] ?: true,
            notifications = prefs[NOTIFICATIONS_KEY] ?: true
        )
    }

    suspend fun setThemeId(themeId: Int) {
        context.dataStore.edit { prefs ->
            prefs[THEME_ID_KEY] = themeId
        }
    }

    suspend fun setTextSize(textSize: String) {
        context.dataStore.edit { prefs ->
            prefs[TEXT_SIZE_KEY] = textSize
        }
    }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = mode
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language
        }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SOUND_EFFECTS_KEY] = enabled
        }
    }

    suspend fun setMusic(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MUSIC_KEY] = enabled
        }
    }

    suspend fun setVibration(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[VIBRATION_KEY] = enabled
        }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_KEY] = enabled
        }
    }
}
