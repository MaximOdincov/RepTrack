package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.WorkoutSession
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository

class CreateWorkoutSessionUseCase(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(session: WorkoutSession): Result<Unit> {
        return sessionRepository.createSession(session)
    }
}
