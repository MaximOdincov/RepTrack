package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkoutExerciseByIdUseCase(
    private val workoutExerciseRepository: WorkoutExerciseRepository
) {
    suspend operator fun invoke(exerciseId: String): Flow<WorkoutExercise> {
        return workoutExerciseRepository.observeById(exerciseId)
    }
}
