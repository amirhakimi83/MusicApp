package com.example.musicapp.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.DownloadState
import com.example.musicapp.domain.model.PlaybackSpeed
import com.example.musicapp.domain.model.SleepTimerOption
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.DownloadRepository
import com.example.musicapp.domain.repository.UserRepository
import com.example.musicapp.domain.usecase.DownloadRequestResult
import com.example.musicapp.domain.usecase.DownloadSongUseCase
import com.example.musicapp.playback.PlaybackConnection
import com.example.musicapp.playback.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** One-time UI signals from the player (consumed as effects). */
sealed interface PlayerEvent {
    data object PremiumRequired : PlayerEvent
    data object DownloadStarted : PlayerEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val connection: PlaybackConnection,
    private val downloadSong: DownloadSongUseCase,
    private val downloadRepository: DownloadRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = connection.state

    private val _sleepTimer = MutableStateFlow(SleepTimerOption.OFF)
    val sleepTimer: StateFlow<SleepTimerOption> = _sleepTimer.asStateFlow()

    private val _events = Channel<PlayerEvent>(Channel.BUFFERED)
    val events: Flow<PlayerEvent> = _events.receiveAsFlow()

    /** Download state of the currently playing song. */
    val currentDownloadState: StateFlow<DownloadState> = playbackState
        .map { it.currentSong?.id }
        .flatMapLatest { songId ->
            if (songId == null) flowOf(DownloadState.NotDownloaded)
            else downloadRepository.observeDownloadState(songId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DownloadState.NotDownloaded)

    private var sleepJob: Job? = null

    fun play(song: Song) = connection.play(song)
    fun play(songs: List<Song>, startIndex: Int) = connection.play(songs, startIndex)

    fun togglePlayPause() = connection.togglePlayPause()
    fun next() = connection.next()
    fun previous() = connection.previous()
    fun seekTo(positionMs: Long) = connection.seekTo(positionMs)
    fun cycleRepeatMode() = connection.cycleRepeatMode()
    fun toggleShuffle() = connection.toggleShuffle()
    fun setSpeed(speed: PlaybackSpeed) = connection.setSpeed(speed.value)

    fun onDownloadClick() {
        val song = playbackState.value.currentSong ?: return
        viewModelScope.launch {
            when (downloadSong(song)) {
                DownloadRequestResult.Started -> _events.send(PlayerEvent.DownloadStarted)
                DownloadRequestResult.PremiumRequired -> _events.send(PlayerEvent.PremiumRequired)
            }
        }
    }

    fun upgradeToPremium() = viewModelScope.launch {
        userRepository.upgradeToPremium()
    }

    fun setSleepTimer(option: SleepTimerOption) {
        sleepJob?.cancel()
        _sleepTimer.value = option
        if (option == SleepTimerOption.OFF) return

        val current = connection.state.value
        val delayMs = when (option) {
            SleepTimerOption.END_OF_TRACK ->
                (current.durationMs - current.positionMs).coerceAtLeast(0L)
            else -> option.minutes * 60_000L
        }
        sleepJob = viewModelScope.launch {
            delay(delayMs)
            connection.pause()
            _sleepTimer.value = SleepTimerOption.OFF
        }
    }
}
