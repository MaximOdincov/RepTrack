package com.example.reptrack.domain.friends

import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getFriends(userId: String): Flow<List<Friend>>
    suspend fun addFriend(userId: String, friendEmail: String, passkey: String): Result<Friend>
    suspend fun deleteFriend(userId: String, friendId: String): Result<Unit>
}
