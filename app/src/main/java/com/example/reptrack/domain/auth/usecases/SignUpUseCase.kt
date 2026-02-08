package com.example.reptrack.domain.auth.usecases

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.example.reptrack.domain.workout.GdprConsent
import com.example.reptrack.domain.workout.User
import com.example.reptrack.data.auth.toDomain
import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.auth.AuthUser
import com.example.reptrack.domain.profile.usecases.AddUserUseCase
import java.time.Clock
import java.time.Instant

class SignUpUseCase(
    private val repository: AuthRepository,
    private val addUserUseCase: AddUserUseCase
) {
    suspend operator fun invoke(email: String, password: String, username: String, dataConsent: Boolean): Result<User> {
        return repository.signUp(email, password)
            .fold(
                onSuccess = { authUser ->
                    runCatching {
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
                        addUserUseCase(userWithConsent)
                        userWithConsent
                    }
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
    }
}