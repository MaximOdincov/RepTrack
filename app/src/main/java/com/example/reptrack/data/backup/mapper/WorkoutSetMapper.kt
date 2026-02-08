package com.example.reptrack.data.backup.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.data.local.models.WorkoutSetDb
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Маппер для преобразования Firestore документов в WorkoutSetDb и наоборот
 */
object WorkoutSetMapper {

    fun fromFirestore(doc: DocumentSnapshot): WorkoutSetDb? {
        return try {
            WorkoutSetDb(
                id = doc.id,
                workoutExerciseId = doc.getString("workoutExerciseId") ?: return null,
                setOrder = (doc.getLong("setOrder") ?: 0L).toInt(),
                weight = doc.getDouble("weight")?.toFloat(),
                reps = doc.getLong("reps")?.toInt(),
                isCompleted = doc.getBoolean("isCompleted") ?: false,
                updatedAt = timestampToLocalDateTime(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { timestampToLocalDateTime(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(set: WorkoutSetDb): Map<String, Any?> {
        return mapOf(
            "id" to set.id,
            "workoutExerciseId" to set.workoutExerciseId,
            "setOrder" to set.setOrder.toLong(),
            "weight" to set.weight?.toDouble(),
            "reps" to set.reps?.toLong(),
            "isCompleted" to set.isCompleted,
            "updatedAt" to localDateTimeToTimestamp(set.updatedAt),
            "deletedAt" to set.deletedAt?.let { localDateTimeToTimestamp(it) }
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
