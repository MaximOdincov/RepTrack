package com.example.reptrack.domain.profile

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun addUser(user: User)
    suspend fun deleteUser(userId: String)
    fun observeUser(userId: String): Flow<User?>
    suspend fun updateUser(user: User)
    suspend fun updateUsername(username: String, userId: String)
    suspend fun updateUsernameInFirebase(username: String, userId: String)
    suspend fun updatePasskey(passkey: String, userId: String)
    suspend fun updateFirebaseProfile(username: String?, email: String?, avatarUrl: String?, passkey: String?, userId: String)
    }
