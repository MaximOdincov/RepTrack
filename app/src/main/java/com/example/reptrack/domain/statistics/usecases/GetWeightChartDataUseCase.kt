package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.domain.statistics.repositories.StatisticsRepository
import com.example.reptrack.domain.statistics.entities.WeightDataPoint
import kotlinx.coroutines.flow.Flow

class GetWeightChartDataUseCase(
    private val repository: StatisticsRepository
) {
    operator fun invoke(userId: String, fromDate: java.time.LocalDateTime, toDate: java.time.LocalDateTime): Flow<List<WeightDataPoint>> {
        return repository.observeWeightData(userId, fromDate, toDate)
    }
}