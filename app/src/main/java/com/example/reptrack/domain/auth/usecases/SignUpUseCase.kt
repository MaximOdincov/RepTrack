package com.example.reptrack.domain.auth.usecases

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.example.reptrack.domain.profile.GdprConsent
import com.example.reptrack.domain.profile.User
import com.example.reptrack.data.auth.toDomain
import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.auth.AuthUser
import java.time.Clock
import java.time.Instant

class SignUpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, username: String, dataConsent: Boolean): Result<User> {
        return repository.signUp(email, password)
            .fold(
                onSuccess = { authUser ->
                    val baseUser = authUser.toDomain().copy(username = username)
                    val userWithConsent = if (dataConsent) {
                        baseUser.copy(
                            gdprConsent = GdprConsent(
                                isAccepted = true,
                                acceptedAt = Instant.now().toEpochMilli()
                            )
                        )
                    } else {
                        baseUser
                    }
                    Result.success(userWithConsent)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
    }
}