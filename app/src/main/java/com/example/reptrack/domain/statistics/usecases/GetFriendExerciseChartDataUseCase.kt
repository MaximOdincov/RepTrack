package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.domain.statistics.repositories.StatisticsRepository
import com.example.reptrack.domain.statistics.entities.ExerciseDataPoint
import kotlinx.coroutines.flow.Flow

class GetFriendExerciseChartDataUseCase(
    private val repository: StatisticsRepository
) {
    operator fun invoke(
        friendId: String,
        friendName: String,
        exerciseId: String,
        fromDate: java.time.LocalDateTime,
        toDate: java.time.LocalDateTime
    ): Flow<List<ExerciseDataPoint>> {
        return repository.observeFriendExerciseData(friendId, friendName, exerciseId, fromDate, toDate)
    }
}