package com.example.reptrack.domain.workout.repositories

import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface WorkoutExerciseRepository {
    fun observeById(exerciseId: String): Flow<WorkoutExercise>

    fun observeBySession(sessionId: String): Flow<List<WorkoutExercise>>

    suspend fun create(exercise: WorkoutExercise, sessionId: String): Result<Unit>
    suspend fun update(exercise: WorkoutExercise): Result<Unit>
    suspend fun delete(exerciseId: String): Result<Unit>

    fun observeBestSetFromLastWorkout(exerciseId: String): Flow<WorkoutSet?>

    fun observeLastCompletedExercise(exerciseId: String): Flow<WorkoutExercise?>
}
