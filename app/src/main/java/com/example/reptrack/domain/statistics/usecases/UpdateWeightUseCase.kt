package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.domain.statistics.repositories.StatisticsRepository

class UpdateWeightUseCase(
    private val repository: StatisticsRepository
) {
    suspend operator fun invoke(userId: String, date: java.time.LocalDateTime, value: Float) {
        repository.updateWeightRecord(userId, date, value)
    }
}