package com.example.reptrack.data.backup.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.data.local.models.WorkoutSessionDb
import com.example.reptrack.domain.backup.WorkoutStatus
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object WorkoutSessionMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromFirestore(doc: DocumentSnapshot, userId: String): WorkoutSessionDb? {
        return try {
            val dateStr = doc.getString("date") ?: return null
            WorkoutSessionDb(
                id = doc.id,
                userId = userId,
                date = LocalDateTime.parse(dateStr),
                status = WorkoutStatus.valueOf(doc.getString("status") ?: "PLANNED"),
                name = doc.getString("name") ?: "",
                durationSeconds = doc.getLong("durationSeconds") ?: 0L,
                comment = doc.getString("comment"),
                updatedAt = timestampToLocalDateTime(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { timestampToLocalDateTime(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toFirestore(session: WorkoutSessionDb): Map<String, Any?> {
        return mapOf(
            "id" to session.id,
            "userId" to session.userId,
            "date" to session.date.toString(),
            "status" to session.status.name,
            "name" to session.name,
            "durationSeconds" to session.durationSeconds,
            "comment" to session.comment,
            "updatedAt" to localDateTimeToTimestamp(session.updatedAt),
            "deletedAt" to session.deletedAt?.let { localDateTimeToTimestamp(it) }
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
