package com.example.musicapp.feature.player

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.domain.model.DownloadState
import com.example.musicapp.domain.model.PlaybackSpeed
import com.example.musicapp.domain.model.RepeatMode
import com.example.musicapp.domain.model.SleepTimerOption
import com.example.musicapp.playback.PlaybackState
import com.example.musicapp.ui.components.NetworkImage
import com.example.musicapp.ui.theme.AppGradients
import com.example.musicapp.ui.theme.spacing
import kotlinx.coroutines.isActive

@Composable
fun NowPlayingScreen(
    state: PlaybackState,
    sleepTimer: SleepTimerOption,
    downloadState: DownloadState,
    coverModifier: Modifier,
    onCollapse: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onSetSpeed: (PlaybackSpeed) -> Unit,
    onSetSleepTimer: (SleepTimerOption) -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val song = state.currentSong ?: return
    val dominant = rememberDominantColor(song.coverImageUrl, MaterialTheme.colorScheme.primary)
    val background = AppGradients.player(dominant, MaterialTheme.colorScheme.surface)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .systemBarsPadding()
            .padding(horizontal = MaterialTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top bar: collapse
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onCollapse) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.cd_close_player),
                    tint = Color.White,
                )
            }
            Text(
                text = stringResource(R.string.player_now_playing),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            ShareButton(title = song.title, artistName = song.artistName)
            DownloadButton(state = downloadState, onClick = onDownloadClick)
        }

        Spacer(modifier = Modifier.weight(0.5f))

        RotatingCover(
            coverUrl = song.coverImageUrl,
            title = song.title,
            isPlaying = state.isPlaying,
            coverModifier = coverModifier,
        )

        Spacer(modifier = Modifier.weight(0.5f))

        AudioVisualizer(
            isPlaying = state.isPlaying,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        // Title & artist
        Text(
            text = song.title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = song.artistName,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        SeekBar(
            positionMs = state.positionMs,
            durationMs = state.durationMs,
            onSeek = onSeek,
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        PlayerControls(
            isPlaying = state.isPlaying,
            repeatMode = state.repeatMode,
            shuffleEnabled = state.shuffleEnabled,
            onTogglePlay = onTogglePlay,
            onNext = onNext,
            onPrevious = onPrevious,
            onCycleRepeat = onCycleRepeat,
            onToggleShuffle = onToggleShuffle,
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        ExtraControls(
            speed = state.speed,
            sleepTimer = sleepTimer,
            onSetSpeed = onSetSpeed,
            onSetSleepTimer = onSetSleepTimer,
        )

        Spacer(modifier = Modifier.weight(0.3f))
    }
}

@Composable
private fun RotatingCover(
    coverUrl: String,
    title: String,
    isPlaying: Boolean,
    coverModifier: Modifier,
) {
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                rotation.animateTo(
                    targetValue = rotation.value + 360f,
                    animationSpec = tween(durationMillis = 12_000, easing = LinearEasing),
                )
            }
        }
    }

    Box(contentAlignment = Alignment.Center) {
        NetworkImage(
            url = coverUrl,
            contentDescription = title,
            modifier = coverModifier
                .fillMaxWidth(0.72f)
                .aspectRatio(1f)
                .graphicsLayer { rotationZ = rotation.value }
                .clip(CircleShape),
        )
        // Vinyl center hole
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
        )
    }
}

@Composable
private fun ShareButton(title: String, artistName: String) {
    val context = LocalContext.current
    val message = stringResource(R.string.share_song_message, title, artistName)
    IconButton(
        onClick = {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(sendIntent, null))
        },
    ) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = stringResource(R.string.action_share),
            tint = Color.White,
        )
    }
}

