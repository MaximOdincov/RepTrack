package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository

class CreateExerciseUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(exercise: Exercise): Result<Unit> {
        return exerciseRepository.createExercise(exercise)
    }
}
