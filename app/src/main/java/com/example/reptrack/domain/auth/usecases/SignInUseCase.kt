package com.example.reptrack.domain.auth.usecases

import com.example.reptrack.domain.workout.User
import com.example.reptrack.data.auth.toDomain
import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.profile.usecases.AddUserUseCase

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