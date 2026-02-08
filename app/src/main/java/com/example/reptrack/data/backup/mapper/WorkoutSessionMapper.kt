package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.WorkoutSessionDb
import com.example.reptrack.domain.workout.WorkoutStatus
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object WorkoutSessionMapper {

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
                updatedAt = TimestampMapper.fromTimestamp(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(session: WorkoutSessionDb): Map<String, Any?> {
        return mapOf(
            "id" to session.id,
            "userId" to session.userId,
            "date" to session.date.toString(),
            "status" to session.status.name,
            "name" to session.name,
            "durationSeconds" to session.durationSeconds,
            "comment" to session.comment,
            "updatedAt" to TimestampMapper.toTimestamp(session.updatedAt),
            "deletedAt" to session.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
