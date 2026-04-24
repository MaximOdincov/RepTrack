package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.logOnFailure
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository

class CreateExerciseUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(exercise: Exercise): Result<Unit> {
        return exerciseRepository.createExercise(exercise)
            .logOnFailure(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "ExerciseList",
                    action = "CreateExercise",
                    entityId = exercise.id
                )
            )
    }
}
