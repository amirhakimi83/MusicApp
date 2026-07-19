package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.AppTheme
import com.example.musicapp.domain.model.FontScale
import com.example.musicapp.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/** User preferences persisted via DataStore. */
interface SettingsRepository {
    val preferences: Flow<UserPreferences>
    suspend fun setTheme(theme: AppTheme)
    suspend fun setLanguage(languageTag: String)
    suspend fun setDynamicColor(enabled: Boolean)
    suspend fun setFontScale(scale: FontScale)
}
