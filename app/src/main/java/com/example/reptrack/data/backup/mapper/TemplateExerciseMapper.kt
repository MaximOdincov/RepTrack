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
                updatedAt = timestampToLocalDateTime(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { timestampToLocalDateTime(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(templateExercise: TemplateExerciseDb): Map<String, Any?> {
        return mapOf(
            "templateId" to templateExercise.templateId,
            "exerciseId" to templateExercise.exerciseId,
            "updatedAt" to localDateTimeToTimestamp(templateExercise.updatedAt),
            "deletedAt" to templateExercise.deletedAt?.let { localDateTimeToTimestamp(it) }
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
