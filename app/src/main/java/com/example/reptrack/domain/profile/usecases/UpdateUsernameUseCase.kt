package com.example.reptrack.domain.profile.usecases

import android.util.Log
import com.example.reptrack.domain.profile.ProfileRepository

class UpdateUsernameUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(username: String, userId: String) {
        Log.d("UpdateUsernameUseCase", "Updating username to: $username for user: $userId")
        repository.updateUsername(username, userId)
        Log.d("UpdateUsernameUseCase", "Username updated successfully")
    }

    suspend fun updateFirebaseOnly(username: String, userId: String) {
        Log.d("UpdateUsernameUseCase", "Updating username in Firebase to: $username for user: $userId")
        repository.updateUsernameInFirebase(username, userId)
        Log.d("UpdateUsernameUseCase", "Username updated successfully in Firebase")
    }
}