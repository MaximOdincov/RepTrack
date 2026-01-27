package com.example.reptrack.feature_backup.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.core.data.local.models.GdprConsentDb
import com.example.reptrack.feature_backup.data.FirestoreConstants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

/**
 * Маппер для синхронизации GdprConsentDb между Firestore и Room
 */
object GdprConsentMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun toFirestore(consent: GdprConsentDb): Map<String, Any?> {
        return mapOf(
            FirestoreConstants.FIELD_USER_ID to consent.userId,
            "isAccepted" to consent.isAccepted,
            "acceptedAt" to consent.acceptedAt,
            FirestoreConstants.FIELD_UPDATED_AT to TimestampMapper.toTimestamp(consent.updatedAt),
            FirestoreConstants.FIELD_DELETED_AT to consent.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromFirestore(doc: QueryDocumentSnapshot): GdprConsentDb? {
        return fromFirestore(doc as DocumentSnapshot)
    }
}
