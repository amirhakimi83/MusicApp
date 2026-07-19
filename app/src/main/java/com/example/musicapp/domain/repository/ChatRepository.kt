package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.ChatMessage
import com.example.musicapp.domain.model.Conversation
import com.example.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Real-time direct messaging. Implementations use a WebSocket for live updates
 * and Room for offline persistence — never polling.
 */
interface ChatRepository {
    fun getConversations(): Flow<List<Conversation>>
    fun getMessages(conversationId: String): Flow<List<ChatMessage>>

    suspend fun sendTextMessage(conversationId: String, text: String)
    suspend fun sendSong(conversationId: String, song: Song)

    fun observeTyping(conversationId: String): Flow<Boolean>
    suspend fun setTyping(conversationId: String, isTyping: Boolean)
    suspend fun markConversationRead(conversationId: String)

    /** Open / close the realtime connection with the socket. */
    fun connect()
    fun disconnect()
}
