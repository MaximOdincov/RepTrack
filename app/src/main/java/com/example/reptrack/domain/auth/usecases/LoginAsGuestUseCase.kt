package com.example.reptrack.domain.auth.usecases

import com.example.reptrack.domain.profile.User
import com.example.reptrack.data.auth.toDomain
import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.auth.AuthUser

class LoginAsGuestUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.signInAsGuest()
            .fold(
                onSuccess = { authUser ->
                    Result.success(authUser.toDomain())
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
    }
}