package com.example.reptrack.feature_auth.domain.usecases

import com.example.reptrack.feature_auth.domain.AuthRepository

class ResetPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit>{
        return repository.resetPassword(email)
    }
}