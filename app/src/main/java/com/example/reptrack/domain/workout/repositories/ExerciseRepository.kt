package com.example.reptrack.domain.workout.repositories

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun observeExerciseById(exerciseId: String): Flow<Exercise>

    suspend fun observeAllExercises(): Flow<List<Exercise>>

    suspend fun createExercise(exercise: Exercise): Result<Unit>

    suspend fun updateExercise(exercise: Exercise): Result<Unit>

    suspend fun deleteExercise(exerciseId: String): Result<Unit>


    suspend fun observeWorkoutExerciseById(exerciseId: String): Flow<WorkoutExercise>

    suspend fun createWorkoutExercise(exercise: WorkoutExercise, workoutSessionId: String): Result<Unit>

    suspend fun updateWorkoutExercise(exercise: WorkoutExercise): Result<Unit>

    suspend fun deleteWorkoutExercise(exerciseId: String): Result<Unit>
    
    suspend fun getLastExerciseProgress(exerciseId: String): Flow<List<WorkoutSet>>
}