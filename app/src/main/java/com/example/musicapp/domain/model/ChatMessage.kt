package com.example.musicapp.domain.model

/** Delivery lifecycle of a chat message (single tick → double tick → read). */
enum class MessageStatus { SENDING, SENT, DELIVERED, READ, FAILED }

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String? = null,
    /** When non-null the message is a shared song rendered as a mini card. */
    val sharedSong: Song? = null,
    val timestamp: Long,
    val status: MessageStatus = MessageStatus.SENT,
    val isMine: Boolean = false,
) {
    val isSongShare: Boolean get() = sharedSong != null
}
