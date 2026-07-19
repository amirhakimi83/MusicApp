package com.example.musicapp.domain.model

data class Conversation(
    val id: String,
    val participant: User,
    val lastMessage: ChatMessage? = null,
    val unreadCount: Int = 0,
    val isTyping: Boolean = false,
)
