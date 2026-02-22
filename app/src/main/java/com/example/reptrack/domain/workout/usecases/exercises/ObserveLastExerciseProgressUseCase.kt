package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing the last completed exercise progress.
 * Returns the sets from the most recent completed workout for the given exercise.
 */
class ObserveLastExerciseProgressUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    /**
     * @param exerciseId The ID of the exercise to get progress for
     * @return Flow<List<WorkoutSet>> - list of sets from the last completed workout,
     *         or empty list if no completed workouts exist for this exercise
     */
    suspend operator fun invoke(exerciseId: String): Flow<List<WorkoutSet>> {
        return exerciseRepository.getLastExerciseProgress(exerciseId)
    }
}
