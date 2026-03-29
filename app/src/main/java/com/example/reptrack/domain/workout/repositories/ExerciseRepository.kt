package com.example.reptrack.domain.workout.repositories

import com.example.reptrack.domain.workout.entities.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun observeExerciseById(exerciseId: String): Flow<Exercise>

    suspend fun observeAllExercises(): Flow<List<Exercise>>

    suspend fun createExercise(exercise: Exercise): Result<Unit>

    suspend fun updateExercise(exercise: Exercise): Result<Unit>

    suspend fun deleteExercise(exerciseId: String): Result<Unit>
}