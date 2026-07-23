package com.example.musicapp.domain.model

/**
 * Represents a single audio track within the application.
 * Contains essential metadata required for playback and UI representation.
 */
data class Song(
    val id: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val coverImageUrl: String,
    val audioUrl: String,
    val durationMs: Long = 0L,
    val album: String? = null,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
) {
    /** True when a playable offline copy exists on this device. */
    val hasLocalCopy: Boolean get() = isDownloaded && !localFilePath.isNullOrBlank()

    /** Prefer the local file when available, otherwise stream. */
    val playbackUri: String get() = if (hasLocalCopy) localFilePath!! else audioUrl
}
