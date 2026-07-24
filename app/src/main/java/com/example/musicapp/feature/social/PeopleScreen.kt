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

/**
 * Manage-only view of people the user already follows (mirrors [com.example.musicapp.feature.artist.FollowedArtistsScreen]).
 * Discovering *new* people to follow happens from the Search tab's People filter.
 */
@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val followedUsers: StateFlow<List<User>> = userRepository.getFollowedUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun unfollow(user: User) = viewModelScope.launch {
        userRepository.toggleFollowUser(user.id)
    }
}

@Composable
fun PeopleScreen(
    onBack: () -> Unit,
    onOpenFriend: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PeopleViewModel = hiltViewModel(),
) {
    val users by viewModel.followedUsers.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(title = stringResource(R.string.people_title), onBack = onBack)
        if (users.isEmpty()) {
            EmptyState(messageRes = R.string.empty_generic)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = users, key = { it.id }) { user ->
                    UserRow(
                        user = user,
                        onClick = { onOpenFriend(user.id) },
                        trailing = {
                            OutlinedButton(onClick = { viewModel.unfollow(user) }) {
                                Text(stringResource(R.string.action_unfollow))
                            }
                        },
                    )
                }
            }
        }
    }
}
