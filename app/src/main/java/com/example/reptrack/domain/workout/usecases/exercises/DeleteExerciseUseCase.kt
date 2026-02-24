package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.repositories.ExerciseRepository

class DeleteExerciseUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(exerciseId: String): Result<Unit> {
        return exerciseRepository.deleteExercise(exerciseId)
    }
}
