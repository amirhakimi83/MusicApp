package com.example.musicapp.feature.social

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.musicapp.R
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.ChatRepository
import com.example.musicapp.domain.repository.UserRepository
import com.example.musicapp.ui.components.DetailTopBar
import com.example.musicapp.ui.components.EmptyState
import com.example.musicapp.ui.components.UserRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    val users: StateFlow<List<User>> = userRepository.getAllOtherUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleFollow(user: User) = viewModelScope.launch {
        userRepository.toggleFollowUser(user.id)
    }

    /** Opens (creating if needed) a direct chat with [user] and hands back its conversation id. */
    fun openChat(user: User, onReady: (String) -> Unit) = viewModelScope.launch {
        val conversationId = chatRepository.getOrCreateConversationId(user.id)
        onReady(conversationId)
    }
}

@Composable
fun PeopleScreen(
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PeopleViewModel = hiltViewModel(),
) {
    val users by viewModel.users.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(title = stringResource(R.string.people_title), onBack = onBack)
        if (users.isEmpty()) {
            EmptyState(messageRes = R.string.empty_generic)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = users, key = { it.id }) { user ->
                    UserRow(
                        user = user,
                        onClick = { viewModel.openChat(user, onReady = onOpenChat) },
                        trailing = {
                            OutlinedButton(onClick = { viewModel.toggleFollow(user) }) {
                                Text(
                                    stringResource(
                                        if (user.isFollowed) R.string.action_unfollow
                                        else R.string.action_follow,
                                    ),
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
