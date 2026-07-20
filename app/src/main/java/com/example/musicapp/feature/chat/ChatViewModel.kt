package com.example.musicapp.feature.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.ChatMessage
import com.example.musicapp.domain.model.Conversation
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.ChatRepository
import com.example.musicapp.domain.repository.LibraryRepository
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    libraryRepository: LibraryRepository,
    musicRepository: MusicRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val conversationId: String =
        checkNotNull(savedStateHandle[Routes.ARG_CONVERSATION_ID])

    val messages: StateFlow<List<ChatMessage>> = chatRepository.getMessages(conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isPeerTyping: StateFlow<Boolean> = chatRepository.observeTyping(conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val conversation: StateFlow<Conversation?> = chatRepository.getConversations()
        .map { list -> list.firstOrNull { it.id == conversationId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Songs the user can attach: their liked songs plus the newest catalog picks. */
    val shareableSongs: StateFlow<List<Song>> = combine(
        libraryRepository.getLikedSongs(),
        musicRepository.getNewestSongs(),
    ) { liked, newest ->
        (liked + newest).distinctBy { it.id }.take(30)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private var typingJob: Job? = null

    init {
        chatRepository.connect()
        // Keep the conversation marked read while it is on screen.
        viewModelScope.launch {
            messages.collect { chatRepository.markConversationRead(conversationId) }
        }
    }

    fun sendText(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            typingJob?.cancel()
            chatRepository.setTyping(conversationId, false)
            chatRepository.sendTextMessage(conversationId, trimmed)
        }
    }

    fun sendSong(song: Song) {
        viewModelScope.launch { chatRepository.sendSong(conversationId, song) }
    }

    /** Called as the user types; reports typing, then auto-clears after a pause. */
    fun onUserTyping() {
        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            chatRepository.setTyping(conversationId, true)
            delay(1_500)
            chatRepository.setTyping(conversationId, false)
        }
    }
}
