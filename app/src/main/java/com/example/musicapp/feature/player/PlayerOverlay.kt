package com.example.musicapp.feature.player

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.ui.theme.spacing

/**
 * Top-level player layer. Shows the mini-player above the bottom bar and, when
 * tapped, morphs into the full Now Playing screen using a shared-element
 * transition on the cover art.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerOverlay(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.playbackState.collectAsStateWithLifecycle()
    val sleepTimer by viewModel.sleepTimer.collectAsStateWithLifecycle()

    if (!state.hasSong) return

    var expanded by remember { mutableStateOf(false) }
    BackHandler(enabled = expanded) { expanded = false }

    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        val sharedScope = this
        AnimatedContent(
            targetState = expanded,
            label = "player",
            transitionSpec = { fadeIn() togetherWith fadeOut() },
        ) { isExpanded ->
            val coverModifier = with(sharedScope) {
                Modifier.sharedElement(
                    rememberSharedContentState(key = "player-cover"),
                    animatedVisibilityScope = this@AnimatedContent,
                )
            }

            if (isExpanded) {
                NowPlayingScreen(
                    state = state,
                    sleepTimer = sleepTimer,
                    coverModifier = coverModifier,
                    onCollapse = { expanded = false },
                    onTogglePlay = viewModel::togglePlayPause,
                    onNext = viewModel::next,
                    onPrevious = viewModel::previous,
                    onSeek = viewModel::seekTo,
                    onCycleRepeat = viewModel::cycleRepeatMode,
                    onToggleShuffle = viewModel::toggleShuffle,
                    onSetSpeed = viewModel::setSpeed,
                    onSetSleepTimer = viewModel::setSleepTimer,
                )
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    MiniPlayer(
                        state = state,
                        coverModifier = coverModifier,
                        onTogglePlay = viewModel::togglePlayPause,
                        onExpand = { expanded = true },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(horizontal = MaterialTheme.spacing.small)
                            .padding(bottom = MaterialTheme.spacing.bottomBarHeight),
                    )
                }
            }
        }
    }
}
