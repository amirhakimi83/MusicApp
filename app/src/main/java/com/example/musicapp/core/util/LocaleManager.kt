package com.example.musicapp.core.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Thin wrapper over [AppCompatDelegate] per-app locales.
 *
 * Changing the locale recreates the activity; because the manifest declares
 * `android:supportsRtl="true"`, Compose automatically flips layout direction
 * (RTL for Persian, LTR for English) — no manual layout-direction handling
 * is needed in screens.
 */
object LocaleManager {

    const val LANG_ENGLISH = "en"
    const val LANG_PERSIAN = "fa"

    /** Apply a language by tag ("en" / "fa"). Persisted automatically. */
    fun setLanguage(tag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }

    /** Current app language tag, defaulting to English when unset. */
    fun currentLanguage(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) LANG_ENGLISH else locales[0]?.language ?: LANG_ENGLISH
    }
}
