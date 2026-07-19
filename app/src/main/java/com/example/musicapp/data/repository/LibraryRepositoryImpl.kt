package com.example.musicapp.data.repository

import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.local.LibraryDao
import com.example.musicapp.data.local.toLikedEntity
import com.example.musicapp.data.local.toRecentEntity
import com.example.musicapp.data.local.toSong
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.LibraryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val libraryDao: LibraryDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : LibraryRepository {

    override fun getLikedSongs(): Flow<List<Song>> =
        libraryDao.observeLiked().map { list -> list.map { it.toSong() } }.flowOn(io)

    override fun isLiked(songId: String): Flow<Boolean> =
        libraryDao.isLiked(songId).flowOn(io)

    override suspend fun toggleLike(song: Song) = withContext(io) {
        if (libraryDao.isLikedOnce(song.id)) {
            libraryDao.unlike(song.id)
        } else {
            libraryDao.like(song.toLikedEntity())
        }
    }

    override fun getRecentlyPlayed(): Flow<List<Song>> =
        libraryDao.observeRecent().map { list -> list.map { it.toSong() } }.flowOn(io)

    override suspend fun addRecentlyPlayed(song: Song) = withContext(io) {
        libraryDao.addRecent(song.toRecentEntity())
    }

    override suspend fun clearRecentlyPlayed() = withContext(io) {
        libraryDao.clearRecent()
    }
}
