package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.domain.statistics.repositories.StatisticsRepository
import com.example.reptrack.domain.statistics.entities.MuscleGroupDataPoint
import kotlinx.coroutines.flow.Flow

class GetFriendMuscleGroupChartDataUseCase(
    private val repository: StatisticsRepository
) {
    operator fun invoke(friendId: String, fromDate: java.time.LocalDateTime, toDate: java.time.LocalDateTime): Flow<List<MuscleGroupDataPoint>> {
        return repository.observeFriendMuscleGroupData(friendId, fromDate, toDate)
    }
}