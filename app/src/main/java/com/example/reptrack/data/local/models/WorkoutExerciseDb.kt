package com.example.reptrack.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionDb::class,
            parentColumns = ["id"],
            childColumns = ["workoutSessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutSessionId")]
)
data class WorkoutExerciseDb(
    @PrimaryKey val id: String,
    val workoutSessionId: String,
    val exerciseId: String,  // Для справки, но не используется в ForeignKey
    // Денормализованные данные из Exercise (для независимости)
    val exerciseName: String,
    val muscleGroup: String,  // Храним как String (enum name)
    val exerciseType: String,  // Храним как String (enum name)
    val iconRes: Int?,
    val restTimerSeconds: Int,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)

