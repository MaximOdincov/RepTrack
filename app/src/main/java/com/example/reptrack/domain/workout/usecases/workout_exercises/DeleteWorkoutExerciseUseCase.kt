package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository

class DeleteWorkoutExerciseUseCase(
    private val workoutExerciseRepository: WorkoutExerciseRepository
) {
    suspend operator fun invoke(exerciseId: String): Result<Unit> {
        return workoutExerciseRepository.delete(exerciseId)
    }
}
