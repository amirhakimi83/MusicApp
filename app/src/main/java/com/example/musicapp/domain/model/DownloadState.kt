package com.example.musicapp.domain.model

/** Ordering options for the Downloads tab. */
enum class DownloadSort { RECENT, TITLE, ARTIST }

/** State of a single song's offline download. */
sealed interface DownloadState {
    data object NotDownloaded : DownloadState
    data class InProgress(val progress: Float) : DownloadState
    data object Completed : DownloadState
    data object Failed : DownloadState
}
