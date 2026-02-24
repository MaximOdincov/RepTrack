package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow

class ObserveExerciseByIdUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(exerciseId: String): Flow<Exercise> {
        return exerciseRepository.observeExerciseById(exerciseId)
    }
}
