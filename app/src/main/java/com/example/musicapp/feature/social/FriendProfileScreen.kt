package com.example.musicapp.feature.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.musicapp.R
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.ChatRepository
import com.example.musicapp.domain.repository.UserRepository
import com.example.musicapp.ui.components.DetailTopBar
import com.example.musicapp.ui.components.EmptyState
import com.example.musicapp.ui.components.NetworkImage
import com.example.musicapp.ui.components.SongRow
import com.example.musicapp.ui.components.UserAvatar
import com.example.musicapp.ui.navigation.Routes
import com.example.musicapp.ui.theme.AppCorners
import com.example.musicapp.ui.theme.spacing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle[Routes.ARG_USER_ID])

    val user: StateFlow<User?> = userRepository.getAllOtherUsers()
        .map { list -> list.firstOrNull { it.id == userId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val playlists: StateFlow<List<Playlist>> = userRepository.getUserPublicPlaylists(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleFollow() = viewModelScope.launch {
        userRepository.toggleFollowUser(userId)
    }

    /** Opens (creating if needed) a direct chat with this user. */
    fun openChat(onReady: (String) -> Unit) = viewModelScope.launch {
        onReady(chatRepository.getOrCreateConversationId(userId))
    }
}

@Composable
fun FriendProfileScreen(
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onPlaySong: (Song) -> Unit,
    onPlayList: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FriendProfileViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(title = user?.name.orEmpty(), onBack = onBack)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                FriendHeader(
                    user = user,
                    onToggleFollow = viewModel::toggleFollow,
                    onMessage = { viewModel.openChat(onOpenChat) },
                )
            }
            if (playlists.isEmpty()) {
                item { EmptyState(messageRes = R.string.empty_generic) }
            } else {
                playlists.forEach { playlist ->
                    item(key = "header_${playlist.id}") {
                        FriendPlaylistHeader(
                            playlist = playlist,
                            onPlayAll = { onPlayList(playlist.songs, 0) },
                        )
                    }
                    items(items = playlist.songs, key = { it.id }) { song ->
                        SongRow(song = song, onClick = { onPlaySong(song) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendHeader(
    user: User?,
    onToggleFollow: () -> Unit,
    onMessage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        UserAvatar(
            url = user?.avatarUrl,
            contentDescription = user?.name,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape),
        )
        Text(
            text = user?.name.orEmpty(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = user?.username?.let { "@$it" }.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.format_follower_count, user?.followerCount ?: 0),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
            if (user?.isFollowed == true) {
                OutlinedButton(onClick = onToggleFollow) {
                    Text(stringResource(R.string.action_unfollow))
                }
            } else {
                Button(onClick = onToggleFollow) {
                    Text(stringResource(R.string.action_follow))
                }
            }
            OutlinedButton(onClick = onMessage) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.spacing.iconSmall),
                )
                Text(
                    text = stringResource(R.string.action_message),
                    modifier = Modifier.padding(start = MaterialTheme.spacing.extraSmall),
                )
            }
        }
    }
}

@Composable
private fun FriendPlaylistHeader(playlist: Playlist, onPlayAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.screen,
                vertical = MaterialTheme.spacing.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
    ) {
        NetworkImage(
            url = playlist.coverImageUrl,
            contentDescription = playlist.title,
            modifier = Modifier
                .size(56.dp)
                .clip(AppCorners.cover),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = pluralStringResource(R.plurals.song_count, playlist.songCount, playlist.songCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onPlayAll) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.action_play_all),
            )
        }
    }
}
