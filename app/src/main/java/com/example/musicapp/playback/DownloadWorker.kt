package com.example.musicapp.playback

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.musicapp.data.local.DownloadDao
import com.example.musicapp.data.local.DownloadEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Downloads a song's audio to internal storage (Premium feature). Reports
 * progress via [setProgress] and, on success, records a [DownloadEntity] with
 * the local file path so the app can play it offline.
 */
@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val downloadDao: DownloadDao,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val songId = inputData.getString(KEY_ID) ?: return@withContext Result.failure()
        val audioUrl = inputData.getString(KEY_AUDIO) ?: return@withContext Result.failure()

        try {
            val dir = File(applicationContext.filesDir, "downloads").apply { mkdirs() }
            val outFile = File(dir, "$songId.mp3")

            val connection = (URL(audioUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 20_000
            }
            connection.connect()
            val total = connection.contentLength.toLong()

            connection.inputStream.use { input ->
                outFile.outputStream().use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var downloaded = 0L
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        downloaded += read
                        if (total > 0) {
                            val percent = (downloaded * 100 / total).toInt()
                            setProgress(workDataOf(KEY_PROGRESS to percent))
                        }
                    }
                }
            }

            downloadDao.insert(
                DownloadEntity(
                    songId = songId,
                    title = inputData.getString(KEY_TITLE).orEmpty(),
                    artistName = inputData.getString(KEY_ARTIST).orEmpty(),
                    artistId = inputData.getString(KEY_ARTIST_ID).orEmpty(),
                    coverImageUrl = inputData.getString(KEY_COVER).orEmpty(),
                    audioUrl = audioUrl,
                    durationMs = inputData.getLong(KEY_DURATION, 0L),
                    album = inputData.getString(KEY_ALBUM),
                    localPath = outFile.absolutePath,
                    downloadedAt = System.currentTimeMillis(),
                ),
            )
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_ID = "id"
        const val KEY_TITLE = "title"
        const val KEY_ARTIST = "artist"
        const val KEY_ARTIST_ID = "artistId"
        const val KEY_COVER = "cover"
        const val KEY_AUDIO = "audio"
        const val KEY_DURATION = "duration"
        const val KEY_ALBUM = "album"
        const val KEY_PROGRESS = "progress"

        fun uniqueName(songId: String) = "download_$songId"
    }
}
