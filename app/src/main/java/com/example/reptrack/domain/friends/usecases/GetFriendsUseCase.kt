package com.example.reptrack.domain.friends.usecases

import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.domain.friends.FriendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class GetFriendsUseCase(
    private val authRepository: AuthRepository,
    private val friendRepository: FriendRepository
) {
    operator fun invoke(): Flow<List<Friend>> {
        val currentUser = authRepository.getCurrentUser()
        android.util.Log.d("GetFriendsUseCase", "currentUser = ${currentUser?.id ?: "null"}")

        if (currentUser == null) {
            android.util.Log.e("GetFriendsUseCase", "No current user, returning empty flow")
            return emptyFlow()
        }

        android.util.Log.d("GetFriendsUseCase", "Observing friends for user: ${currentUser.id}")
        return friendRepository.getFriends(currentUser.id)
            .map { it }
    }
}
