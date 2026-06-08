package com.example.reptrack.presentation.profile.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.auth.usecases.SignOutUseCase
import com.example.reptrack.domain.profile.User
import com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase
import com.example.reptrack.domain.profile.usecases.UpdateUsernameUseCase
import com.example.reptrack.domain.profile.usecases.UpdatePasskeyUseCase
import com.example.reptrack.domain.backup.SyncUseCase
import com.example.reptrack.presentation.profile.stores.ProfileStore.Label
import com.example.reptrack.presentation.profile.stores.ProfileStore.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import android.content.Context

interface ProfileStore : Store<com.example.reptrack.presentation.profile.stores.ProfileStore.Intent, State, Label> {

    sealed interface Intent {
        object LoadProfile: Intent
        object SignOut: Intent
        object Retry: Intent
        object SyncData: Intent
        data class SyncUser(val userId: String): Intent
        data class UpdateUsername(val username: String, val userId: String): Intent
        data class UpdatePasskey(val passkey: String, val userId: String): Intent
            }

    data class State(
        val user: User? = null,
        val isLoading: Boolean = false,
        val isLoggingOut: Boolean = false,
        val isSyncing: Boolean = false,
        val syncError: String? = null,
        val lastSyncTime: Long = 0,
        val error: String? = null
    )

    sealed interface Label {
        object SignedOut: Label
        data class Error(val message: String): Label
        object SyncCompleted: Label
        data class SyncError(val message: String): Label
    }
}

