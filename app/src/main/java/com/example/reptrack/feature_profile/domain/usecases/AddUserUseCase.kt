package com.example.reptrack.feature_profile.domain.usecases

import com.example.reptrack.core.domain.entities.User
import com.example.reptrack.feature_profile.domain.ProfileRepository

class AddUserUseCase(
    private val repository: ProfileRepository
){
    suspend operator fun invoke(user: User){
        repository.addUser(user)
    }
}