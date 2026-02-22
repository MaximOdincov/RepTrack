package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.repositories.ExerciseRepository

class DeleteWorkoutExerciseUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(exerciseId: String): Result<Unit> {
        return exerciseRepository.deleteWorkoutExercise(exerciseId)
    }
}
