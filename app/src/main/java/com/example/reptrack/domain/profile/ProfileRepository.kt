package com.example.reptrack.domain.profile

import com.example.reptrack.domain.workout.User

interface ProfileRepository {
    suspend fun addUser(user: User)
    suspend fun deleteUser(userId: String)
}