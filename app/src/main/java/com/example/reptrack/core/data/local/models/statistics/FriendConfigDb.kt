package com.example.reptrack.core.data.local.models.statistics

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "friend_configs",
    foreignKeys = [ForeignKey(
        entity = ChartTemplateDb::class,
        parentColumns = ["id"], childColumns = ["templateId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FriendConfigDb(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long,
    val friendId: String,
    val color: Long,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)