package com.example.reptrack.data.workout.repositories

import com.example.reptrack.data.local.dao.ExerciseDao
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class ExerciseRepositoryImpl(
    private val exerciseDao: ExerciseDao
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
}
