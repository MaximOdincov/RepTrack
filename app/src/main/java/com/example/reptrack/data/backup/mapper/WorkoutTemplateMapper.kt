package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.WorkoutTemplateDb
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Маппер для преобразования Firestore документов в WorkoutTemplateDb и наоборот
 */
object WorkoutTemplateMapper {

    fun fromFirestore(doc: DocumentSnapshot): WorkoutTemplateDb? {
        return try {
            WorkoutTemplateDb(
                id = doc.id,
                name = doc.getString("name") ?: return null,
                description = doc.getString("description"),
                iconId = doc.getString("iconId"),
                iconRes = doc.getLong("iconRes")?.toInt(),
                iconColor = doc.getString("iconColor"),
                week1Days = doc.getString("week1Days"),
                week2Days = doc.getString("week2Days"),
                updatedAt = TimestampMapper.fromTimestamp(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(template: WorkoutTemplateDb): Map<String, Any?> {
        return mapOf(
            "id" to template.id,
            "name" to template.name,
            "description" to template.description,
            "iconId" to template.iconId,
            "iconRes" to template.iconRes?.toLong(),
            "iconColor" to template.iconColor,
            "week1Days" to template.week1Days,
            "week2Days" to template.week2Days,
            "updatedAt" to TimestampMapper.toTimestamp(template.updatedAt),
            "deletedAt" to template.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
