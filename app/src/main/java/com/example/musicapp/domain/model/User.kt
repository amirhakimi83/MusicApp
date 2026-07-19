package com.example.musicapp.domain.model

data class User(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String? = null,
    val isPremium: Boolean = false,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowed: Boolean = false,
)
