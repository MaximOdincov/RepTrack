package com.example.reptrack.presentation.profile.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.domain.friends.usecases.AddFriendUseCase
import com.example.reptrack.domain.friends.usecases.DeleteFriendUseCase
import com.example.reptrack.domain.friends.usecases.GetFriendsUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

interface FriendsStore : Store<FriendsStore.Intent, FriendsStore.State, FriendsStore.Label> {

    sealed interface Intent {
        object LoadFriends : Intent
        data class AddFriend(val email: String, val passkey: String) : Intent
        data class DeleteFriend(val friendId: String) : Intent
        object ClearError : Intent
        object ClearSuccessMessage : Intent
        data class ToggleSection(val section: Section) : Intent
    }

    sealed interface Section {
        object Friends : Section
    }

    data class State(
        val friends: List<Friend> = emptyList(),
        val isLoading: Boolean = false,
        val isAddingFriend: Boolean = false,
        val isDeletingFriend: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null,
        val expandedSections: Set<Section> = emptySet()
    )

    sealed interface Label {
        object FriendAdded : Label
        data class Error(val message: String) : Label
    }
}

internal class FriendsStoreFactory(
    private val storeFactory: StoreFactory,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val addFriendUseCase: AddFriendUseCase,
    private val deleteFriendUseCase: DeleteFriendUseCase
) {

    fun create(): FriendsStore =
        object : FriendsStore, Store<FriendsStore.Intent, FriendsStore.State, FriendsStore.Label> by storeFactory.create(
            name = "FriendsStore",
            initialState = FriendsStore.State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        object Loading : Msg
        data class FriendsLoaded(val friends: List<Friend>) : Msg
        data class Error(val error: String) : Msg
        object AddingFriend : Msg
        object FriendAdded : Msg
        object DeletingFriend : Msg
        object FriendDeleted : Msg
        object ClearError : Msg
        object ClearSuccessMessage : Msg
        data class SectionToggled(val section: FriendsStore.Section, val isExpanded: Boolean) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<FriendsStore.Intent, Nothing, FriendsStore.State, Msg, FriendsStore.Label>() {
        override fun executeIntent(intent: FriendsStore.Intent, getState: () -> FriendsStore.State) {
            android.util.Log.d("important", "=== FriendsStore executor ===")
            android.util.Log.d("important", "Intent: $intent")
            when (intent) {
                FriendsStore.Intent.LoadFriends -> loadFriends()
                is FriendsStore.Intent.AddFriend -> addFriend(intent.email, intent.passkey)
                is FriendsStore.Intent.DeleteFriend -> deleteFriend(intent.friendId)
                FriendsStore.Intent.ClearError -> dispatch(Msg.ClearError)
                FriendsStore.Intent.ClearSuccessMessage -> dispatch(Msg.ClearSuccessMessage)
                is FriendsStore.Intent.ToggleSection -> toggleSection(intent.section, getState())
            }
        }

        private fun loadFriends() = scope.launch {
            android.util.Log.d("important", "=== loadFriends called ===")
            dispatch(Msg.Loading)
            try {
                getFriendsUseCase().catch { e ->
                    android.util.Log.e("important", "❌ ERROR loading friends: ${e.message}")
                    dispatch(Msg.Error(e.message ?: "Failed to load friends"))
                }.collect { friends ->
                    android.util.Log.d("important", "✅ Friends received from use case: ${friends.size}")
                    android.util.Log.d("important", "Friends: ${friends.map { it.friendUserId to (it.username ?: "Unknown") }}")
                    dispatch(Msg.FriendsLoaded(friends))
                }
            } catch (e: Exception) {
                android.util.Log.e("important", "❌ EXCEPTION in loadFriends: ${e.message}")
                dispatch(Msg.Error(e.message ?: "Failed to load friends"))
            }
        }

        private fun addFriend(email: String, passkey: String) = scope.launch {
            android.util.Log.d("FriendsStore", "addFriend called with email=$email, passkey=$passkey")
            dispatch(Msg.AddingFriend)
            try {
                addFriendUseCase(email, passkey)
                    .onSuccess {
                        android.util.Log.d("FriendsStore", "addFriend success!")
                        dispatch(Msg.FriendAdded)
                        publish(FriendsStore.Label.FriendAdded)
                        // Reload friends after adding
                        loadFriends()
                    }
                    .onFailure { e ->
                        android.util.Log.e("FriendsStore", "addFriend failed: ${e.message}", e)
                        dispatch(Msg.Error(e.message ?: "Failed to add friend"))
                    }
            } catch (e: Exception) {
                android.util.Log.e("FriendsStore", "addFriend exception: ${e.message}", e)
                dispatch(Msg.Error(e.message ?: "Failed to add friend"))
            }
        }

        private fun deleteFriend(friendId: String) = scope.launch {
            dispatch(Msg.DeletingFriend)
            try {
                deleteFriendUseCase(friendId)
                    .onSuccess {
                        dispatch(Msg.FriendDeleted)
                        // Reload friends after deletion
                        loadFriends()
                    }
                    .onFailure { e ->
                        dispatch(Msg.Error(e.message ?: "Failed to delete friend"))
                    }
            } catch (e: Exception) {
                dispatch(Msg.Error(e.message ?: "Failed to delete friend"))
            }
        }

        private fun toggleSection(section: FriendsStore.Section, state: FriendsStore.State) {
            val isExpanded = section in state.expandedSections
            dispatch(Msg.SectionToggled(section,                                                                       !isExpanded))
        }
    }

    private object ReducerImpl : Reducer<FriendsStore.State, Msg> {
        override fun FriendsStore.State.reduce(msg: Msg): FriendsStore.State =
            when (msg) {
                Msg.Loading -> copy(
                    isLoading = true,
                    error = null
                )
                is Msg.FriendsLoaded -> {
                    android.util.Log.d("important", "=== Reducer: FriendsLoaded ===")
                    android.util.Log.d("important", "Friends count: ${msg.friends.size}")
                    copy(
                        friends = msg.friends,
                        isLoading = false,
                        error = null
                    )
                }
                is Msg.Error -> copy(
                    isLoading = false,
                    isAddingFriend = false,
                    isDeletingFriend = false,
                    error = msg.error
                )
                Msg.AddingFriend -> copy(
                    isAddingFriend = true,
                    error = null
                )
                Msg.FriendAdded -> copy(
                    isAddingFriend = false,
                    successMessage = "Friend added successfully!",
                    error = null
                )
                Msg.DeletingFriend -> copy(
                    isDeletingFriend = true,
                    error = null
                )
                Msg.FriendDeleted -> copy(
                    isDeletingFriend = false,
                    error = null
                )
                Msg.ClearError -> copy(error = null)
                Msg.ClearSuccessMessage -> copy(successMessage = null)
                is Msg.SectionToggled -> copy(
                    expandedSections = if (msg.isExpanded) {
                        expandedSections + msg.section
                    } else {
                        expandedSections - msg.section
                    }
                )
            }
    }
}
