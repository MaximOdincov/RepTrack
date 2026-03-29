package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository

class CreateWorkoutExerciseUseCase(
    private val workoutExerciseRepository: WorkoutExerciseRepository
) {
    suspend operator fun invoke(exercise: WorkoutExercise, workoutSessionId: String): Result<Unit> {
        return workoutExerciseRepository.create(exercise, workoutSessionId)
    }
}
