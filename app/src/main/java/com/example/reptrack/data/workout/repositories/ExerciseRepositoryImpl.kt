package com.example.reptrack.data.workout.repositories

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.exceptions.DataException
import com.example.reptrack.core.error.exceptions.DomainException
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndHandle
import com.example.reptrack.core.extensions.toAppException
import com.example.reptrack.data.local.dao.ExerciseDao
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.sql.SQLException
import java.time.LocalDateTime

class ExerciseRepositoryImpl(
    private val exerciseDao: ExerciseDao,
    private val errorHandler: ErrorHandler
) : ExerciseRepository {

    override fun observeExerciseById(exerciseId: String): Flow<Exercise> {
        return exerciseDao.observeById(exerciseId)
            .map { it?.toDomain() ?: throw NoSuchElementException("Exercise with id $exerciseId not found") }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(
                    action = "observeExerciseById",
                    entityId = exerciseId
                )
            )
    }

    override fun observeAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.observeAll()
            .map { exercisesDb ->
                exercisesDb.map { it.toDomain() }
            }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(action = "observeAllExercises")
            )
    }

    override suspend fun createExercise(exercise: Exercise): Result<Unit> = try {
        exerciseDao.insert(exercise.toDb())
        Result.success(Unit)
    } catch (e: Exception) {
        val appException = when (e) {
            is SQLException -> DataException.DatabaseError(
                operation = "insertExercise",
                cause = e
            )
            else -> e.toAppException()
        }
        errorHandler.log(
            appException,
            ErrorContext(
                action = "createExercise",
                entityId = exercise.id
            )
        )
        Result.failure(appException)
    }

    override suspend fun updateExercise(exercise: Exercise): Result<Unit> = try {
        exerciseDao.insert(exercise.toDb().copy(updatedAt = LocalDateTime.now()))
        Result.success(Unit)
    } catch (e: Exception) {
        val appException = when (e) {
            is SQLException -> DataException.DatabaseError(
                operation = "updateExercise",
                cause = e
            )
            else -> e.toAppException()
        }
        errorHandler.log(
            appException,
            ErrorContext(
                action = "updateExercise",
                entityId = exercise.id
            )
        )
        Result.failure(appException)
    }

    override suspend fun deleteExercise(exerciseId: String): Result<Unit> = try {
        val exercise = exerciseDao.getById(exerciseId)
        if (exercise != null) {
            // Сначала удаляем из шаблонов (вручную, так как нет Foreign Key)
            exerciseDao.deleteFromAllTemplates(exerciseId)
            // Затем удаляем само упражнение
            exerciseDao.deleteById(exerciseId)
            Result.success(Unit)
        } else {
            val appException = DomainException.EntityNotFound(
                entityType = "Exercise",
                entityId = exerciseId
            )
            errorHandler.log(
                appException,
                ErrorContext(
                    action = "deleteExercise",
                    entityId = exerciseId
                )
            )
            Result.failure(appException)
        }
    } catch (e: Exception) {
        val appException = when (e) {
            is SQLException -> DataException.DatabaseError(
                operation = "deleteExercise",
                cause = e
            )
            else -> e.toAppException()
        }
        errorHandler.log(
            appException,
            ErrorContext(
                action = "deleteExercise",
                entityId = exerciseId
            )
        )
        Result.failure(appException)
    }
}
