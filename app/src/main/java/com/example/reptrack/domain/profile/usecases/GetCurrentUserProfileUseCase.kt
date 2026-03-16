package com.example.reptrack.domain.profile.usecases

import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.profile.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class GetCurrentUserProfileUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<com.example.reptrack.domain.profile.User?> {
        val currentUser = authRepository.getCurrentUser()
        android.util.Log.d("GetCurrentUserProfileUseCase", "currentUser = ${currentUser?.id ?: "null"}")

        if (currentUser == null) {
            android.util.Log.e("GetCurrentUserProfileUseCase", "No current user, returning empty flow")
            return emptyFlow()
        }

        android.util.Log.d("GetCurrentUserProfileUseCase", "Observing user: ${currentUser.id}")
        return profileRepository.observeUser(currentUser.id)
            .map { it }
    }
}
