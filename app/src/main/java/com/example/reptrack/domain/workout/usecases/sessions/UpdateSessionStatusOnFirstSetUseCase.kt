package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.entities.WorkoutStatus
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import kotlinx.coroutines.flow.first

/**
 * Use case для обновления статуса сессии на IN_PROGRESS когда добавляется первый завершённый подход
 */
class UpdateSessionStatusOnFirstSetUseCase(
    private val workoutSessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> {
        return try {
            // Получаем текущую сессию
            val session = workoutSessionRepository.observeSessionById(sessionId).first()
                ?: return Result.failure(NoSuchElementException("Session not found: $sessionId"))

            // Если статус PLANNED, обновляем на IN_PROGRESS
            if (session.status == WorkoutStatus.PLANNED) {
                val updatedSession = session.copy(status = WorkoutStatus.IN_PROGRESS)
                workoutSessionRepository.updateSession(updatedSession)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
