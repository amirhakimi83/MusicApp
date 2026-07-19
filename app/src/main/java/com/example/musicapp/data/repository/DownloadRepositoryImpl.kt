package com.example.musicapp.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.local.DownloadDao
import com.example.musicapp.data.local.toSong
import com.example.musicapp.domain.model.DownloadSort
import com.example.musicapp.domain.model.DownloadState
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.DownloadRepository
import com.example.musicapp.playback.DownloadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: DownloadDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : DownloadRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun getDownloadedSongs(sort: DownloadSort): Flow<List<Song>> {
        val source = when (sort) {
            DownloadSort.RECENT -> dao.observeAllByRecent()
            DownloadSort.TITLE -> dao.observeAllByTitle()
            DownloadSort.ARTIST -> dao.observeAllByArtist()
        }
        return source.map { list -> list.map { it.toSong() } }.flowOn(io)
    }

    override fun observeDownloadState(songId: String): Flow<DownloadState> {
        val existsFlow = dao.observeExists(songId)
        val workFlow = workManager.getWorkInfosForUniqueWorkFlow(DownloadWorker.uniqueName(songId))
        return combine(existsFlow, workFlow) { existsAt, workInfos ->
            when {
                existsAt != null -> DownloadState.Completed
                else -> {
                    val info = workInfos.firstOrNull()
                    when (info?.state) {
                        WorkInfo.State.RUNNING -> {
                            val progress = info.progress.getInt(DownloadWorker.KEY_PROGRESS, 0)
                            DownloadState.InProgress(progress / 100f)
                        }
                        WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED ->
                            DownloadState.InProgress(0f)
                        WorkInfo.State.FAILED -> DownloadState.Failed
                        else -> DownloadState.NotDownloaded
                    }
                }
            }
        }.flowOn(io)
    }

    override suspend fun enqueueDownload(song: Song) {
        val data = Data.Builder()
            .putString(DownloadWorker.KEY_ID, song.id)
            .putString(DownloadWorker.KEY_TITLE, song.title)
            .putString(DownloadWorker.KEY_ARTIST, song.artistName)
            .putString(DownloadWorker.KEY_ARTIST_ID, song.artistId)
            .putString(DownloadWorker.KEY_COVER, song.coverImageUrl)
            .putString(DownloadWorker.KEY_AUDIO, song.audioUrl)
            .putLong(DownloadWorker.KEY_DURATION, song.durationMs)
            .putString(DownloadWorker.KEY_ALBUM, song.album)
            .build()

        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            DownloadWorker.uniqueName(song.id),
            ExistingWorkPolicy.KEEP,
            request,
        )
    }

    override suspend fun removeDownload(songId: String) = withContext(io) {
        workManager.cancelUniqueWork(DownloadWorker.uniqueName(songId))
        dao.getLocalPath(songId)?.let { path -> runCatching { File(path).delete() } }
        dao.delete(songId)
    }

    override suspend fun getLocalPath(songId: String): String? =
        withContext(io) { dao.getLocalPath(songId) }
}
