package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.domain.workout.ExerciseType
import com.example.reptrack.domain.workout.MuscleGroup
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object ExerciseMapper {

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
                updatedAt = TimestampMapper.fromTimestamp(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

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
            "updatedAt" to TimestampMapper.toTimestamp(exercise.updatedAt),
            "deletedAt" to exercise.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
