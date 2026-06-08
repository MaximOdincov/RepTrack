package com.example.reptrack.data.auth

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseUserDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveUser(
        userId: String,
        username: String?,
        email: String?,
        avatarUrl: String?,
        passkey: String?
    ): Result<Unit> {
        return try {
            android.util.Log.d("FirebaseUserDataSource", "Saving user: userId=$userId, username=$username, email=$email, hasAvatar=${avatarUrl != null}, hasPasskey=${passkey != null}")
            firestore
                .collection("users")
                .document(userId)
                .set(
                    mapOf(
                        "id" to userId,
                        "username" to username,
                        "email" to email,
                        "avatarUrl" to avatarUrl,
                        "passkey" to passkey,
                        "createdAt" to com.google.firebase.Timestamp.now()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                )
                .await()

            android.util.Log.d("FirebaseUserDataSource", "User saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseUserDataSource", "Error saving user: ${e.message}", e)
            Result.failure(e)
        }
    }
}
