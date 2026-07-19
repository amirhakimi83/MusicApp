package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

/** The user's personal library: liked songs and play history (Room-backed). */
interface LibraryRepository {
    fun getLikedSongs(): Flow<List<Song>>
    fun isLiked(songId: String): Flow<Boolean>
    suspend fun toggleLike(song: Song)

    fun getRecentlyPlayed(): Flow<List<Song>>
    suspend fun addRecentlyPlayed(song: Song)
    suspend fun clearRecentlyPlayed()
}
