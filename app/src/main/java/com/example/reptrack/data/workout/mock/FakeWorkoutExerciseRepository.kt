package com.example.reptrack.data.workout.mock

import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Fake repository for workout exercises with state management.
 * Uses StateFlow to automatically notify subscribers when data changes.
 *
 * Used for UI development and testing.
 */
class FakeWorkoutExerciseRepository : WorkoutExerciseRepository {

    private val _exercises = MutableStateFlow<List<WorkoutExercise>>(emptyList())
    val exercises: StateFlow<List<WorkoutExercise>> = _exercises

    // Map from sessionId to list of exercise IDs
    private val _sessionExercises = MutableStateFlow<Map<String, List<WorkoutExercise>>>(emptyMap())
    val sessionExercises: StateFlow<Map<String, List<WorkoutExercise>>> = _sessionExercises

    override fun observeById(exerciseId: String): Flow<WorkoutExercise> {
        return _exercises.map { exercises ->
            exercises.find { it.id == exerciseId }
                ?: throw NoSuchElementException("WorkoutExercise with id $exerciseId not found")
        }
    }

    override fun observeBySession(sessionId: String): Flow<List<WorkoutExercise>> {
        return _sessionExercises.map { sessionMap ->
            sessionMap[sessionId] ?: emptyList()
        }
    }

    override suspend fun create(exercise: WorkoutExercise, sessionId: String): Result<Unit> {
        _exercises.update { current -> current + exercise }
        _sessionExercises.update { current ->
            val updated = current[sessionId]?.toMutableList() ?: mutableListOf()
            updated.add(exercise)
            current + (sessionId to updated)
        }
        return Result.success(Unit)
    }

    override suspend fun update(exercise: WorkoutExercise): Result<Unit> {
        _exercises.update { current ->
            current.map {
                if (it.id == exercise.id) exercise
                else it
            }
        }
        _sessionExercises.update { current ->
            current.mapValues { (_, exercises) ->
                exercises.map {
                    if (it.id == exercise.id) exercise
                    else it
                }
            }
        }
        return Result.success(Unit)
    }

    override suspend fun delete(exerciseId: String): Result<Unit> {
        _exercises.update { current -> current.filterNot { it.id == exerciseId } }
        _sessionExercises.update { current ->
            current.mapValues { (_, exercises) ->
                exercises.filterNot { it.id == exerciseId }
            }
        }
        return Result.success(Unit)
    }

    override fun observeBestSetFromLastWorkout(exerciseId: String): Flow<WorkoutSet?> {
        return _exercises.map { exercises ->
            // Find all exercises with matching exerciseId
            val matchingExercises = exercises.filter { it.exerciseId == exerciseId }

            // Get all completed sets from all matching exercises
            val completedSets = matchingExercises
                .flatMap { it.sets }
                .filter { it.isCompleted }

            completedSets.maxByOrNull { it.weight ?: 0f }
        }
    }

    override fun observeLastCompletedExercise(exerciseId: String): Flow<WorkoutExercise?> {
        return _exercises.map { exercises ->
            exercises
                .filter { it.exerciseId == exerciseId }
                .firstOrNull()
        }
    }

    /**
     * Helper method to add mock data for testing
     */
    fun addMockExercise(sessionId: String, exercise: WorkoutExercise) {
        _exercises.update { current -> current + exercise }
        _sessionExercises.update { current ->
            val updated = current[sessionId]?.toMutableList() ?: mutableListOf()
            updated.add(exercise)
            current + (sessionId to updated)
        }
    }

    /**
     * Helper method to clear all data
     */
    fun clear() {
        _exercises.update { emptyList() }
        _sessionExercises.update { emptyMap() }
    }

    /**
     * Create a mock workout exercise with sets
     */
    fun createMockExercise(
        id: String,
        exerciseId: String,
        weight: Float = 20f,
        reps: Int = 12,
        setsCount: Int = 3
    ): WorkoutExercise {
        return WorkoutExercise(
            id = id,
            workoutSessionId = "mock_session_id",
            exerciseId = exerciseId,
            exerciseName = "Mock Exercise",
            muscleGroup = com.example.reptrack.domain.workout.entities.MuscleGroup.CHEST,
            exerciseType = com.example.reptrack.domain.workout.entities.ExerciseType.WEIGHT_REPS,
            iconRes = null,
            sets = List(setsCount) { index ->
                WorkoutSet(
                    id = "${id}_set_${index + 1}",
                    index = index + 1,
                    weight = weight + (index * 2.5f),
                    reps = reps - index,
                    isCompleted = true
                )
            },
            restTimerSeconds = 90
        )
    }
}
