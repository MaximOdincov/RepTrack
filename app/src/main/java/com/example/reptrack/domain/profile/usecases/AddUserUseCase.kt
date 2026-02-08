package com.example.reptrack.domain.profile.usecases

import com.example.reptrack.domain.workout.User
import com.example.reptrack.domain.profile.ProfileRepository

class AddUserUseCase(
    private val repository: ProfileRepository
){
    suspend operator fun invoke(user: User){
        repository.addUser(user)
    }
}