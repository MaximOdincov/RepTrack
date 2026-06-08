package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.domain.statistics.repositories.StatisticsRepository
import com.example.reptrack.domain.statistics.entities.ExerciseDataPoint
import kotlinx.coroutines.flow.Flow

class GetExerciseChartDataUseCase(
    private val repository: StatisticsRepository
) {
    operator fun invoke(
        userId: String,
        userName: String,
        exerciseId: String,
        fromDate: java.time.LocalDateTime,
        toDate: java.time.LocalDateTime,
        maxSets: Int = 10
    ): Flow<List<ExerciseDataPoint>> {
        android.util.Log.d("GetExerciseChartDataUseCase", "=== invoke called ===")
        android.util.Log.d("GetExerciseChartDataUseCase", "userId: $userId, userName: $userName")
        android.util.Log.d("GetExerciseChartDataUseCase", "exerciseId: $exerciseId")
        android.util.Log.d("GetExerciseChartDataUseCase", "Date range: $fromDate to $toDate")
        android.util.Log.d("GetExerciseChartDataUseCase", "maxSets: $maxSets")

        return repository.observeExerciseData(userId, userName, exerciseId, fromDate, toDate, maxSets)
    }
}