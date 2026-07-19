package com.example.musicapp.data.repository

import com.example.musicapp.core.di.ApplicationScope
import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.local.ChatDao
import com.example.musicapp.data.local.ConversationEntity
import com.example.musicapp.data.local.toConversation
import com.example.musicapp.data.local.toEntity
import com.example.musicapp.data.local.toMessage
import com.example.musicapp.data.mock.MockCatalog
import com.example.musicapp.domain.model.ChatMessage
import com.example.musicapp.domain.model.Conversation
import com.example.musicapp.domain.model.MessageStatus
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Basic offline-first chat backed by Room. The realtime layer (WebSocket,
 * delivered/read receipts, typing indicator) is added in Step 13; the contract
 * and persistence are established here so the UI can be built against them.
 */
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    @IoDispatcher private val io: CoroutineDispatcher,
    @ApplicationScope private val appScope: CoroutineScope,
) : ChatRepository {

    init {
        // Seed conversation list on first construction.
        appScope.launch(io) {
            MockCatalog.conversationsSeed.forEach { conv ->
                chatDao.upsertConversation(
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
            status = MessageStatus.SENT,
            isMine = true,
        )
        chatDao.upsertMessage(message.toEntity())
    }

    override suspend fun sendSong(conversationId: String, song: Song) = withContext(io) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = MockCatalog.currentUser.id,
            sharedSong = song,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT,
            isMine = true,
        )
        chatDao.upsertMessage(message.toEntity())
    }

    override fun observeTyping(conversationId: String): Flow<Boolean> = flowOf(false)

    override suspend fun setTyping(conversationId: String, isTyping: Boolean) {
        // No-op until the realtime layer is wired up in Step 13.
    }

    override suspend fun markConversationRead(conversationId: String) = withContext(io) {
        chatDao.clearUnread(conversationId)
    }

    override fun connect() {
        // Realtime socket connection is established in Step 13.
    }

    override fun disconnect() {
        // No-op for now.
    }
}
