package com.example.musicapp.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.PlaybackSpeed
import com.example.musicapp.domain.model.SleepTimerOption
import com.example.musicapp.domain.model.Song
import com.example.musicapp.playback.PlaybackConnection
import com.example.musicapp.playback.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val connection: PlaybackConnection,
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = connection.state

    private val _sleepTimer = MutableStateFlow(SleepTimerOption.OFF)
    val sleepTimer: StateFlow<SleepTimerOption> = _sleepTimer.asStateFlow()

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
