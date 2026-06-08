package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.entities.WorkoutStatus
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository

class CompleteWorkoutSessionUseCase(
    private val workoutSessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> {
        return try {
            workoutSessionRepository.updateSessionStatus(sessionId, WorkoutStatus.COMPLETED)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
