package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.entities.WorkoutStatus
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.LocalDate
import java.util.UUID

/**
 * Use case для создания тренировочной сессии из шаблона
 * Создаёт сессию со статусом PLANNED на указанную дату
 */
class CreateWorkoutSessionFromTemplateUseCase(
    private val sessionRepository: WorkoutSessionRepository,
    private val templateRepository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(
        templateId: String,
        userId: String,
        date: LocalDate,
        sessionName: String? = null
    ): Result<WorkoutSession> {
        return try {
            val template = templateRepository.observeTemplateById(templateId).first()
                ?: return Result.failure(NoSuchElementException("Template not found: $templateId"))

            // Создаём сессию на начало указанного дня (например, 9:00)
            val sessionDateTime = date.atTime(9, 0)

            val sessionId = UUID.randomUUID().toString()

            val session = WorkoutSession(
                id = sessionId,
                userId = userId,
                date = sessionDateTime,
                status = WorkoutStatus.PLANNED,
                name = sessionName ?: template.name,
                durationSeconds = 0,
                exercises = template.exerciseIds.mapIndexed { index, exerciseId ->
                    WorkoutExercise(
                        id = UUID.randomUUID().toString(),
                        workoutSessionId = sessionId,
                        exerciseId = exerciseId,
                        sets = emptyList(),
                        restTimerSeconds = 60
                    )
                },
                comment = null
            )

            sessionRepository.createSession(session)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
