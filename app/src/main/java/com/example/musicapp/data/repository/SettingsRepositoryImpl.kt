package com.example.musicapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.musicapp.data.datastore.PreferenceKeys
import com.example.musicapp.domain.model.AppTheme
import com.example.musicapp.domain.model.FontScale
import com.example.musicapp.domain.model.UserPreferences
import com.example.musicapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            theme = prefs[PreferenceKeys.THEME]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() }
                ?: AppTheme.SYSTEM,
            language = prefs[PreferenceKeys.LANGUAGE] ?: "en",
            dynamicColor = prefs[PreferenceKeys.DYNAMIC_COLOR] ?: false,
            fontScale = prefs[PreferenceKeys.FONT_SCALE]?.let { runCatching { FontScale.valueOf(it) }.getOrNull() }
                ?: FontScale.NORMAL,
        )
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[PreferenceKeys.THEME] = theme.name }
    }

    override suspend fun setLanguage(languageTag: String) {
        dataStore.edit { it[PreferenceKeys.LANGUAGE] = languageTag }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[PreferenceKeys.DYNAMIC_COLOR] = enabled }
    }

    override suspend fun setFontScale(scale: FontScale) {
        dataStore.edit { it[PreferenceKeys.FONT_SCALE] = scale.name }
    }
}
