package com.example.reptrack.data.local.models.statistics

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.reptrack.data.local.models.UserDb
import java.time.LocalDateTime

@Entity(
    tableName = "set_configs",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseLineConfigDb::class,
            parentColumns = ["id"],
            childColumns = ["exerciseConfigId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserDb::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseConfigId"), Index("userId")]
)
data class SetConfigDb(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseConfigId: Long,
    val userId: String,
    val setIndex: Int,
    val color: Long,
    val visible: Boolean,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
