package com.example.reptrack.presentation.profile

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reptrack.domain.profile.User
import com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase
) : ViewModel() {

    val user: MutableState<User?> = mutableStateOf(null)
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val isDarkTheme = mutableStateOf(false)

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                getCurrentUserProfileUseCase().collect { profileUser ->
                    user.value = profileUser
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "Failed to load profile: ${e.message}"
                isLoading.value = false
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Username update requested: $newUsername")
        }
    }

    fun updateWeight(newWeight: Float) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Weight update requested: $newWeight")
        }
    }

    fun toggleTheme(darkTheme: Boolean) {
        isDarkTheme.value = darkTheme
        Log.d("ProfileViewModel", "Theme changed to: $darkTheme")
    }

    fun getPasskey(): String {
        return user.value?.friendCode ?: "Не указано"
    }
}