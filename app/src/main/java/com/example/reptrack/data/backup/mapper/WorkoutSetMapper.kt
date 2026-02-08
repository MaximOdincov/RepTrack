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
                updatedAt = TimestampMapper.fromTimestamp(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
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
            "updatedAt" to TimestampMapper.toTimestamp(set.updatedAt),
            "deletedAt" to set.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
