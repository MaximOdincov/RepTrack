package com.example.reptrack.data.friends

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class FirebaseUserInfo(
    val id: String,
    val username: String?,
    val email: String?,
    val avatarUrl: String?,
    val passkey: String?
)

class FirebaseFriendsDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun findUserByEmail(email: String): Result<FirebaseUserInfo> {
        android.util.Log.d("FirebaseFriendsDataSource", "findUserByEmail called: email=$email")
        return try {
            android.util.Log.d("FirebaseFriendsDataSource", "Querying Firestore for user with email=$email")
            val querySnapshot = firestore
                .collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            android.util.Log.d("FirebaseFriendsDataSource", "Query returned ${querySnapshot.size()} documents")

            if (querySnapshot.isEmpty) {
                android.util.Log.e("FirebaseFriendsDataSource", "User not found for email: $email")
                return Result.failure(Exception("User not found"))
            }

            val document = querySnapshot.documents[0]
            android.util.Log.d("FirebaseFriendsDataSource", "Document found: ${document.id}")

            val userInfo = FirebaseUserInfo(
                id = document.id,
                username = document.getString("username"),
                email = document.getString("email"),
                avatarUrl = document.getString("avatarUrl"),
                passkey = document.getString("passkey")
            )

            android.util.Log.d("FirebaseFriendsDataSource", "User info: username=${userInfo.username}, hasPasskey=${userInfo.passkey != null}")
            Result.success(userInfo)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseFriendsDataSource", "Error finding user by email: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun verifyPasskey(userId: String, passkey: String): Result<Boolean> {
        android.util.Log.d("FirebaseFriendsDataSource", "verifyPasskey called: userId=$userId, passkey=$passkey")
        return try {
            android.util.Log.d("FirebaseFriendsDataSource", "Getting document for userId=$userId")
            val document = firestore
                .collection("users")
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                android.util.Log.e("FirebaseFriendsDataSource", "User document not found: $userId")
                return Result.failure(Exception("User not found"))
            }

            val storedPasskey = document.getString("passkey")
            android.util.Log.d("FirebaseFriendsDataSource", "Stored passkey: $storedPasskey, provided: $passkey")
            val isValid = storedPasskey == passkey

            Result.success(isValid)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseFriendsDataSource", "Error verifying passkey: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun sendFriendRequest(userId: String, friendId: String): Result<Unit> {
        android.util.Log.d("FirebaseFriendsDataSource", "sendFriendRequest called: userId=$userId, friendId=$friendId")
        return try {
            // Store friend relationship in Firestore
            android.util.Log.d("FirebaseFriendsDataSource", "Storing friend relationship in Firestore...")
            firestore
                .collection("users")
                .document(userId)
                .collection("friends")
                .document(friendId)
                .set(
                    mapOf(
                        "friendId" to friendId,
                        "status" to "accepted",
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()

            android.util.Log.d("FirebaseFriendsDataSource", "Friend relationship stored successfully")

            // Also store reverse relationship
            firestore
                .collection("users")
                .document(friendId)
                .collection("friends")
                .document(userId)
                .set(
                    mapOf(
                        "friendId" to userId,
                        "status" to "accepted",
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()

            android.util.Log.d("FirebaseFriendsDataSource", "Reverse friend relationship stored successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseFriendsDataSource", "Error sending friend request: ${e.message}", e)
            Result.failure(e)
        }
    }
}
