package com.example.reptrack.core.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "users")
data class UserDb(
    @PrimaryKey val id: String,
    val isGuest: Boolean,
    val username: String?,
    val email: String?,
    val avatarUrl: String?,
    val currentWeight: Float?,
    val height: Float?,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
