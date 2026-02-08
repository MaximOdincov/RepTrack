package com.example.reptrack.domain.auth.usecases

import com.example.reptrack.domain.workout.GdprConsent
import com.example.reptrack.domain.workout.User
import com.example.reptrack.data.auth.toDomain
import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.profile.usecases.AddUserUseCase

class SignInWithGoogleUseCase(
    private val repository: AuthRepository,
    private val addUserUseCase: AddUserUseCase
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return repository.signInWithGoogle(idToken)
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