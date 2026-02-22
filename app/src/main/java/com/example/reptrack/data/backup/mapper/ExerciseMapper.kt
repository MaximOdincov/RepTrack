package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.google.firebase.firestore.DocumentSnapshot

object ExerciseMapper {

    fun fromFirestore(doc: DocumentSnapshot): ExerciseDb? {
        return try {
            ExerciseDb(
                id = doc.id,
                name = doc.getString("name") ?: return null,
                muscleGroup = MuscleGroup.valueOf(doc.getString("muscleGroup") ?: "CHEST"),
                type = ExerciseType.valueOf(doc.getString("type") ?: "WEIGHT_REPS"),
                iconRes = doc.getLong("iconRes")?.toInt(),
                iconColor = doc.getString("iconColor"),
                backgroundRes = doc.getLong("backgroundRes")?.toInt(),
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
            "iconRes" to exercise.iconRes?.toLong(),
            "iconColor" to exercise.iconColor,
            "backgroundRes" to exercise.backgroundRes?.toLong(),
            "backgroundColor" to exercise.backgroundColor,
            "isCustom" to exercise.isCustom,
            "updatedAt" to TimestampMapper.toTimestamp(exercise.updatedAt),
            "deletedAt" to exercise.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
