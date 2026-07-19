package com.example.musicapp.domain.model

/**
 * Core music entity. The five fields required by the spec
 * (id, title, artist_name, cover_image_url, audio_url) are always present;
 * the rest are enriched locally (like/download state, duration, album).
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
