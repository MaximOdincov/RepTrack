package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository


class UpdateWorkoutSessionUseCase(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(session: WorkoutSession): Result<Unit> {
        return sessionRepository.updateSession(session)
    }
}
