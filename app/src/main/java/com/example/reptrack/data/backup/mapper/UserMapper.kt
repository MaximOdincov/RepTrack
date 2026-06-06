package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.UserDb
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Маппер для преобразования Firestore документов в UserDb и наоборот
 */
object UserMapper {

    fun fromFirestore(doc: DocumentSnapshot): UserDb? {
        return try {
            UserDb(
                id = doc.id,
                isGuest = doc.getBoolean("isGuest") ?: true,
                username = doc.getString("username"),
                email = doc.getString("email"),
                avatarUrl = doc.getString("avatarUrl"),
                currentWeight = doc.getDouble("currentWeight")?.toFloat(),
                height = doc.getDouble("height")?.toFloat(),
                passkey = doc.getString("passkey"),
                isDarkTheme = doc.getBoolean("isDarkTheme"),
                updatedAt = TimestampMapper.fromTimestamp(doc.getLong("updatedAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(user: UserDb): Map<String, Any?> {
        return mapOf(
            "id" to user.id,
            "isGuest" to user.isGuest,
            "username" to user.username,
            "email" to user.email,
            "avatarUrl" to user.avatarUrl,
            "currentWeight" to user.currentWeight?.toDouble(),
            "height" to user.height?.toDouble(),
            "passkey" to user.passkey,
            "isDarkTheme" to user.isDarkTheme,
            "updatedAt" to TimestampMapper.toTimestamp(user.updatedAt),
            "deletedAt" to user.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }
}
