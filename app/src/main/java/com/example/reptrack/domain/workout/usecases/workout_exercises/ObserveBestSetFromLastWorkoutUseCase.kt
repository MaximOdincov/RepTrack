package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndLog
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository
import kotlinx.coroutines.flow.Flow

class ObserveBestSetFromLastWorkoutUseCase(
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(exerciseId: String): Flow<WorkoutSet?> {
        return workoutExerciseRepository.observeBestSetFromLastWorkout(exerciseId)
            .catchAndLog(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "WorkoutSession",
                    action = "ObserveBestSetFromLastWorkout",
                    entityId = exerciseId
                )
            )
    }
}
