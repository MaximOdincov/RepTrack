package com.example.reptrack.domain.profile.usecases

import android.util.Log
import com.example.reptrack.domain.profile.ProfileRepository

class UpdatePasskeyUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(passkey: String, userId: String) {
        Log.d("UpdatePasskeyUseCase", "Updating passkey to: $passkey for user: $userId")
        repository.updatePasskey(passkey, userId)
        Log.d("UpdatePasskeyUseCase", "Passkey updated successfully")
    }
}