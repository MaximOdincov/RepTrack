package com.example.reptrack.data.workout.repositories

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.exceptions.DataException
import com.example.reptrack.core.error.exceptions.DomainException
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndHandle
import com.example.reptrack.core.extensions.toAppException
import com.example.reptrack.data.local.dao.ExerciseDao
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.data.local.models.WorkoutSetDb
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.sql.SQLException
import java.time.LocalDateTime

class WorkoutExerciseRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val errorHandler: ErrorHandler
) : WorkoutExerciseRepository {

    override fun observeById(exerciseId: String): Flow<WorkoutExercise> {
        return workoutDao.observeWorkoutExerciseWithSets(exerciseId)
            .map { it?.toDomain() ?: throw NoSuchElementException("WorkoutExercise with id $exerciseId not found") }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(
                    action = "observeById",
                    entityId = exerciseId
                )
            )
    }

    override fun observeBySession(sessionId: String): Flow<List<WorkoutExercise>> {
        return workoutDao.observeExercisesBySession(sessionId)
            .map { exercisesWithSets ->
                exercisesWithSets.map { it.toDomain() }
            }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(
                    action = "observeBySession",
                    entityId = sessionId
                )
            )
    }

    override suspend fun create(exercise: WorkoutExercise, sessionId: String): Result<Unit> = try {
        workoutDao.insertExercises(listOf(exercise.toDb(sessionId)))
        workoutDao.insertSets(exercise.sets.map { it.toDb(exercise.id) })
        Result.success(Unit)
    } catch (e: Exception) {
        val appException = when (e) {
            is SQLException -> DataException.DatabaseError(
                operation = "insertExerciseAndSets",
                cause = e
            )
            else -> e.toAppException()
        }
        errorHandler.log(
            appException,
            ErrorContext(
                action = "create",
                entityId = exercise.id
            )
        )
        Result.failure(appException)
    }

    override suspend fun update(exercise: WorkoutExercise): Result<Unit> = try {
        android.util.Log.d("WorkoutExerciseRepo", "update: exerciseId=${exercise.id}, sets: ${exercise.sets.size}")

        // Получаем текущие сеты из БД
        val currentSets = workoutDao.getAllSetsForWorkoutExercise(exercise.id)
        val currentSetIds = currentSets.map { it.id }.toSet()
        val newSetIds = exercise.sets.map { it.id }.toSet()

        // Находим сеты, которые нужно удалить (есть в БД, но нет в новом списке)
        val setsToDelete = currentSets.filter { it.id !in newSetIds }

        android.util.Log.d("WorkoutExerciseRepo", "Current sets in DB: ${currentSets.size}, new sets: ${exercise.sets.size}, to delete: ${setsToDelete.size}")

        // Удаляем старые сеты
        if (setsToDelete.isNotEmpty()) {
            setsToDelete.forEach { set ->
                android.util.Log.d("WorkoutExerciseRepo", "Deleting set: ${set.id}")
                workoutDao.deleteSet(set.id)
            }
        }

        // Вставляем/обновляем новые сеты
        val setsDb = exercise.sets.map { it.toDb(exercise.id) }
        workoutDao.insertSets(setsDb)

        android.util.Log.d("WorkoutExerciseRepo", "update SUCCESS: saved ${setsDb.size} sets")
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
                action = "update",
                entityId = exercise.id
            )
        )
        Result.failure(appException)
    }

    override suspend fun delete(exerciseId: String): Result<Unit> = try {
        android.util.Log.d("SessionDB", "WorkoutExerciseRepository.delete: exerciseId=$exerciseId")
        // CASCADE автоматически удалит все сеты этого упражнения
        workoutDao.deleteExerciseById(exerciseId)
        android.util.Log.d("SessionDB", "WorkoutExerciseRepository.delete: SUCCESS")
        Result.success(Unit)
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
                action = "delete",
                entityId = exerciseId
            )
        )
        Result.failure(appException)
    }

    override fun observeBestSetFromLastWorkout(exerciseId: String): Flow<WorkoutSet?> {
        return workoutDao.observeBestSetFromLastWorkout(exerciseId)
            .map { it?.toDomain() }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(
                    action = "observeBestSetFromLastWorkout",
                    entityId = exerciseId
                )
            )
    }

    override fun observeLastCompletedExercise(exerciseId: String): Flow<WorkoutExercise?> {
        return workoutDao.observeLastCompletedExerciseWithSets(exerciseId)
            .map { it?.toDomain() }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(
                    action = "observeLastCompletedExercise",
                    entityId = exerciseId
                )
            )
    }
}
