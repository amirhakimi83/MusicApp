package com.example.musicapp.domain.model

/** Repeat behaviour of the player queue. */
enum class RepeatMode { OFF, ONE, ALL }

/** Sleep-timer presets (minutes; 0 means "end of current track"). */
enum class SleepTimerOption(val minutes: Int) {
    OFF(-1),
    END_OF_TRACK(0),
    MIN_15(15),
    MIN_30(30),
    MIN_60(60),
}

/** Supported playback speeds. */
enum class PlaybackSpeed(val value: Float) {
    SLOW(0.5f),
    NORMAL(1.0f),
    FAST(1.5f),
    FASTEST(2.0f),
}
