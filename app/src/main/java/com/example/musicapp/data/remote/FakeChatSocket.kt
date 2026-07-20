package com.example.musicapp.data.remote

import com.example.musicapp.core.di.ApplicationScope
import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.mock.MockCatalog
import com.example.musicapp.domain.model.ChatMessage
import com.example.musicapp.domain.model.MessageStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * A locally-simulated realtime chat server. It mimics what a real backend would
 * push over a WebSocket:
 *  - our outgoing messages progress SENT → DELIVERED → READ with small delays;
 *  - the peer then "types" for a moment and sends an automatic reply.
 *
 * Everything is emitted on [events] exactly like server frames, so the repository
 * never has to know it is talking to a fake.
 */
@Singleton
class FakeChatSocket @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    @IoDispatcher private val io: CoroutineDispatcher,
) : ChatSocket {

    private val _events = MutableSharedFlow<ChatSocketEvent>(extraBufferCapacity = 64)
    override val events: SharedFlow<ChatSocketEvent> = _events.asSharedFlow()

    private val _connected = MutableStateFlow(false)
    override val connected: StateFlow<Boolean> = _connected.asStateFlow()

    override fun connect() {
        _connected.value = true
    }

    override fun disconnect() {
        _connected.value = false
    }

    override fun send(message: ChatMessage) {
        if (!_connected.value) return
        scope.launch(io) {
            // Server acknowledges and delivers our message.
            delay(250)
            emit(ChatSocketEvent.StatusChanged(message.conversationId, message.id, MessageStatus.SENT))
            delay(450)
            emit(ChatSocketEvent.StatusChanged(message.conversationId, message.id, MessageStatus.DELIVERED))

            // The peer reads it shortly after.
            delay(900)
            emit(ChatSocketEvent.StatusChanged(message.conversationId, message.id, MessageStatus.READ))

            // The peer types, then replies.
            delay(500)
            emit(ChatSocketEvent.Typing(message.conversationId, isTyping = true))
            delay(Random.nextLong(1200, 2200))
            emit(ChatSocketEvent.Typing(message.conversationId, isTyping = false))
            emit(ChatSocketEvent.IncomingMessage(buildReply(message.conversationId)))
        }
    }

    override fun notifyTyping(conversationId: String, isTyping: Boolean) {
        // A real server would broadcast this to the peer; nothing to simulate here.
    }

    override fun markRead(conversationId: String) {
        // A real server would notify the peer that we read their messages.
    }

    private fun emit(event: ChatSocketEvent) {
        _events.tryEmit(event)
    }

    private fun buildReply(conversationId: String): ChatMessage {
        val senderId = MockCatalog.conversationsSeed
            .firstOrNull { it.id == conversationId }?.participant?.id ?: "peer"
        return ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = senderId,
            text = CANNED_REPLIES.random(),
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.DELIVERED,
            isMine = false,
        )
    }

    private companion object {
        val CANNED_REPLIES = listOf(
            "Nice one! 🎶",
            "Adding this to my playlist right now.",
            "Haven't heard this in ages 😍",
            "Great taste as always.",
            "Let's listen together later?",
            "🔥🔥🔥",
            "Sending you one back in a sec.",
            "This is my jam!",
        )
    }
}
