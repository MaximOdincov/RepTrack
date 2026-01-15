package com.example.reptrack.feature_backup.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.core.data.local.models.WorkoutExerciseDb
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
                updatedAt = timestampToLocalDateTime(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { timestampToLocalDateTime(it) }
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
            "updatedAt" to localDateTimeToTimestamp(exercise.updatedAt),
            "deletedAt" to exercise.deletedAt?.let { localDateTimeToTimestamp(it) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timestampToLocalDateTime(timestamp: Long?): LocalDateTime {
        return if (timestamp != null && timestamp > 0) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        } else {
            LocalDateTime.now()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun localDateTimeToTimestamp(ldt: LocalDateTime): Long {
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
