package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.WorkoutSession
import com.example.reptrack.domain.workout.WorkoutStatus
import com.example.reptrack.domain.workout.WorkoutExercise
import com.example.reptrack.domain.workout.WorkoutSet
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.util.UUID

/**
 * Use case для создания тренировочной сессии из шаблона
 */
class CreateWorkoutSessionFromTemplateUseCase(
    private val sessionRepository: WorkoutSessionRepository,
    private val templateRepository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(
        templateId: String,
        userId: String,
        sessionName: String? = null
    ): Result<WorkoutSession> {
        return try {
            val template = templateRepository.observeTemplateById(templateId).first()
                ?: return Result.failure(NoSuchElementException("Template not found: $templateId"))

            val now = LocalDateTime.now()
            val session = WorkoutSession(
                id = UUID.randomUUID().toString(),
                userId = userId,
                date = now,
                status = WorkoutStatus.IN_PROGRESS,
                name = sessionName ?: template.name,
                durationSeconds = 0,
                exercises = template.exerciseIds.mapIndexed { index, exerciseId ->
                    WorkoutExercise(
                        id = UUID.randomUUID().toString(),
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
