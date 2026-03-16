package com.example.reptrack.presentation.profile.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.auth.usecases.SignOutUseCase
import com.example.reptrack.domain.profile.User
import com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase
import com.example.reptrack.presentation.profile.stores.ProfileStore.Intent
import com.example.reptrack.presentation.profile.stores.ProfileStore.Label
import com.example.reptrack.presentation.profile.stores.ProfileStore.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

interface ProfileStore : Store<Intent, State, Label> {

    sealed interface Intent {
        object LoadProfile: Intent
        object SignOut: Intent
        object Retry: Intent
    }

    data class State(
        val user: User? = null,
        val isLoading: Boolean = false,
        val isLoggingOut: Boolean = false,
        val error: String? = null
    )

    sealed interface Label {
        object SignedOut: Label
        data class Error(val message: String): Label
    }
}

internal class ProfileStoreFactory(
    private val storeFactory: StoreFactory,
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val signOutUseCase: SignOutUseCase
) {

    fun create(): ProfileStore =
        object : ProfileStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ProfileStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}


    private sealed interface Msg {
        object Loading: Msg
        data class UserLoaded(val user: User): Msg
        data class Error(val error: String): Msg
        object SigningOut: Msg
        object SignedOut: Msg
    }


    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.LoadProfile -> loadProfile()
                Intent.SignOut -> signOut()
                Intent.Retry -> loadProfile()
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
                    error = msg.error
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
            }
    }
}
