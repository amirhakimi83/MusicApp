package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.PlaylistCategory
import com.example.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

/** Read access to the music catalog (songs, artists, playlists). */
interface MusicRepository {
    fun getDailyPicks(): Flow<List<Song>>
    fun getNewestSongs(): Flow<List<Song>>
    fun getMostPopularSongs(): Flow<List<Song>>
    fun getTopArtists(): Flow<List<Artist>>
    fun getGlobalPlaylists(): Flow<List<Playlist>>
    fun getLocalPlaylists(): Flow<List<Playlist>>
    fun getPlaylistsByCategory(category: PlaylistCategory): Flow<List<Playlist>>

    suspend fun getSongById(id: String): Song?
    suspend fun getArtistById(id: String): Artist?
    suspend fun getPlaylistById(id: String): Playlist?
    suspend fun getSongsByArtist(artistId: String): List<Song>
}
