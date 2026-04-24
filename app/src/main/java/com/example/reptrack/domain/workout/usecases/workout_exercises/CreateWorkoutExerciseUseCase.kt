package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.logOnFailure
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository

class CreateWorkoutExerciseUseCase(
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(exercise: WorkoutExercise, workoutSessionId: String): Result<Unit> {
        return workoutExerciseRepository.create(exercise, workoutSessionId)
            .logOnFailure(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "WorkoutSession",
                    action = "CreateWorkoutExercise",
                    entityId = exercise.id
                )
            )
    }
}
