package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.statistics.ChartTemplateDb
import com.example.reptrack.data.backup.FirestoreConstants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

/**
 * Маппер для синхронизации ChartTemplateDb между Firestore и Room
 */
object ChartTemplateMapper {

    fun toFirestore(template: ChartTemplateDb): Map<String, Any?> {
        return mapOf(
            "id" to template.id,
            FirestoreConstants.FIELD_USER_ID to template.userId,
            "name" to template.name,
            "type" to template.type,
            "dateFrom" to template.dateFrom,
            "dateTo" to template.dateTo,
            FirestoreConstants.FIELD_UPDATED_AT to TimestampMapper.toTimestamp(template.updatedAt),
            FirestoreConstants.FIELD_DELETED_AT to template.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }

    fun fromFirestore(doc: DocumentSnapshot): ChartTemplateDb? {
        return try {
            ChartTemplateDb(
                id = doc.getLong("id")?.toInt()?.toLong() ?: 0L,
                userId = doc.getString(FirestoreConstants.FIELD_USER_ID) ?: return null,
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                dateFrom = doc.getLong("dateFrom") ?: 0L,
                dateTo = doc.getLong("dateTo") ?: 0L,
                updatedAt = doc.getLong(FirestoreConstants.FIELD_UPDATED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) } ?: java.time.LocalDateTime.now(),
                deletedAt = doc.getLong(FirestoreConstants.FIELD_DELETED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun fromFirestore(doc: QueryDocumentSnapshot): ChartTemplateDb? {
        return fromFirestore(doc as DocumentSnapshot)
    }
}
