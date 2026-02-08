package com.example.reptrack.data.backup.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Маппер для преобразования Firestore документов в WorkoutExerciseDb и наоборот
 */
object WorkoutExerciseMapper {

    fun fromFirestore(doc: DocumentSnapshot): WorkoutExerciseDb? {
        return try {
            WorkoutExerciseDb(
                id = doc.id,
                workoutSessionId = doc.getString("workoutSessionId") ?: return null,
                exerciseId = doc.getString("exerciseId") ?: return null,
                restTimerSeconds = (doc.getLong("restTimerSeconds") ?: 0L).toInt(),
                updatedAt = TimestampMapper.fromTimestamp(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(exercise: WorkoutExerciseDb): Map<String, Any?> {
        return mapOf(
            "id" to exercise.id,
            "workoutSessionId" to exercise.workoutSessionId,
            "exerciseId" to exercise.exerciseId,
            "restTimerSeconds" to exercise.restTimerSeconds.toLong(),
            "updatedAt" to TimestampMapper.toTimestamp(exercise.updatedAt),
            "deletedAt" to exercise.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
