package com.example.reptrack.feature_auth.domain.usecases

import com.example.reptrack.feature_auth.domain.AuthRepository
import com.example.reptrack.feature_auth.domain.AuthUser

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): AuthUser?{
        return repository.getCurrentUser()
    }
}