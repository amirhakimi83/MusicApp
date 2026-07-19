package com.example.musicapp.playback

import com.example.musicapp.domain.model.RepeatMode
import com.example.musicapp.domain.model.Song

/** Snapshot of everything the player UI needs to render. */
data class PlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleEnabled: Boolean = false,
    val speed: Float = 1f,
) {
    val hasSong: Boolean get() = currentSong != null

    val progress: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}
