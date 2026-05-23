package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.domain.statistics.repositories.StatisticsRepository
import com.example.reptrack.domain.statistics.entities.MuscleGroupDataPoint
import kotlinx.coroutines.flow.Flow

class GetMuscleGroupChartDataUseCase(
    private val repository: StatisticsRepository
) {
    operator fun invoke(userId: String, fromDate: java.time.LocalDateTime, toDate: java.time.LocalDateTime): Flow<List<MuscleGroupDataPoint>> {
        return repository.observeMuscleGroupData(userId, fromDate, toDate)
    }
}