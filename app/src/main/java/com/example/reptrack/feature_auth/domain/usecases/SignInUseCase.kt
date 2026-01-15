package com.example.reptrack.feature_auth.domain.usecases

import com.example.reptrack.core.domain.entities.User
import com.example.reptrack.feature_auth.data.toDomain
import com.example.reptrack.feature_auth.domain.AuthRepository
import com.example.reptrack.feature_profile.domain.usecases.AddUserUseCase

class SignInUseCase(
    private val repository: AuthRepository,
    private val addUserUseCase: AddUserUseCase
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.signIn(email, password)
            .fold(
                onSuccess = { authUser ->
                    runCatching {
                        addUserUseCase(authUser.toDomain())
                        authUser.toDomain()
                    }
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
    }
}