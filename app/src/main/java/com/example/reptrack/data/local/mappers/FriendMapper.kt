package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.models.FriendDb
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.domain.friends.FriendStatus

fun FriendDb.toDomain(): Friend =
    Friend(
        id = friendUserId,
        userId = userId,
        friendUserId = friendUserId,
        username = username,
        email = email,
        avatarUrl = avatarUrl,
        status = try {
            FriendStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            FriendStatus.PENDING
        },
        createdAt = createdAt
    )

fun Friend.toFriendDb(): FriendDb =
    FriendDb(
        userId = userId,
        friendUserId = friendUserId,
        username = username,
        email = email,
        avatarUrl = avatarUrl,
        status = status.name,
        createdAt = createdAt
    )
