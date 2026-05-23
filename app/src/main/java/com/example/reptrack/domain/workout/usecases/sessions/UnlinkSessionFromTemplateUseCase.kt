package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Use case для разрыва связи сессии с шаблоном
 * Вызывается при любом изменении сессии (добавление/удаление упражнения, изменение статуса)
 */
class UnlinkSessionFromTemplateUseCase(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> {
        return try {
            val session = sessionRepository.observeSessionById(sessionId).firstOrNull()
            if (session != null && session.templateId != null) {
                val updatedSession = session.copy(templateId = null)
                sessionRepository.updateSession(updatedSession)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
