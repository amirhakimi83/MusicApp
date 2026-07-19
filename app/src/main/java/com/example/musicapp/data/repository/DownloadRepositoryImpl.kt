package com.example.musicapp.data.repository

import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.local.DownloadDao
import com.example.musicapp.data.local.toDownloadEntity
import com.example.musicapp.data.local.toSong
import com.example.musicapp.domain.model.DownloadSort
import com.example.musicapp.domain.model.DownloadState
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.DownloadRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NOTE: In this step downloads complete immediately (the local path points at
 * the stream URL) so the offline list is usable. Step 10 replaces
 * [enqueueDownload] with a real WorkManager job that copies bytes to disk and
 * reports progress via [observeDownloadState].
 */
@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val dao: DownloadDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : DownloadRepository {

    override fun getDownloadedSongs(sort: DownloadSort): Flow<List<Song>> {
        val source = when (sort) {
            DownloadSort.RECENT -> dao.observeAllByRecent()
            DownloadSort.TITLE -> dao.observeAllByTitle()
            DownloadSort.ARTIST -> dao.observeAllByArtist()
        }
        return source.map { list -> list.map { it.toSong() } }.flowOn(io)
    }

    override fun observeDownloadState(songId: String): Flow<DownloadState> =
        dao.observeExists(songId).map { existsAt ->
            if (existsAt != null) DownloadState.Completed else DownloadState.NotDownloaded
        }.flowOn(io)

    override suspend fun enqueueDownload(song: Song) = withContext(io) {
        dao.insert(song.toDownloadEntity(localPath = song.audioUrl))
    }

    override suspend fun removeDownload(songId: String) = withContext(io) { dao.delete(songId) }

    override suspend fun getLocalPath(songId: String): String? =
        withContext(io) { dao.getLocalPath(songId) }
}
