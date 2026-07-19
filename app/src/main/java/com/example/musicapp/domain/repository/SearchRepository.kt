package com.example.musicapp.domain.repository

import androidx.paging.PagingData
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

/** Catalog search. Song results are paged (Paging 3) for long result sets. */
interface SearchRepository {
    fun searchSongs(query: String): Flow<PagingData<Song>>
    fun searchArtists(query: String): Flow<List<Artist>>
    fun searchPlaylists(query: String): Flow<List<Playlist>>
}

/** Persisted recent search queries (Room-backed). */
interface SearchHistoryRepository {
    fun getHistory(): Flow<List<String>>
    suspend fun addQuery(query: String)
    suspend fun removeQuery(query: String)
    suspend fun clearHistory()
}