@Composable
private fun DownloadButton(
    state: DownloadState,
    onClick: () -> Unit,
) {
    when (state) {
        is DownloadState.InProgress -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        }
        DownloadState.Completed -> {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.DownloadDone,
                    contentDescription = stringResource(R.string.action_download),
                    tint = Color.White,
                )
            }
        }
        else -> {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = stringResource(R.string.action_download),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun SeekBar(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
) {
    var dragValue by remember { mutableStateOf<Float?>(null) }
    val max = durationMs.coerceAtLeast(1L).toFloat()
    val value = (dragValue ?: positionMs.toFloat()).coerceIn(0f, max)

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = value,
            onValueChange = { dragValue = it },
            onValueChangeFinished = {
                dragValue?.let { onSeek(it.toLong()) }
                dragValue = null
            },
            valueRange = 0f..max,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(formatTime(value.toLong()), style = MaterialTheme.typography.labelSmall, color = Color.White)
            Text(formatTime(durationMs), style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
    }
}

@Composable
private fun PlayerControls(
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    shuffleEnabled: Boolean,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onToggleShuffle) {
            Icon(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.action_shuffle),
                tint = if (shuffleEnabled) Color.White else Color.White.copy(alpha = 0.5f),
            )
        }
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.SkipPrevious, stringResource(R.string.action_previous), tint = Color.White)
        }
        FilledIconButton(
            onClick = onTogglePlay,
            modifier = Modifier.size(72.dp),
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = stringResource(
                    if (isPlaying) R.string.action_pause else R.string.action_play,
                ),
                modifier = Modifier.size(36.dp),
            )
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.SkipNext, stringResource(R.string.action_next), tint = Color.White)
        }
        IconButton(onClick = onCycleRepeat) {
            Icon(
                imageVector = if (repeatMode == RepeatMode.ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                contentDescription = null,
                tint = if (repeatMode == RepeatMode.OFF) Color.White.copy(alpha = 0.5f) else Color.White,
            )
        }
    }
}

@Composable
private fun ExtraControls(
    speed: Float,
    sleepTimer: SleepTimerOption,
    onSetSpeed: (PlaybackSpeed) -> Unit,
    onSetSleepTimer: (SleepTimerOption) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Sleep timer menu
        var sleepMenu by remember { mutableStateOf(false) }
        Box {
            TextButton(onClick = { sleepMenu = true }) {
                Icon(Icons.Filled.Bedtime, contentDescription = null, tint = Color.White)
                Spacer(Modifier.size(4.dp))
                Text(stringResource(R.string.player_sleep_timer), color = Color.White)
            }
            DropdownMenu(expanded = sleepMenu, onDismissRequest = { sleepMenu = false }) {
                sleepTimerOptions.forEach { (option, labelRes) ->
                    DropdownMenuItem(
                        text = { Text(stringResource(labelRes)) },
                        onClick = { onSetSleepTimer(option); sleepMenu = false },
                    )
                }
            }
        }

        // Speed menu
        var speedMenu by remember { mutableStateOf(false) }
        Box {
            TextButton(onClick = { speedMenu = true }) {
                Icon(Icons.Filled.Speed, contentDescription = null, tint = Color.White)
                Spacer(Modifier.size(4.dp))
                Text("${speed}x", color = Color.White)
            }
            DropdownMenu(expanded = speedMenu, onDismissRequest = { speedMenu = false }) {
                PlaybackSpeed.entries.forEach { s ->
                    DropdownMenuItem(
                        text = { Text("${s.value}x") },
                        onClick = { onSetSpeed(s); speedMenu = false },
                    )
                }
            }
        }
    }
}

private val sleepTimerOptions = listOf(
    SleepTimerOption.OFF to R.string.sleep_timer_off,
    SleepTimerOption.MIN_15 to R.string.sleep_timer_15,
    SleepTimerOption.MIN_30 to R.string.sleep_timer_30,
    SleepTimerOption.MIN_60 to R.string.sleep_timer_60,
    SleepTimerOption.END_OF_TRACK to R.string.sleep_timer_end_of_track,
)

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
