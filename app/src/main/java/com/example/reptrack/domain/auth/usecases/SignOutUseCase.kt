package com.example.reptrack.domain.auth.usecases

import com.example.reptrack.domain.auth.AuthRepository

class SignOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}
