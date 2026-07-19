package com.example.musicapp.domain.model

/** Aggregated content for the Home tab, assembled by GetHomeFeedUseCase. */
data class HomeFeed(
    val dailyPicks: List<Song> = emptyList(),
    val newest: List<Song> = emptyList(),
    val mostPopular: List<Song> = emptyList(),
    val globalPlaylists: List<Playlist> = emptyList(),
    val localPlaylists: List<Playlist> = emptyList(),
    val topArtists: List<Artist> = emptyList(),
)
