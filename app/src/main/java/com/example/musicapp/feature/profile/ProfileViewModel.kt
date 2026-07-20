package com.example.musicapp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val user: StateFlow<User?> = userRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    private val _premiumActivated = Channel<Unit>(Channel.BUFFERED)
    val premiumActivated: Flow<Unit> = _premiumActivated.receiveAsFlow()

    /** Persist a newly picked avatar image (content:// uri as string). */
    fun changeAvatar(uri: String) = viewModelScope.launch {
        userRepository.updateAvatar(uri)
    }

    fun upgradeToPremium() = viewModelScope.launch {
        userRepository.upgradeToPremium()
        _premiumActivated.send(Unit)
    }
}
