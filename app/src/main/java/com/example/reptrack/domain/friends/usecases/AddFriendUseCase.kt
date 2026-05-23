package com.example.reptrack.domain.friends.usecases

import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.domain.friends.FriendRepository

class AddFriendUseCase(
    private val authRepository: AuthRepository,
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(friendEmail: String, passkey: String): Result<Friend> {
        val currentUser = authRepository.getCurrentUser()
            ?: return Result.failure(Exception("Not authenticated"))

        android.util.Log.d("AddFriendUseCase", "Adding friend: $friendEmail for user: ${currentUser.id}")
        return friendRepository.addFriend(currentUser.id, friendEmail, passkey)
    }
}
