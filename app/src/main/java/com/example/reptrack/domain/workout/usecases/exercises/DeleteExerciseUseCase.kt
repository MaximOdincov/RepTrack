package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.logOnFailure
import com.example.reptrack.domain.workout.repositories.ExerciseRepository

class DeleteExerciseUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(exerciseId: String): Result<Unit> {
        return exerciseRepository.deleteExercise(exerciseId)
            .logOnFailure(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "ExerciseList",
                    action = "DeleteExercise",
                    entityId = exerciseId
                )
            )
    }
}
