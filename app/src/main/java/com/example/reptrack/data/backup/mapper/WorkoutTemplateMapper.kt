package com.example.reptrack.data.backup.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.data.local.models.WorkoutTemplateDb
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Маппер для преобразования Firestore документов в WorkoutTemplateDb и наоборот
 */
object WorkoutTemplateMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromFirestore(doc: DocumentSnapshot): WorkoutTemplateDb? {
        return try {
            WorkoutTemplateDb(
                id = doc.id,
                name = doc.getString("name") ?: return null,
                iconId = doc.getString("iconId"),
                week1Days = doc.getString("week1Days"),
                week2Days = doc.getString("week2Days"),
                updatedAt = timestampToLocalDateTime(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { timestampToLocalDateTime(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toFirestore(template: WorkoutTemplateDb): Map<String, Any?> {
        return mapOf(
            "id" to template.id,
            "name" to template.name,
            "iconId" to template.iconId,
            "week1Days" to template.week1Days,
            "week2Days" to template.week2Days,
            "updatedAt" to localDateTimeToTimestamp(template.updatedAt),
            "deletedAt" to template.deletedAt?.let { localDateTimeToTimestamp(it) }
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
