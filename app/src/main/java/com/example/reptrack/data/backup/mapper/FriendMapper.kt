package com.example.reptrack.data.backup.mapper

import com.example.reptrack.data.local.models.FriendDb
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Маппер для преобразования Firestore документов в FriendDb и наоборот
 */
object FriendMapper {

    fun fromFirestore(doc: DocumentSnapshot): FriendDb? {
        return try {
            FriendDb(
                id = doc.getLong("id")?.toLong() ?: 0,
                userId = doc.getString("userId") ?: return null,
                friendUserId = doc.getString("friendUserId") ?: return null,
                username = doc.getString("username"),
                email = doc.getString("email"),
                avatarUrl = doc.getString("avatarUrl"),
                status = doc.getString("status") ?: "pending",
                createdAt = TimestampMapper.fromTimestamp(doc.getLong("createdAt")),
                deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toFirestore(friend: FriendDb): Map<String, Any?> {
        return mapOf(
            "id" to friend.id,
            "userId" to friend.userId,
            "friendUserId" to friend.friendUserId,
            "username" to friend.username,
            "email" to friend.email,
            "avatarUrl" to friend.avatarUrl,
            "status" to friend.status,
            "createdAt" to TimestampMapper.toTimestamp(friend.createdAt),
            "deletedAt" to friend.deletedAt?.let { TimestampMapper.toTimestamp(it) }
        )
    }

    fun getCreatedAt(friend: FriendDb): java.time.LocalDateTime = friend.createdAt
}
