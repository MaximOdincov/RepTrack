package com.example.reptrack.domain.auth.usecases

import com.example.reptrack.domain.profile.User
import com.example.reptrack.data.auth.toDomain
import com.example.reptrack.domain.auth.AuthRepository

class SignInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.signIn(email, password)
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