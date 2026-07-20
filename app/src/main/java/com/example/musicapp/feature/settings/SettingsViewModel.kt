package com.example.musicapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.core.util.LocaleManager
import com.example.musicapp.domain.model.AppTheme
import com.example.musicapp.domain.model.FontScale
import com.example.musicapp.domain.model.UserPreferences
import com.example.musicapp.domain.repository.SettingsRepository
import com.example.musicapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the Settings screen. Preferences are persisted through [SettingsRepository]
 * (DataStore); the language change is additionally routed through [LocaleManager]
 * which recreates the activity so Compose re-reads the new locale (RTL/LTR).
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = settingsRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences(),
        )

    fun setTheme(theme: AppTheme) = viewModelScope.launch {
        settingsRepository.setTheme(theme)
    }

    fun setFontScale(scale: FontScale) = viewModelScope.launch {
        settingsRepository.setFontScale(scale)
    }

    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch {
        settingsRepository.setDynamicColor(enabled)
    }

    /** Persist the choice and apply it immediately (activity is recreated). */
    fun setLanguage(tag: String) {
        viewModelScope.launch { settingsRepository.setLanguage(tag) }
        LocaleManager.setLanguage(tag)
    }

    fun logout() = viewModelScope.launch {
        userRepository.logout()
    }
}
