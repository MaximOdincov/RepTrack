package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.GdprConsentDb
import com.example.reptrack.data.backup.FirestoreConstants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

/**
 * Маппер для синхронизации GdprConsentDb между Firestore и Room
 */
object GdprConsentMapper {

    fun toFirestore(consent: GdprConsentDb): Map<String, Any?> {
        return mapOf(
            FirestoreConstants.FIELD_USER_ID to consent.userId,
            "isAccepted" to consent.isAccepted,
            "acceptedAt" to consent.acceptedAt,
            FirestoreConstants.FIELD_UPDATED_AT to TimestampMapper.toTimestamp(consent.updatedAt),
            FirestoreConstants.FIELD_DELETED_AT to consent.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }

    fun fromFirestore(doc: DocumentSnapshot): GdprConsentDb? {
        return try {
            GdprConsentDb(
                userId = doc.getString(FirestoreConstants.FIELD_USER_ID) ?: return null,
                isAccepted = doc.getBoolean("isAccepted") ?: false,
                acceptedAt = doc.getLong("acceptedAt") ?: 0L,
                updatedAt = doc.getLong(FirestoreConstants.FIELD_UPDATED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) } ?: java.time.LocalDateTime.now(),
                deletedAt = doc.getLong(FirestoreConstants.FIELD_DELETED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun fromFirestore(doc: QueryDocumentSnapshot): GdprConsentDb? {
        return fromFirestore(doc as DocumentSnapshot)
    }
}
