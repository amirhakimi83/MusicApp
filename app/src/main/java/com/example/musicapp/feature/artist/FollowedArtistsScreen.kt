package com.example.musicapp.feature.artist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.repository.UserRepository
import com.example.musicapp.ui.components.ArtistRow
import com.example.musicapp.ui.components.DetailTopBar
import com.example.musicapp.ui.components.EmptyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowedArtistsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val artists: StateFlow<List<Artist>> = userRepository.getFollowedArtists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun unfollow(artist: Artist) = viewModelScope.launch {
        userRepository.toggleFollowArtist(artist.id)
    }
}

@Composable
fun FollowedArtistsScreen(
    onBack: () -> Unit,
    onArtistClick: (Artist) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FollowedArtistsViewModel = hiltViewModel(),
) {
    val artists by viewModel.artists.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(title = stringResource(R.string.artists_followed), onBack = onBack)
        if (artists.isEmpty()) {
            EmptyState(messageRes = R.string.empty_generic)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = artists, key = { it.id }) { artist ->
                    ArtistRow(
                        artist = artist,
                        onClick = { onArtistClick(artist) },
                        trailing = {
                            OutlinedButton(onClick = { viewModel.unfollow(artist) }) {
                                Text(stringResource(R.string.action_unfollow))
                            }
                        },
                    )
                }
            }
        }
    }
}
