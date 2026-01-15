package com.example.reptrack.feature_backup.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.core.data.local.models.statistics.FriendConfigDb
import com.example.reptrack.feature_backup.data.FirestoreConstants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

/**
 * Маппер для синхронизации FriendConfigDb между Firestore и Room
 */
object FriendConfigMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun toFirestore(config: FriendConfigDb): Map<String, Any?> {
        return mapOf(
            "id" to config.id,
            "templateId" to config.templateId,
            "friendId" to config.friendId,
            "color" to config.color,
            FirestoreConstants.FIELD_UPDATED_AT to TimestampMapper.toTimestamp(config.updatedAt),
            FirestoreConstants.FIELD_DELETED_AT to config.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromFirestore(doc: DocumentSnapshot): FriendConfigDb? {
        return try {
            FriendConfigDb(
                id = doc.getLong("id")?.toInt()?.toLong() ?: 0L,
                templateId = doc.getLong("templateId")?.toInt()?.toLong() ?: 0L,
                friendId = doc.getString("friendId") ?: return null,
                color = doc.getLong("color") ?: 0L,
                updatedAt = doc.getLong(FirestoreConstants.FIELD_UPDATED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) } ?: java.time.LocalDateTime.now(),
                deletedAt = doc.getLong(FirestoreConstants.FIELD_DELETED_AT)
                    ?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun fromFirestore(doc: QueryDocumentSnapshot): FriendConfigDb? {
        return fromFirestore(doc as DocumentSnapshot)
    }
}
