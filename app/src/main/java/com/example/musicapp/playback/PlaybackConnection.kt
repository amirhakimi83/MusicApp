package com.example.musicapp.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicapp.core.di.ApplicationScope
import com.example.musicapp.domain.model.RepeatMode
import com.example.musicapp.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bridges the UI to [PlaybackService] through a [MediaController] and publishes
 * a single [PlaybackState] stream. Also keeps the current queue of [Song]s so
 * we can map the player's current index back to rich domain data.
 */
@Singleton
class PlaybackConnection @Inject constructor(
    @ApplicationContext context: Context,
    @ApplicationScope private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var controller: MediaController? = null
    private var queue: List<Song> = emptyList()
    private var positionJob: Job? = null

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            syncFromPlayer(player)
        }
    }

    init {
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener(
            {
                controller = future.get().also { it.addListener(listener) }
                controller?.let(::syncFromPlayer)
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    // ---- Commands ----

    /** Replace the queue with [songs] and start playing at [startIndex]. */
    fun play(songs: List<Song>, startIndex: Int = 0) {
        val c = controller ?: return
        queue = songs
        c.setMediaItems(songs.map { it.toMediaItem() }, startIndex, 0L)
        c.prepare()
        c.play()
    }

    /** Play a single song as its own queue. */
    fun play(song: Song) = play(listOf(song), 0)

    fun togglePlayPause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun next() {
        controller?.seekToNextMediaItem()
    }

    fun previous() {
        controller?.seekToPreviousMediaItem()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun setSpeed(speed: Float) {
        controller?.setPlaybackSpeed(speed)
    }

    fun cycleRepeatMode() {
        val c = controller ?: return
        c.repeatMode = when (c.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
    }

    fun toggleShuffle() {
        val c = controller ?: return
        c.shuffleModeEnabled = !c.shuffleModeEnabled
    }

    // ---- State syncing ----

    private fun syncFromPlayer(player: Player) {
        val index = player.currentMediaItemIndex
        val song = queue.getOrNull(index)
        _state.update {
            it.copy(
                currentSong = song,
                isPlaying = player.isPlaying,
                isBuffering = player.playbackState == Player.STATE_BUFFERING,
                durationMs = player.duration.coerceAtLeast(0L),
                repeatMode = when (player.repeatMode) {
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    else -> RepeatMode.OFF
                },
                shuffleEnabled = player.shuffleModeEnabled,
                speed = player.playbackParameters.speed,
            )
        }
        managePositionUpdates(player.isPlaying)
    }

    private fun managePositionUpdates(isPlaying: Boolean) {
        if (isPlaying) {
            if (positionJob?.isActive == true) return
            positionJob = scope.launch {
                while (true) {
                    controller?.let { c ->
                        _state.update { it.copy(positionMs = c.currentPosition.coerceAtLeast(0L)) }
                    }
                    delay(500)
                }
            }
        } else {
            positionJob?.cancel()
            positionJob = null
            controller?.let { c ->
                _state.update { it.copy(positionMs = c.currentPosition.coerceAtLeast(0L)) }
            }
        }
    }
}

/** Maps a domain [Song] to a media3 [MediaItem] (local file when downloaded). */
fun Song.toMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(id)
    .setUri(playbackUri)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artistName)
            .setArtworkUri(coverImageUrl.toUri())
            .build(),
    )
    .build()
