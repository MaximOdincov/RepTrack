package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository

class DeleteWorkoutSessionUseCase(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> {
        return sessionRepository.deleteSession(sessionId)
    }
}
