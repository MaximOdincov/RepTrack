package com.example.reptrack.domain.auth.usecases

import com.example.reptrack.domain.auth.AuthRepository

class ResetPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit>{
        return repository.resetPassword(email)
    }
}