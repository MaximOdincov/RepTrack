package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndLog
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkoutExercisesBySessionUseCase(
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(sessionId: String): Flow<List<WorkoutExercise>> {
        return workoutExerciseRepository.observeBySession(sessionId)
            .catchAndLog(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "WorkoutSession",
                    action = "ObserveWorkoutExercisesBySession",
                    entityId = sessionId
                )
            )
    }
}
