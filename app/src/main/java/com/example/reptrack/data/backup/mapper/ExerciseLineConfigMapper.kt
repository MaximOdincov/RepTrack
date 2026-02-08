package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.statistics.ExerciseLineConfigDb
import com.example.reptrack.data.backup.FirestoreConstants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

/**
 * Маппер для синхронизации ExerciseLineConfigDb между Firestore и Room
 */
object ExerciseLineConfigMapper {

    fun toFirestore(config: ExerciseLineConfigDb): Map<String, Any?> {
        return mapOf(
            "id" to config.id,
            "templateId" to config.templateId,
            "exerciseId" to config.exerciseId,
            FirestoreConstants.FIELD_UPDATED_AT to TimestampMapper.toTimestamp(config.updatedAt),
            FirestoreConstants.FIELD_DELETED_AT to config.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }

    fun fromFirestore(doc: DocumentSnapshot): ExerciseLineConfigDb? {
        return try {
            ExerciseLineConfigDb(
                id = doc.getLong("id")?.toInt()?.toLong() ?: 0L,
                templateId = doc.getLong("templateId")?.toInt()?.toLong() ?: 0L,
                exerciseId = doc.getString("exerciseId") ?: return null,
                updatedAt = doc.getLong(FirestoreConstants.FIELD_UPDATED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) } ?: java.time.LocalDateTime.now(),
                deletedAt = doc.getLong(FirestoreConstants.FIELD_DELETED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun fromFirestore(doc: QueryDocumentSnapshot): ExerciseLineConfigDb? {
        return fromFirestore(doc as DocumentSnapshot)
    }
}
