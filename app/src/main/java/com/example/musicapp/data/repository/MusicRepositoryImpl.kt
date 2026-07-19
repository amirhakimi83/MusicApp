package com.example.musicapp.data.repository

import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.local.DownloadDao
import com.example.musicapp.data.local.LibraryDao
import com.example.musicapp.data.mock.MockCatalog
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.PlaylistCategory
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val libraryDao: LibraryDao,
    private val downloadDao: DownloadDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : MusicRepository {

    // Stable per-session selections so the Home feed doesn't reshuffle on recompose.
    private val dailyPicks = MockCatalog.songs.shuffled().take(10)
    private val newest = MockCatalog.songs.takeLast(15).reversed()
    private val popular = MockCatalog.songs.take(15)

    /**
     * Emits [base] enriched with the user's live liked/downloaded state so the
     * UI updates instantly when a song is liked or downloaded. A short delay
     * simulates network latency, exercising the shimmer loading states.
     */
    private fun songStateFlow(base: List<Song>): Flow<List<Song>> = combine(
        libraryDao.observeLikedIds(),
        downloadDao.observeAllByRecent(),
    ) { likedIds, downloads ->
        val liked = likedIds.toSet()
        val downloaded = downloads.map { it.songId }.toSet()
        base.map { it.copy(isLiked = it.id in liked, isDownloaded = it.id in downloaded) }
    }.flowOn(io)

    override fun getDailyPicks(): Flow<List<Song>> = songStateFlow(dailyPicks)
    override fun getNewestSongs(): Flow<List<Song>> = songStateFlow(newest)
    override fun getMostPopularSongs(): Flow<List<Song>> = songStateFlow(popular)

    override fun getTopArtists(): Flow<List<Artist>> = flow {
        delay(300)
        emit(MockCatalog.artists.sortedByDescending { it.followerCount }.take(10))
    }.flowOn(io)

    override fun getGlobalPlaylists(): Flow<List<Playlist>> = flow {
        delay(300); emit(MockCatalog.globalPlaylists)
    }.flowOn(io)

    override fun getLocalPlaylists(): Flow<List<Playlist>> = flow {
        delay(300); emit(MockCatalog.localPlaylists)
    }.flowOn(io)

    override fun getPlaylistsByCategory(category: PlaylistCategory): Flow<List<Playlist>> = flow {
        emit(MockCatalog.allPlaylists.filter { it.category == category })
    }.flowOn(io)

    override suspend fun getSongById(id: String): Song? {
        val song = MockCatalog.songById(id) ?: return null
        return song.copy(
            isLiked = libraryDao.isLikedOnce(id),
            isDownloaded = downloadDao.getLocalPath(id) != null,
            localFilePath = downloadDao.getLocalPath(id),
        )
    }

    override suspend fun getArtistById(id: String): Artist? = MockCatalog.artistById(id)

    override suspend fun getPlaylistById(id: String): Playlist? = MockCatalog.playlistById(id)

    override suspend fun getSongsByArtist(artistId: String): List<Song> =
        MockCatalog.songs.filter { it.artistId == artistId }
}
