package com.example.reptrack.domain.profile

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun addUser(user: User)
    suspend fun deleteUser(userId: String)
    fun observeUser(userId: String): Flow<User?>
    suspend fun updateUser(user: User)
}