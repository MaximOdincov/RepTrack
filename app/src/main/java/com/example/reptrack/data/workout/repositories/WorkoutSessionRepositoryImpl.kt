package com.example.reptrack.data.workout.repositories

import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.mappers.DomainMapper.toDb
import com.example.reptrack.data.local.mappers.DomainMapper.toDomain
import com.example.reptrack.domain.workout.WorkoutSession
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WorkoutSessionRepositoryImpl(
    private val workoutDao: WorkoutDao
) : WorkoutSessionRepository {

    override fun observeSessionById(sessionId: String): Flow<WorkoutSession?> {
        return workoutDao.observeSessionById(sessionId)
            .map { it?.toDomain() }
            .catch { e -> throw e }
    }

    override fun observeSessionsInRange(
        fromDate: Long,
        toDate: Long
    ): Flow<List<WorkoutSession>> {
        val fromDateTime = LocalDateTime.ofEpochSecond(fromDate / 1000, 0, null)
        val toDateTime = LocalDateTime.ofEpochSecond(toDate / 1000, 0, null)

        // Примечание: здесь нужен userId, который мы получим из SessionManager или передадим параметром
        // Для MVP возвращаем пустой Flow, так как DAO требует userId
        throw NotImplementedError("observeSessionsInRange requires userId - will be implemented with SessionManager")
    }

    override fun observeSessionByDate(date: LocalDate): Flow<WorkoutSession?> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(LocalTime.MAX)

        // Примечание: здесь нужен userId
        throw NotImplementedError("observeSessionByDate requires userId - will be implemented with SessionManager")
    }

    override suspend fun createSession(session: WorkoutSession): Result<Unit> {
        return try {
            val sessionDb = session.toDb()
            val exercisesDb = session.exercises.map { exercise ->
                exercise.toDb(session.id)
            }
            val setsDb = session.exercises.flatMap { exercise ->
                exercise.sets.map { set ->
                    set.toDb(exercise.id)
                }
            }

            workoutDao.insertFullWorkout(
                session = sessionDb,
                exercises = exercisesDb,
                sets = setsDb
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSession(session: WorkoutSession): Result<Unit> {
        return try {
            val sessionDb = session.toDb()
            val exercisesDb = session.exercises.map { exercise ->
                exercise.toDb(session.id)
            }
            val setsDb = session.exercises.flatMap { exercise ->
                exercise.sets.map { set ->
                    set.toDb(exercise.id)
                }
            }

            workoutDao.insertFullWorkout(
                session = sessionDb,
                exercises = exercisesDb,
                sets = setsDb
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            val now = LocalDateTime.now()
            workoutDao.deleteSession(sessionId, now, now)
            workoutDao.deleteExercisesBySession(sessionId, now, now)
            workoutDao.deleteSetsBySession(sessionId, now, now)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
