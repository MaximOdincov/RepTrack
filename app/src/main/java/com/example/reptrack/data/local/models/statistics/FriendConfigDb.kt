package com.example.reptrack.data.local.models.statistics

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.reptrack.data.local.models.UserDb
import java.time.LocalDateTime

@Entity(
    tableName = "friend_configs",
    foreignKeys = [
        ForeignKey(
            entity = ChartTemplateDb::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserDb::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("templateId"), Index("userId")]
)
data class FriendConfigDb(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long,
    val userId: String,
    val friendId: String,
    val color: Long,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
