package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkoutExerciseByIdUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(exerciseId: String): Flow<WorkoutExercise> {
        return exerciseRepository.observeWorkoutExerciseById(exerciseId)
    }
}
