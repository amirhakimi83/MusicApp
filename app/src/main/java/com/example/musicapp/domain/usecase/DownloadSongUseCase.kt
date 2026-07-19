package com.example.musicapp.domain.usecase

import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.DownloadRepository
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Result of attempting to download a song. */
sealed interface DownloadRequestResult {
    data object Started : DownloadRequestResult
    /** Free users can't download offline — the UI should prompt an upgrade. */
    data object PremiumRequired : DownloadRequestResult
}

/**
 * Encapsulates the core business rule: offline download is a Premium-only
 * feature. Free users are told to upgrade instead of starting a download.
 */
class DownloadSongUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val downloadRepository: DownloadRepository,
) {
    suspend operator fun invoke(song: Song): DownloadRequestResult {
        val isPremium = userRepository.getCurrentUser().first().isPremium
        return if (isPremium) {
            downloadRepository.enqueueDownload(song)
            DownloadRequestResult.Started
        } else {
            DownloadRequestResult.PremiumRequired
        }
    }
}
