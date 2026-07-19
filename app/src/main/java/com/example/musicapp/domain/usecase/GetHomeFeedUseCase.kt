package com.example.musicapp.domain.usecase

import com.example.musicapp.domain.model.HomeFeed
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Assembles all Home-tab sections into a single [HomeFeed] stream so the
 * ViewModel observes one source of truth instead of six.
 */
class GetHomeFeedUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(): Flow<HomeFeed> = combine(
        musicRepository.getDailyPicks(),
        musicRepository.getNewestSongs(),
        musicRepository.getMostPopularSongs(),
        musicRepository.getGlobalPlaylists(),
        musicRepository.getLocalPlaylists(),
    ) { daily, newest, popular, global, local ->
        HomeFeed(
            dailyPicks = daily,
            newest = newest,
            mostPopular = popular,
            globalPlaylists = global,
            localPlaylists = local,
        )
    }.combine(musicRepository.getTopArtists()) { feed, artists ->
        feed.copy(topArtists = artists)
    }
}
