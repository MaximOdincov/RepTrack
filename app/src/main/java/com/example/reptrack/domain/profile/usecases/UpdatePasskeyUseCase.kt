package com.example.reptrack.domain.profile.usecases

import android.util.Log
import com.example.reptrack.domain.profile.ProfileRepository
import kotlinx.coroutines.flow.firstOrNull

class UpdatePasskeyUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(passkey: String, userId: String) {
        Log.d("UpdatePasskeyUseCase", "Updating passkey to: $passkey for user: $userId")
        repository.updatePasskey(passkey, userId)
        Log.d("UpdatePasskeyUseCase", "Passkey updated successfully")
    }
    
    suspend fun updateFirebaseOnly(passkey: String?, userId: String) {
        Log.d("UpdatePasskeyUseCase", "Updating Firebase passkey to: $passkey for user: $userId")
        val user = repository.observeUser(userId).firstOrNull()
        user?.let {
            repository.updateFirebaseProfile(it.username, it.email, it.avatarUrl, passkey, userId)
        }
        Log.d("UpdatePasskeyUseCase", "Firebase passkey updated successfully")
    }
}