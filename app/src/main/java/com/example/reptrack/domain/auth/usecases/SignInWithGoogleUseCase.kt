package com.example.reptrack.domain.auth.usecases

import com.example.reptrack.domain.profile.GdprConsent
import com.example.reptrack.domain.profile.User
import com.example.reptrack.data.auth.toDomain
import com.example.reptrack.domain.auth.AuthRepository

class SignInWithGoogleUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return repository.signInWithGoogle(idToken)
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