internal class ProfileStoreFactory(
    private val storeFactory: StoreFactory,
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val syncUseCase: SyncUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase,
    private val updatePasskeyUseCase: UpdatePasskeyUseCase
) {

    fun create(): ProfileStore =
        object : ProfileStore, Store<ProfileStore.Intent, State, Label> by storeFactory.create(
            name = "ProfileStore",
            initialState = State(lastSyncTime = syncUseCase.getLastSyncTime()),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}


    private sealed interface Msg {
        object Loading: Msg
        data class UserLoaded(val user: User): Msg
        data class Error(val error: String): Msg
        object SigningOut: Msg
        object SignedOut: Msg
        object Syncing: Msg
        object SyncCompleted: Msg
        data class SyncError(val error: String): Msg
        data class UsernameUpdated(val username: String): Msg
        data class PasskeyUpdated(val passkey: String): Msg
            }


    private inner class ExecutorImpl : CoroutineExecutor<com.example.reptrack.presentation.profile.stores.ProfileStore.Intent, Nothing, State, Msg, Label>() {

        override fun executeIntent(intent: com.example.reptrack.presentation.profile.stores.ProfileStore.Intent, getState: () -> State) {
            when (intent) {
                com.example.reptrack.presentation.profile.stores.ProfileStore.Intent.LoadProfile -> loadProfile()
                com.example.reptrack.presentation.profile.stores.ProfileStore.Intent.SignOut -> signOut()
                com.example.reptrack.presentation.profile.stores.ProfileStore.Intent.Retry -> loadProfile()
                com.example.reptrack.presentation.profile.stores.ProfileStore.Intent.SyncData -> syncData(getState().user?.id)
                is com.example.reptrack.presentation.profile.stores.ProfileStore.Intent.SyncUser -> syncData(intent.userId)
                is com.example.reptrack.presentation.profile.stores.ProfileStore.Intent.UpdateUsername -> updateUsername(intent.username, intent.userId, getState)
                is com.example.reptrack.presentation.profile.stores.ProfileStore.Intent.UpdatePasskey -> updatePasskey(intent.passkey, intent.userId, getState)
            }
        }

        private fun loadProfile() = scope.launch {
            android.util.Log.d("ProfileStore", "Loading profile...")
            dispatch(Msg.Loading)
            try {
                getCurrentUserProfileUseCase().catch { e ->
                    dispatch(Msg.Error(e.message ?: "Unknown error"))
                }.collect { user ->
                    if (user != null) {
                        dispatch(Msg.UserLoaded(user))
                    } else {
                        dispatch(Msg.Error("User not found in database"))
                    }
                }
            } catch (e: Exception) {
                dispatch(Msg.Error(e.message ?: "Failed to load profile"))
            }
        }

        private fun signOut() = scope.launch {
            dispatch(Msg.SigningOut)
            try {
                signOutUseCase()
                dispatch(Msg.SignedOut)
                publish(Label.SignedOut)
            } catch (e: Exception) {
                dispatch(Msg.Error(e.message ?: "Failed to sign out"))
                publish(Label.Error(e.message ?: "Failed to sign out"))
            }
        }

        
         private fun updatePasskey(passkey: String, userId: String, getState: () -> State) = scope.launch {
              android.util.Log.d("ProfileStore", "Updating passkey to: $passkey for user: $userId")
             try {
                  updatePasskeyUseCase(passkey, userId)
                  updatePasskeyUseCase.updateFirebaseOnly(passkey, userId)
                   // Sync user data to Firestore
                   val syncSuccess = syncUseCase.syncUserOnly(userId)
                   android.util.Log.d("ProfileStore", "User sync result: $syncSuccess")
                  // Update the current user's friend code in the state
                 getState().user?.let { currentUser ->
                     val updatedUser = currentUser.copy(friendCode = passkey)
                     dispatch(Msg.UserLoaded(updatedUser))
                     android.util.Log.d("ProfileStore", "Passkey updated successfully: $passkey")
                 }
             } catch (e: Exception) {
                 android.util.Log.e("ProfileStore", "Failed to update passkey", e)
                 dispatch(Msg.Error(e.message ?: "Failed to update passkey"))
             }
         }

        private fun updateUsername(username: String, userId: String, getState: () -> State) = scope.launch {
            android.util.Log.d("ProfileStore", "Updating username to: $username for user: $userId")
            try {
                updateUsernameUseCase(username, userId)
                updateUsernameUseCase.updateFirebaseOnly(username, userId)
                // Update the current user's username in the state
                getState().user?.let { currentUser ->
                    val updatedUser = currentUser.copy(username = username)
                    dispatch(Msg.UserLoaded(updatedUser))
                    android.util.Log.d("ProfileStore", "Username updated successfully: $username")
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileStore", "Failed to update username", e)
                dispatch(Msg.Error(e.message ?: "Failed to update username"))
            }
        }

        private fun syncData(userId: String?) = scope.launch {
            if (userId == null) {
                android.util.Log.e("ProfileStore", "Cannot sync: user ID is null")
                dispatch(Msg.SyncError("User ID is null"))
                publish(Label.SyncError("User ID is null"))
                return@launch
            }

            android.util.Log.d("ProfileStore", "Starting sync for user: $userId")
            
            // Sync will continue in background even after screen destruction
            // because it runs in the store's scope
            dispatch(Msg.Syncing)
            try {
                val success = syncUseCase(userId)
                if (success) {
                    android.util.Log.d("ProfileStore", "Sync completed successfully")
                    dispatch(Msg.SyncCompleted)
                    publish(Label.SyncCompleted)
                } else {
                    android.util.Log.e("ProfileStore", "Sync failed: returned false")
                    dispatch(Msg.SyncError("Sync failed"))
                    publish(Label.SyncError("Sync failed"))
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileStore", "Sync failed with exception", e)
                dispatch(Msg.SyncError(e.message ?: "Sync error"))
                publish(Label.SyncError(e.message ?: "Sync error"))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.UserLoaded -> copy(
                    user = msg.user,
                    isLoading = false,
                    error = null
                )
                is Msg.Error -> copy(
                    isLoading = false,
                    isLoggingOut = false,
                    isSyncing = false,
                    error = msg.error
                )
                is Msg.SyncError -> copy(
                    isSyncing = false,
                    syncError = msg.error
                )
                Msg.Loading -> copy(
                    isLoading = true,
                    error = null
                )
                Msg.SigningOut -> copy(
                    isLoggingOut = true,
                    error = null
                )
                Msg.SignedOut -> copy(
                    isLoggingOut = false,
                    error = null
                )
                Msg.Syncing -> copy(
                    isSyncing = true,
                    syncError = null
                )
                Msg.SyncCompleted -> copy(
                    isSyncing = false,
                    syncError = null,
                    lastSyncTime = System.currentTimeMillis()
                )
                is Msg.UsernameUpdated -> copy(
                    user = user?.copy(username = msg.username),
                    error = null
                )
                is Msg.PasskeyUpdated -> copy(
                    user = user?.copy(friendCode = msg.passkey),
                    error = null
                )
            }
    }
}
