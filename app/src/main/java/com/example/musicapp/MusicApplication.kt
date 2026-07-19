package com.example.musicapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point.
 *
 * - [HiltAndroidApp] bootstraps Hilt's dependency graph for the whole app.
 * - Implements [Configuration.Provider] so WorkManager uses the Hilt-aware
 *   [HiltWorkerFactory], allowing our download workers to receive injected
 *   dependencies (repositories, etc.).
 */
@HiltAndroidApp
class MusicApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
