package com.example.musicapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.domain.model.AppTheme
import com.example.musicapp.feature.settings.SettingsViewModel
import com.example.musicapp.ui.MelodiaApp
import com.example.musicapp.ui.theme.MusicAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. Extends [AppCompatActivity] so the AppCompat per-app
 * language backport applies on Android 12 and below. All UI is Compose.
 *
 * Theme mode, dynamic color and font scale are driven by the user's persisted
 * preferences (DataStore) read here at the root so they apply app-wide.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val prefs by settingsViewModel.preferences.collectAsStateWithLifecycle()

            val darkTheme = when (prefs.theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }
            // Determine the active theme based on user preferences, falling back to the system default if necessary
            MusicAppTheme(darkTheme = darkTheme, dynamicColor = prefs.dynamicColor) {
                // Apply the in-app font scale on top of the system scale.
                val density = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = density.density,
                        fontScale = density.fontScale * prefs.fontScale.scale,
                    )
                ) {
                    // Enable edge-to-edge display to draw the UI behind system bars for a modern look
                    MelodiaApp()
                }
            }
        }
    }
}
