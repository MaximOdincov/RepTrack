package com.example.reptrack.feature_auth.domain.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.example.reptrack.core.domain.entities.GdprConsent
import com.example.reptrack.core.domain.entities.User
import com.example.reptrack.feature_auth.data.toDomain
import com.example.reptrack.feature_auth.domain.AuthRepository
import com.example.reptrack.feature_auth.domain.AuthUser
import com.example.reptrack.feature_profile.domain.usecases.AddUserUseCase
import java.time.Clock
import java.time.Instant

class SignUpUseCase(
    private val repository: AuthRepository,
    private val addUserUseCase: AddUserUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
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