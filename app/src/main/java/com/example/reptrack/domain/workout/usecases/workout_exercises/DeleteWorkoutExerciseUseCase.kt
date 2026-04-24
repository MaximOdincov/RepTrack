package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.logOnFailure
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository

class DeleteWorkoutExerciseUseCase(
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(exerciseId: String): Result<Unit> {
        return workoutExerciseRepository.delete(exerciseId)
            .logOnFailure(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "WorkoutSession",
                    action = "DeleteWorkoutExercise",
                    entityId = exerciseId
                )
            )
    }
}
