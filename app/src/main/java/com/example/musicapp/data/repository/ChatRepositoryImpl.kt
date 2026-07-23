package com.example.musicapp.data.repository

import com.example.musicapp.core.di.ApplicationScope
import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.local.ChatDao
import com.example.musicapp.data.local.ConversationEntity
import com.example.musicapp.data.local.toConversation
import com.example.musicapp.data.local.toEntity
import com.example.musicapp.data.local.toMessage
import com.example.musicapp.data.mock.MockCatalog
import com.example.musicapp.data.remote.ChatSocket
import com.example.musicapp.data.remote.ChatSocketEvent
import com.example.musicapp.domain.model.ChatMessage
import com.example.musicapp.domain.model.Conversation
import com.example.musicapp.domain.model.MessageStatus
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first chat: Room is the single source of truth for conversations and
 * messages, while [ChatSocket] provides the realtime layer (delivery/read
 * receipts, typing indicator and incoming messages). Socket frames are applied
 * back into Room so the UI only ever observes the database — never polling.
 */
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val socket: ChatSocket,
    @IoDispatcher private val io: CoroutineDispatcher,
    @ApplicationScope private val appScope: CoroutineScope,
) : ChatRepository {

    /** Per-conversation "peer is typing" state, fed by socket events. */
    private val typing = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    init {
        appScope.launch(io) {
            // Seed conversation list on first construction only — must not clobber
            // an existing row on later app launches (would wipe last message / unread count).
            MockCatalog.conversationsSeed.forEach { conv ->
                chatDao.insertConversationIfAbsent(
                    ConversationEntity(
                        id = conv.id,
                        participantId = conv.participant.id,
                        participantName = conv.participant.name,
                        participantUsername = conv.participant.username,
                        participantAvatar = conv.participant.avatarUrl,
                        lastMessageText = null,
                        lastTimestamp = System.currentTimeMillis(),
                        unreadCount = conv.unreadCount,
                    ),
                )
            }
        }
        // Apply realtime frames into Room / typing state.
        appScope.launch(io) {
            socket.events.collect { event -> handleEvent(event) }
        }
    }

    private suspend fun handleEvent(event: ChatSocketEvent) {
        when (event) {
            is ChatSocketEvent.StatusChanged ->
                chatDao.updateStatus(event.messageId, event.status.name)

            is ChatSocketEvent.Typing ->
                typing.update { it + (event.conversationId to event.isTyping) }

            is ChatSocketEvent.IncomingMessage -> {
                val message = event.message
                chatDao.upsertMessage(message.toEntity())
                chatDao.updateLastMessage(message.conversationId, message.preview(), message.timestamp)
                chatDao.incrementUnread(message.conversationId)
            }
        }
    }

    override fun getConversations(): Flow<List<Conversation>> =
        chatDao.observeConversations().map { list -> list.map { it.toConversation() } }.flowOn(io)

    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> =
        chatDao.observeMessages(conversationId).map { list -> list.map { it.toMessage() } }.flowOn(io)

    override suspend fun sendTextMessage(conversationId: String, text: String) = withContext(io) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = MockCatalog.currentUser.id,
            text = text,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENDING,
            isMine = true,
        )
        persistOutgoing(message)
    }

    override suspend fun sendSong(conversationId: String, song: Song) = withContext(io) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = MockCatalog.currentUser.id,
            sharedSong = song,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENDING,
            isMine = true,
        )
        persistOutgoing(message)
    }

    private suspend fun persistOutgoing(message: ChatMessage) {
        chatDao.upsertMessage(message.toEntity())
        chatDao.updateLastMessage(message.conversationId, message.preview(), message.timestamp)
        socket.send(message)
    }

    override fun observeTyping(conversationId: String): Flow<Boolean> =
        typing.map { it[conversationId] ?: false }.distinctUntilChanged()

    override suspend fun setTyping(conversationId: String, isTyping: Boolean) {
        socket.notifyTyping(conversationId, isTyping)
    }

    override suspend fun markConversationRead(conversationId: String) = withContext(io) {
        chatDao.clearUnread(conversationId)
        socket.markRead(conversationId)
    }

    override fun connect() = socket.connect()

    override fun disconnect() = socket.disconnect()

    private fun ChatMessage.preview(): String =
        if (isSongShare) "🎵 ${sharedSong?.title.orEmpty()}" else text.orEmpty()
}
