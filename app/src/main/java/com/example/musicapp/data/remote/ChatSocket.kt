package com.example.musicapp.data.remote

import com.example.musicapp.domain.model.ChatMessage
import com.example.musicapp.domain.model.MessageStatus
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages the WebSocket connection for real-time chat functionality.
 * Handles establishing connections, receiving real-time messages, and graceful disconnections.
 */

/** Events pushed from the server to the client over the realtime connection. */
sealed interface ChatSocketEvent {
    /** A new message from the other participant. */
    data class IncomingMessage(val message: ChatMessage) : ChatSocketEvent

    /** The other participant started/stopped typing. */
    data class Typing(val conversationId: String, val isTyping: Boolean) : ChatSocketEvent

    /** Delivery lifecycle update for one of *our* outgoing messages. */
    data class StatusChanged(
        val conversationId: String,
        val messageId: String,
        val status: MessageStatus,
    ) : ChatSocketEvent
}

/**
 * Realtime transport for direct messaging. Modeled after a WebSocket: a single
 * hot [events] stream carries server→client pushes, while the `send*`/`notify*`
 * methods are client→server frames. The [FakeChatSocket] implementation
 * simulates the server locally; swapping in an OkHttp WebSocket later only
 * requires a new implementation of this interface.
 */
interface ChatSocket {
    val events: SharedFlow<ChatSocketEvent>
    val connected: StateFlow<Boolean>

    fun connect()
    fun disconnect()

    /** Send an outgoing message; the server answers with [ChatSocketEvent.StatusChanged]. */
    fun send(message: ChatMessage)

    /** Tell the server we are (not) typing in a conversation. */
    fun notifyTyping(conversationId: String, isTyping: Boolean)

    /** Tell the server we have read the peer's messages in this conversation. */
    fun markRead(conversationId: String)
}
