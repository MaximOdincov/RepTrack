package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.WorkoutSession
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow


class ObserveWorkoutSessionByIdUseCase(
    private val sessionRepository: WorkoutSessionRepository
) {
    operator fun invoke(sessionId: String): Flow<WorkoutSession?> {
        return sessionRepository.observeSessionById(sessionId)
    }
}
