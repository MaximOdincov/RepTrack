package com.example.reptrack.data.backup.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.data.local.models.TemplateExerciseDb
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Маппер для преобразования Firestore документов в TemplateExerciseDb и наоборот
 */
object TemplateExerciseMapper {

    fun fromFirestore(doc: DocumentSnapshot): TemplateExerciseDb? {
        return try {
            TemplateExerciseDb(
                templateId = doc.getString("templateId") ?: return null,
                exerciseId = doc.getString("exerciseId") ?: return null,
                updatedAt = TimestampMapper.fromTimestamp(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(templateExercise: TemplateExerciseDb): Map<String, Any?> {
        return mapOf(
            "templateId" to templateExercise.templateId,
            "exerciseId" to templateExercise.exerciseId,
            "updatedAt" to TimestampMapper.toTimestamp(templateExercise.updatedAt),
            "deletedAt" to templateExercise.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
