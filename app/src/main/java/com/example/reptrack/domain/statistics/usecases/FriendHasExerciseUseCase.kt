package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.domain.statistics.repositories.StatisticsRepository

class FriendHasExerciseUseCase(
    private val repository: StatisticsRepository
) {
    suspend operator fun invoke(friendId: String, exerciseId: String): Boolean {
        return repository.friendHasExercise(friendId, exerciseId)
    }
}