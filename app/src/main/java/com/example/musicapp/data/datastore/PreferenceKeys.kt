package com.example.musicapp.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/** Keys for the single app-wide Preferences DataStore. */
object PreferenceKeys {
    val THEME = stringPreferencesKey("theme")
    val LANGUAGE = stringPreferencesKey("language")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val FONT_SCALE = stringPreferencesKey("font_scale")

    // User state
    val IS_PREMIUM = booleanPreferencesKey("is_premium")
    val AVATAR_URI = stringPreferencesKey("avatar_uri")
}
