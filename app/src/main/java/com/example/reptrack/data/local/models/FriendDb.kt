package com.example.reptrack.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.reptrack.data.local.models.UserDb
import java.time.LocalDateTime

@Entity(
    tableName = "friends",
    foreignKeys = [
        ForeignKey(
            entity = UserDb::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("friendUserId")]
)
data class FriendDb(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val friendUserId: String,
    val username: String?,
    val email: String?,
    val avatarUrl: String?,
    val status: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
