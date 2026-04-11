package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndLog
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow

class ObserveExerciseByIdUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(exerciseId: String): Flow<Exercise> {
        return exerciseRepository.observeExerciseById(exerciseId)
            .catchAndLog(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "ExerciseDetail",
                    action = "ObserveExerciseById",
                    entityId = exerciseId
                )
            )
    }
}
