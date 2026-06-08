package com.example.reptrack.data.friends

import com.example.reptrack.data.local.dao.FriendDao
import com.example.reptrack.data.local.models.FriendDb
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.data.local.mappers.toFriendDb
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.domain.friends.FriendRepository
import com.example.reptrack.domain.friends.FriendStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class FriendRepositoryImpl(
    private val friendDao: FriendDao,
    private val firebaseFriendsDataSource: FirebaseFriendsDataSource
) : FriendRepository {

    override fun getFriends(userId: String): Flow<List<Friend>> {
        return friendDao.observeFriends(userId).map { friendsDb ->
            friendsDb.map { it.toDomain() }
        }
    }

    override suspend fun addFriend(
        userId: String,
        friendEmail: String,
        passkey: String
    ): Result<Friend> {
        android.util.Log.d("FriendRepositoryImpl", "addFriend called: userId=$userId, friendEmail=$friendEmail, passkey=$passkey")
        return try {
            // First, find the user by email in Firebase
            android.util.Log.d("FriendRepositoryImpl", "Finding user by email in Firebase...")
            val friendUser = firebaseFriendsDataSource.findUserByEmail(friendEmail)
                .getOrElse { error ->
                    android.util.Log.e("FriendRepositoryImpl", "Failed to find user: ${error.message}", error)
                    return Result.failure(error)
                }

            android.util.Log.d("FriendRepositoryImpl", "User found: id=${friendUser.id}, username=${friendUser.username}, email=${friendUser.email}, passkey=${friendUser.passkey}")

            // Verify the passkey
            android.util.Log.d("FriendRepositoryImpl", "Verifying passkey...")
            val isPasskeyValid = firebaseFriendsDataSource.verifyPasskey(friendUser.id, passkey)
                .getOrElse { error ->
                    android.util.Log.e("FriendRepositoryImpl", "Failed to verify passkey: ${error.message}", error)
                    return Result.failure(error)
                }

             android.util.Log.d("FriendRepositoryImpl", "Passkey valid: $isPasskeyValid")

            if (!isPasskeyValid) {
                android.util.Log.e("FriendRepositoryImpl", "Invalid passkey. User ID: ${friendUser.id}, Email: ${friendUser.email}, Passkey provided: $passkey")
                return Result.failure(Exception("Invalid passkey"))
            }

            // Check if already friends
            android.util.Log.d("FriendRepositoryImpl", "Checking if already friends...")
            val existingFriend = friendDao.getFriendByUserIds(userId, friendUser.id)
            if (existingFriend != null) {
                android.util.Log.e("FriendRepositoryImpl", "Already friends with this user")
                return Result.failure(Exception("Already friends with this user"))
            }

            // Send friend request (or accept automatically for simplicity)
            android.util.Log.d("FriendRepositoryImpl", "Sending friend request to Firebase...")
            firebaseFriendsDataSource.sendFriendRequest(userId, friendUser.id)
                .getOrElse { error ->
                    android.util.Log.e("FriendRepositoryImpl", "Failed to send friend request: ${error.message}", error)
                    // We can continue even if this fails for now
                }

            // Create friend record locally
            android.util.Log.d("FriendRepositoryImpl", "Creating friend record locally...")
            val friendDb = FriendDb(
                userId = userId,
                friendUserId = friendUser.id,
                username = friendUser.username,
                email = friendUser.email,
                avatarUrl = friendUser.avatarUrl,
                status = FriendStatus.ACCEPTED.name
            )

            val friendId = friendDao.insertFriend(friendDb)
            android.util.Log.d("FriendRepositoryImpl", "Friend inserted with ID: $friendId")

            val insertedFriend = friendDao.getFriendByUserIds(userId, friendUser.id)!!
            android.util.Log.d("FriendRepositoryImpl", "Friend added successfully: ${insertedFriend.toDomain()}")
            Result.success(insertedFriend.toDomain())
        } catch (e: Exception) {
            android.util.Log.e("FriendRepositoryImpl", "Error adding friend: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteFriend(userId: String, friendId: String): Result<Unit> {
        return try {
            android.util.Log.d("FriendRepositoryImpl", "deleteFriend called: userId=$userId, friendId=$friendId")
            
            // Find the friend record
            val friends = friendDao.observeFriends(userId).first()
            android.util.Log.d("FriendRepositoryImpl", "Friends count: ${friends.size}")
            friends.forEach { friend ->
                android.util.Log.d("FriendRepositoryImpl", "  Friend: id=${friend.id}, friendUserId=${friend.friendUserId}, username=${friend.username}")
            }
            val friendDb = friends.firstOrNull { it.friendUserId == friendId }

            if (friendDb == null) {
                android.util.Log.w("FriendRepositoryImpl", "Friend not found for deletion: $friendId")
                return Result.failure(Exception("Friend not found"))
            }

            android.util.Log.d("FriendRepositoryImpl", "Deleting friend from Firebase...")
            firebaseFriendsDataSource.deleteFriend(userId, friendId)
                .getOrElse { error ->
                    android.util.Log.e("FriendRepositoryImpl", "Failed to delete friend from Firebase: ${error.message}", error)
                    // Don't fail if Firebase deletion fails, still delete locally
                }

            android.util.Log.d("FriendRepositoryImpl", "Deleting friend from local database...")
            friendDao.deleteFriend(friendDb.id)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FriendRepositoryImpl", "Error deleting friend: ${e.message}", e)
            Result.failure(e)
        }
    }
}
