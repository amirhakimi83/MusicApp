package com.example.musicapp.feature.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.PlaylistCategory
import com.example.musicapp.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlaylistsUiState(
    val world: List<Playlist> = emptyList(),
    val local: List<Playlist> = emptyList(),
    val user: List<Playlist> = emptyList(),
)

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    musicRepository: MusicRepository,
) : ViewModel() {

    val uiState: StateFlow<PlaylistsUiState> = combine(
        musicRepository.getPlaylistsByCategory(PlaylistCategory.WORLD),
        musicRepository.getPlaylistsByCategory(PlaylistCategory.LOCAL),
        musicRepository.getPlaylistsByCategory(PlaylistCategory.USER),
    ) { world, local, user ->
        PlaylistsUiState(world = world, local = local, user = user)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlaylistsUiState())
}
