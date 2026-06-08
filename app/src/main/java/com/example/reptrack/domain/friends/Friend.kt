package com.example.reptrack.domain.friends

import java.time.LocalDateTime

data class Friend(
    val id: String,
    val userId: String,
    val friendUserId: String,
    val username: String?,
    val email: String?,
    val avatarUrl: String?,
    val status: FriendStatus,
    val createdAt: LocalDateTime
)

enum class FriendStatus {
    PENDING,
    ACCEPTED,
    BLOCKED
}
