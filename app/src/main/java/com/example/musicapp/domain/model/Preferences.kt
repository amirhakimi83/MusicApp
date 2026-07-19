package com.example.musicapp.domain.model

/** Theme mode chosen by the user (independent of system dark mode). */
enum class AppTheme { LIGHT, DARK, SYSTEM }

/** In-app text scaling option. */
enum class FontScale(val scale: Float) {
    SMALL(0.9f),
    NORMAL(1.0f),
    LARGE(1.15f),
}

/** All user-controlled preferences, backed by DataStore. */
data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: String = "en",
    val dynamicColor: Boolean = false,
    val fontScale: FontScale = FontScale.NORMAL,
)
