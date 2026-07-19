package com.example.musicapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.ui.MelodiaApp
import com.example.musicapp.ui.theme.MusicAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. Extends [AppCompatActivity] so the AppCompat per-app
 * language backport applies on Android 12 and below. All UI is Compose.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicAppTheme {
                MelodiaApp()
            }
        }
    }
}
