package com.example.musicapp.domain.model

/** Top-level grouping used by the Playlists tab. */
enum class PlaylistCategory { WORLD, LOCAL, USER }

data class Playlist(
    val id: String,
    val title: String,
    val coverImageUrl: String,
    val description: String? = null,
    val ownerId: String? = null,
    val ownerName: String? = null,
    val isPublic: Boolean = true,
    val category: PlaylistCategory = PlaylistCategory.WORLD,
    val songCount: Int = 0,
    val songs: List<Song> = emptyList(),
)
