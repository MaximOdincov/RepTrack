package com.example.reptrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reptrack.data.local.models.FriendDb
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends WHERE userId = :userId AND deletedAt IS NULL ORDER BY createdAt DESC")
    fun observeFriends(userId: String): Flow<List<FriendDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendDb): Long

    @Query("UPDATE friends SET deletedAt = (strftime('%s','now') * 1000) WHERE id = :friendId")
    suspend fun deleteFriend(friendId: Long)

    @Query("SELECT * FROM friends WHERE userId = :userId AND friendUserId = :friendUserId AND deletedAt IS NULL")
    suspend fun getFriendByUserIds(userId: String, friendUserId: String): FriendDb?

    @Query("DELETE FROM friends WHERE deletedAt IS NOT NULL")
    suspend fun cleanupDeletedFriends()

    @Query("SELECT * FROM friends")
    suspend fun getAllFriends(): List<FriendDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendDb>)
}
