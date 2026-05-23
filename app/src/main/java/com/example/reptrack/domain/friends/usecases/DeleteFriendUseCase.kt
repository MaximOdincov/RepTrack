package com.example.reptrack.domain.friends.usecases

import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.friends.FriendRepository

class DeleteFriendUseCase(
    private val authRepository: AuthRepository,
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(friendId: String): Result<Unit> {
        val currentUser = authRepository.getCurrentUser()
            ?: return Result.failure(Exception("Not authenticated"))

        android.util.Log.d("DeleteFriendUseCase", "Deleting friend: $friendId for user: ${currentUser.id}")
        return friendRepository.deleteFriend(currentUser.id, friendId)
    }
}
