package com.example.reptrack.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.reptrack.domain.workout.WorkoutStatus
import java.time.LocalDateTime

@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserDb::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class WorkoutSessionDb(
    @PrimaryKey val id: String,
    val userId: String,
    val date: LocalDateTime,
    val status: WorkoutStatus,
    val name: String,
    val durationSeconds: Long,
    val comment: String?,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
