package com.example.reptrack.feature_profile.domain

import com.example.reptrack.core.domain.entities.User

interface ProfileRepository {
    suspend fun addUser(user: User)
    suspend fun deleteUser(userId: String)
}