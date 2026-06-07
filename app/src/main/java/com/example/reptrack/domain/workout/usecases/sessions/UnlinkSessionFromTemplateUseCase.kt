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
        android.util.Log.d("SessionDB", "UnlinkSessionFromTemplateUseCase: START - sessionId=$sessionId")
        return try {
            val session = sessionRepository.observeSessionById(sessionId).firstOrNull()
            android.util.Log.d("SessionDB", "UnlinkSessionFromTemplateUseCase: session=${session?.id}, templateId=${session?.templateId}")
            if (session != null && session.templateId != null) {
                val updatedSession = session.copy(templateId = null)
                val result = sessionRepository.updateSession(updatedSession)
                android.util.Log.d("SessionDB", "UnlinkSessionFromTemplateUseCase: unlink completed - sessionId=$sessionId, result=$result")
            } else {
                android.util.Log.d("SessionDB", "UnlinkSessionFromTemplateUseCase: no templateId to unlink (session=${session?.id}, templateId=${session?.templateId})")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SessionDB", "UnlinkSessionFromTemplateUseCase: FAILED - sessionId=$sessionId, error=${e.message}", e)
            Result.failure(e)
        }
    }
}
