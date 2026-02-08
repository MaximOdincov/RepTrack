package com.example.reptrack.data.backup.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.data.local.models.statistics.SetConfigDb
import com.example.reptrack.data.backup.FirestoreConstants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

/**
 * Маппер для синхронизации SetConfigDb между Firestore и Room
 */
object SetConfigMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun toFirestore(config: SetConfigDb): Map<String, Any?> {
        return mapOf(
            "id" to config.id,
            "exerciseConfigId" to config.exerciseConfigId,
            "setIndex" to config.setIndex,
            "color" to config.color,
            "visible" to config.visible,
            FirestoreConstants.FIELD_UPDATED_AT to TimestampMapper.toTimestamp(config.updatedAt),
            FirestoreConstants.FIELD_DELETED_AT to config.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromFirestore(doc: DocumentSnapshot): SetConfigDb? {
        return try {
            SetConfigDb(
                id = doc.getLong("id")?.toInt()?.toLong() ?: 0L,
                exerciseConfigId = doc.getLong("exerciseConfigId")?.toInt()?.toLong() ?: 0L,
                setIndex = doc.getLong("setIndex")?.toInt() ?: 0,
                color = doc.getLong("color") ?: 0L,
                visible = doc.getBoolean("visible") ?: true,
                updatedAt = doc.getLong(FirestoreConstants.FIELD_UPDATED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) } ?: java.time.LocalDateTime.now(),
                deletedAt = doc.getLong(FirestoreConstants.FIELD_DELETED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) },
                userId = doc.getString(FirestoreConstants.FIELD_USER_ID) ?: return null
            )
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromFirestore(doc: QueryDocumentSnapshot): SetConfigDb? {
        return fromFirestore(doc as DocumentSnapshot)
    }
}
