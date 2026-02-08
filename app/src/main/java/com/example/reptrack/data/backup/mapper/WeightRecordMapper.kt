package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.WeightRecordDb
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object WeightRecordMapper {

    fun fromFirestore(doc: DocumentSnapshot): WeightRecordDb? {
        return try {
            val dateStr = doc.getString("date") ?: return null
            WeightRecordDb(
                id = doc.id,
                date = LocalDateTime.parse(dateStr),
                value = (doc.getDouble("value") ?: 0.0).toFloat(),
                updatedAt = TimestampMapper.fromTimestamp(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) },
                userId = doc.getString("userId")?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(record: WeightRecordDb): Map<String, Any?> {
        return mapOf(
            "id" to record.id,
            "date" to record.date.toString(),
            "value" to record.value.toDouble(),
            "updatedAt" to TimestampMapper.toTimestamp(record.updatedAt),
            "deletedAt" to record.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
