package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.entities.WorkoutStatus
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Use case для обновления всех сессий, связанных с шаблоном
 * Вызывается при изменении шаблона для синхронизации всех PLANNED сессий
 */
class UpdateLinkedSessionsUseCase(
    private val sessionRepository: WorkoutSessionRepository,
    private val templateRepository: WorkoutTemplateRepository,
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(templateId: String): Result<Unit> {
        return try {
            // Get template
            val template = templateRepository.observeTemplateById(templateId).firstOrNull()
                ?: return Result.failure(NoSuchElementException("Template not found: $templateId"))

            // Get all linked PLANNED sessions
            sessionRepository.observeSessionsByTemplateId(templateId).firstOrNull()?.forEach { session ->
                // Recreate exercises from updated template
                val updatedExercises = template.exerciseIds.mapIndexed { index, exerciseId ->
                    val exercise = exerciseRepository.observeExerciseById(exerciseId).firstOrNull()

                    WorkoutExercise(
                        id = session.exercises.getOrNull(index)?.id ?: java.util.UUID.randomUUID().toString(),
                        workoutSessionId = session.id,
                        exerciseId = exerciseId,
                        exerciseName = exercise?.name ?: "Unknown",
                        muscleGroup = exercise?.muscleGroup ?: com.example.reptrack.domain.workout.entities.MuscleGroup.ARMS,
                        exerciseType = exercise?.type ?: com.example.reptrack.domain.workout.entities.ExerciseType.WEIGHT_REPS,
                        iconRes = exercise?.iconRes,
                        sets = emptyList(),
                        restTimerSeconds = 60
                    )
                }

                val updatedSession = session.copy(
                    name = template.name,
                    exercises = updatedExercises
                )

                sessionRepository.updateSession(updatedSession)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
