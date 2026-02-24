package com.example.reptrack.data.workout.repositories

import com.example.reptrack.data.local.dao.ExerciseDao
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class ExerciseRepositoryImpl(
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao
) : ExerciseRepository {

    override suspend fun observeExerciseById(exerciseId: String): Flow<Exercise> {
        return exerciseDao.observeById(exerciseId)
            .map { it?.toDomain() ?: throw NoSuchElementException("Exercise with id $exerciseId not found") }
    }

    override suspend fun observeAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.observeAll()
            .map { exercisesDb ->
                exercisesDb
                    .filter { it.deletedAt == null }
                    .map { it.toDomain() }
            }
    }

    override suspend fun createExercise(exercise: Exercise): Result<Unit> = try {
        exerciseDao.insert(exercise.toDb())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateExercise(exercise: Exercise): Result<Unit> = try {
        exerciseDao.insert(exercise.toDb().copy(updatedAt = LocalDateTime.now()))
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteExercise(exerciseId: String): Result<Unit> = try {
        val exercise = exerciseDao.getById(exerciseId)
        if (exercise != null) {
            exerciseDao.insert(exercise.copy(deletedAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()))
            Result.success(Unit)
        } else {
            Result.failure(NoSuchElementException("Exercise with id $exerciseId not found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun observeWorkoutExerciseById(exerciseId: String): Flow<WorkoutExercise> {
        return workoutDao.observeWorkoutExerciseWithSets(exerciseId)
            .map { it?.toDomain() ?: throw NoSuchElementException("WorkoutExercise with id $exerciseId not found") }
    }

    override suspend fun createWorkoutExercise(exercise: WorkoutExercise, workoutSessionId: String): Result<Unit> = try {
        workoutDao.insertExercise(exercise.toDb(workoutSessionId))
        workoutDao.insertSets(exercise.sets.map { it.toDb(exercise.id) })
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateWorkoutExercise(exercise: WorkoutExercise): Result<Unit> = try {
        // Update entire exercise with all sets (as per user requirement)
        val current = workoutDao.getWorkoutExerciseWithSets(exercise.id)
            ?: return Result.failure(NoSuchElementException("WorkoutExercise with id ${exercise.id} not found"))

        workoutDao.insertExercise(current.exercise.copy(
            restTimerSeconds = exercise.restTimerSeconds,
            updatedAt = LocalDateTime.now()
        ))

        workoutDao.insertSets(exercise.sets.map { it.toDb(exercise.id) })
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteWorkoutExercise(exerciseId: String): Result<Unit> = try {
        workoutDao.deleteExerciseById(
            exerciseId = exerciseId,
            deletedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getLastExerciseProgress(exerciseId: String): Flow<List<WorkoutSet>> {
        return workoutDao.observeLastCompletedExerciseWithSets(exerciseId)
            .map { it?.sets?.map { set -> set.toDomain() } ?: emptyList() }
    }
}
