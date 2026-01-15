package com.example.reptrack.feature_backup.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.core.data.local.models.WeightRecordDb
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object WeightRecordMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromFirestore(doc: DocumentSnapshot): WeightRecordDb? {
        return try {
            val dateStr = doc.getString("date") ?: return null
            WeightRecordDb(
                id = doc.id,
                date = LocalDateTime.parse(dateStr),
                value = (doc.getDouble("value") ?: 0.0).toFloat(),
                updatedAt = timestampToLocalDateTime(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { timestampToLocalDateTime(it) }
            )
        } catch (e: Exception) {
            null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun toFirestore(record: WeightRecordDb): Map<String, Any?> {
        return mapOf(
            "id" to record.id,
            "date" to record.date.toString(),
            "value" to record.value.toDouble(),
            "updatedAt" to localDateTimeToTimestamp(record.updatedAt),
            "deletedAt" to record.deletedAt?.let { localDateTimeToTimestamp(it) }
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
