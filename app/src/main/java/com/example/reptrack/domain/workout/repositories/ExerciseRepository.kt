package com.example.reptrack.domain.workout.repositories

import com.example.reptrack.domain.workout.Exercise
import com.example.reptrack.domain.workout.WorkoutExercise
import com.example.reptrack.domain.workout.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun observeExerciseById(exerciseId: String): Flow<Result<Exercise>>

    suspend fun observeAllExercises(): Flow<Result<List<Exercise>>>

    suspend fun createExercise(exercise: Exercise): Result<Unit>

    suspend fun updateExercise(exercise: Exercise): Result<Unit>

    suspend fun deleteExercise(exerciseId: String): Result<Unit>


    suspend fun observeWorkoutExerciseById(exerciseId: String): Flow<Result<WorkoutExercise>>

    suspend fun createWorkoutExercise(exercise: WorkoutExercise): Result<Unit>

    suspend fun updateWorkoutExercise(exercise: WorkoutExercise): Result<Unit>

    suspend fun deleteWorkoutExercise(exerciseId: String): Result<Unit>
    
    suspend fun getLastExerciseProgress(exerciseId: String): List<WorkoutSet>
}