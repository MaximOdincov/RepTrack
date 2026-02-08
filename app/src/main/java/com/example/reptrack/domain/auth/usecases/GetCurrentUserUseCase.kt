package com.example.reptrack.domain.auth.usecases

import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.auth.AuthUser

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): AuthUser?{
        return repository.getCurrentUser()
    }
}