package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository

class UpdateWorkoutExerciseUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(exercise: WorkoutExercise): Result<Unit> {
        return exerciseRepository.updateWorkoutExercise(exercise)
    }
}
