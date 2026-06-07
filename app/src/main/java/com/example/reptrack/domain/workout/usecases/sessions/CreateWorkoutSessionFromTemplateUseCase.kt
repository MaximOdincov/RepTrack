package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.entities.WorkoutStatus
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.util.UUID

/**
 * Use case для создания тренировочной сессии из шаблона
 * Создаёт сессию со статусом PLANNED на указанную дату
 */
class CreateWorkoutSessionFromTemplateUseCase(
    private val sessionRepository: WorkoutSessionRepository,
    private val templateRepository: WorkoutTemplateRepository,
    private val exerciseRepository: ExerciseRepository,
    private val unlinkSessionFromTemplateUseCase: UnlinkSessionFromTemplateUseCase? = null
) {
    suspend operator fun invoke(
        templateId: String,
        userId: String,
        date: LocalDate,
        sessionName: String? = null,
        unlinkFromTemplate: Boolean = false
    ): Result<WorkoutSession> {
        return try {
            android.util.Log.d("SessionDB", "CreateWorkoutSessionFromTemplateUseCase: templateId=$templateId, date=$date, userId=$userId")
            android.util.Log.d("SessionDB", "CreateWorkoutSessionFromTemplateUseCase: START: templateId=$templateId, date=$date, userId=$userId")

            // Check if session already exists for this date (synchronous check to avoid Flow caching issues)
            val existingSession = sessionRepository.getSessionByDate(date)

            if (existingSession != null) {
                // Only add template exercises if session was NOT created from a template
                // (i.e., templateId is null - manually created session)
                if (existingSession.templateId == null) {
                    android.util.Log.d("SessionDB", "Session FOUND but NOT from template: id=${existingSession.id}, exercises=${existingSession.exercises.size}, adding template exercises")
                    
                    val template = templateRepository.observeTemplateById(templateId).firstOrNull()
                        ?: return Result.failure(NoSuchElementException("Template not found: $templateId"))

                    // Получаем упражнения из шаблона
                    val newExercises = template.exerciseIds.map { exerciseId ->
                        exerciseRepository.observeExerciseById(exerciseId).firstOrNull()
                    }.mapNotNull { exercise ->
                        WorkoutExercise(
                            id = UUID.randomUUID().toString(),
                            workoutSessionId = existingSession.id,
                            exerciseId = exercise?.id ?: "0",
                            exerciseName = exercise?.name ?: "Unknown",
                            muscleGroup = exercise?.muscleGroup ?: com.example.reptrack.domain.workout.entities.MuscleGroup.ARMS,
                            exerciseType = exercise?.type ?: com.example.reptrack.domain.workout.entities.ExerciseType.WEIGHT_REPS,
                            iconRes = exercise?.iconRes,
                            sets = emptyList(),
                            restTimerSeconds = 60
                        )
                    }

                    if (newExercises.isNotEmpty()) {
                        // Добавляем упражнения в существующую сессию
                        val updatedSession = existingSession.copy(
                            exercises = existingSession.exercises + newExercises
                        )
                        
                        val result = sessionRepository.updateSession(updatedSession)
                        if (result.isSuccess) {
                            if (unlinkFromTemplate) {
                                unlinkSessionFromTemplateUseCase?.invoke(updatedSession.id)
                            }
                            return Result.success(updatedSession)
                        }
                    }
                    
                    return Result.success(existingSession)
                } else {
                    // Session was created from a template - do NOT add more exercises
                    android.util.Log.d("SessionDB", "Session FOUND but IS from template (id=${existingSession.id}, templateId=${existingSession.templateId}), skipping template addition")
                    return Result.success(existingSession)
                }
            }

            android.util.Log.w("SessionDB", "Session NOT FOUND for date=$date, creating new session from template=$templateId")

            val template = templateRepository.observeTemplateById(templateId).firstOrNull()
                ?: return Result.failure(NoSuchElementException("Template not found: $templateId"))

            // Создаём сессию на начало указанного дня (например, 9:00)
            val sessionDateTime = date.atTime(9, 0)

            val sessionId = UUID.randomUUID().toString()

            // Создаем упражнения с денормализованными данными
            val exercises = template.exerciseIds.mapIndexed { index, exerciseId ->
                // Получаем данные упражнения из библиотеки для копирования
                val exercise = exerciseRepository.observeExerciseById(exerciseId).firstOrNull()

                WorkoutExercise(
                    id = UUID.randomUUID().toString(),
                    workoutSessionId = sessionId,
                    exerciseId = exerciseId,
                    // Копируем данные для независимости
                    exerciseName = exercise?.name ?: "Unknown",
                    muscleGroup = exercise?.muscleGroup ?: com.example.reptrack.domain.workout.entities.MuscleGroup.ARMS,
                    exerciseType = exercise?.type ?: com.example.reptrack.domain.workout.entities.ExerciseType.WEIGHT_REPS,
                    iconRes = exercise?.iconRes,
                    sets = emptyList(),
                    restTimerSeconds = 60
                )
            }

            val session = WorkoutSession(
                id = sessionId,
                userId = userId,
                date = sessionDateTime,
                status = WorkoutStatus.PLANNED,
                name = sessionName ?: template.name,
                durationSeconds = 0,
                exercises = exercises,
                comment = null,
                templateId = if (unlinkFromTemplate) null else templateId
            )

            val result = sessionRepository.createSession(session)

            if (result.isSuccess) {
                if (unlinkFromTemplate) {
                    unlinkSessionFromTemplateUseCase?.invoke(session.id)
                }
                Result.success(session)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
