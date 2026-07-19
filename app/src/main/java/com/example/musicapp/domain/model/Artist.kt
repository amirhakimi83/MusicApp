package com.example.musicapp.domain.model

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String,
    val followerCount: Int = 0,
    val isFollowed: Boolean = false,
    val bio: String? = null,
)
