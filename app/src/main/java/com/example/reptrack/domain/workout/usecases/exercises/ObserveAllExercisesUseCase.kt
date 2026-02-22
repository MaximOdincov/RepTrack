package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for observing all exercises grouped by muscle group.
 * Returns a Map where keys are MuscleGroups sorted alphabetically by name,
 * and values are Lists of Exercises sorted alphabetically by name.
 */
class ObserveAllExercisesUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    /**
     * @return Flow<Map<MuscleGroup, List<Exercise>>> where:
     * - Keys are sorted by MuscleGroup ordinal (CHEST, BACK, LEGS, ARMS, ABS, CARDIO)
     * - Values (exercise lists) are sorted alphabetically by exercise name
     */
    suspend operator fun invoke(): Flow<Map<MuscleGroup, List<Exercise>>> {
        return exerciseRepository.observeAllExercises().map { exercises ->
            exercises.groupBy { it.muscleGroup }
                .mapValues { (_, exercisesList) ->
                    exercisesList.sortedBy { it.name }
                }
                .toSortedMap(compareBy { it.ordinal })
        }
    }
}
