package com.example.reptrack.data.backup.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.domain.backup.ExerciseType
import com.example.reptrack.domain.backup.MuscleGroup
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object ExerciseMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromFirestore(doc: DocumentSnapshot): ExerciseDb? {
        return try {
            ExerciseDb(
                id = doc.id,
                name = doc.getString("name") ?: return null,
                muscleGroup = MuscleGroup.valueOf(doc.getString("muscleGroup") ?: "CHEST"),
                type = ExerciseType.valueOf(doc.getString("type") ?: "WEIGHT_REPS"),
                iconUrl = doc.getString("iconUrl"),
                iconColor = doc.getString("iconColor"),
                backgroundImageUrl = doc.getString("backgroundImageUrl"),
                backgroundColor = doc.getString("backgroundColor"),
                isCustom = doc.getBoolean("isCustom") ?: false,
                updatedAt = timestampToLocalDateTime(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { timestampToLocalDateTime(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toFirestore(exercise: ExerciseDb): Map<String, Any?> {
        return mapOf(
            "id" to exercise.id,
            "name" to exercise.name,
            "muscleGroup" to exercise.muscleGroup.name,
            "type" to exercise.type.name,
            "iconUrl" to exercise.iconUrl,
            "iconColor" to exercise.iconColor,
            "backgroundImageUrl" to exercise.backgroundImageUrl,
            "backgroundColor" to exercise.backgroundColor,
            "isCustom" to exercise.isCustom,
            "updatedAt" to localDateTimeToTimestamp(exercise.updatedAt),
            "deletedAt" to exercise.deletedAt?.let { localDateTimeToTimestamp(it) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timestampToLocalDateTime(timestamp: Long?): LocalDateTime {
        return if (timestamp != null && timestamp > 0) {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
            )
        } else {
            LocalDateTime.now()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun localDateTimeToTimestamp(ldt: LocalDateTime): Long {
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
