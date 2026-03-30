package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository

class UpdateWorkoutExerciseUseCase(
    private val workoutExerciseRepository: WorkoutExerciseRepository
) {
    suspend operator fun invoke(exercise: WorkoutExercise): Result<Unit> {
        return workoutExerciseRepository.update(exercise)
    }
}
