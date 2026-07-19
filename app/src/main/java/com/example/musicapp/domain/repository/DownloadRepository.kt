package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.DownloadSort
import com.example.musicapp.domain.model.DownloadState
import com.example.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

/** Offline downloads (Premium feature). Actual work runs via WorkManager. */
interface DownloadRepository {
    fun getDownloadedSongs(sort: DownloadSort): Flow<List<Song>>
    fun observeDownloadState(songId: String): Flow<DownloadState>
    suspend fun enqueueDownload(song: Song)
    suspend fun removeDownload(songId: String)
    /** Local file path for an already-downloaded song, or null. */
    suspend fun getLocalPath(songId: String): String?
}
