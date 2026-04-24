package com.example.reptrack.data.workout.repositories

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndHandle
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WorkoutSessionRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val authRepository: AuthRepository,
    private val errorHandler: ErrorHandler
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
        val userId = authRepository.getCurrentUser()?.id
            ?: return flowOf(null)

        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(LocalTime.MAX)

        return workoutDao.observeSessionByDate(userId, startOfDay, endOfDay)
            .map { it?.toDomain() }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(
                    action = "observeSessionByDate",
                    additionalInfo = mapOf("date" to date.toString())
                )
            )
    }

    override suspend fun getSessionByDate(date: LocalDate): WorkoutSession? {
        val userId = authRepository.getCurrentUser()?.id

        if (userId == null) {
            android.util.Log.e("SessionDB", "getUser() returned null")
            return null
        }

        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(LocalTime.MAX)

        val session = workoutDao.getSessionByDate(userId, startOfDay, endOfDay)

        if (session != null) {
            android.util.Log.d("SessionDB", "getSessionByDate FOUND: id=${session.session.id}, deletedAt=${session.session.deletedAt}, exercisesCount=${session.exercises.size}")
        } else {
            android.util.Log.e("SessionDB", "!!! SESSION NOT FOUND for date=$date !!!")

            // Debug: show ALL sessions in DB for this user
            val allSessions = workoutDao.debugGetAllSessions(userId)
            android.util.Log.e("SessionDB", "!!! ALL SESSIONS IN DB for user $userId (${allSessions.size} total): !!!")
            allSessions.forEach { s ->
                android.util.Log.e("SessionDB", "  - id=${s.id}, date=${s.date}, status=${s.status}, deletedAt=${s.deletedAt}")
            }
        }

        return session?.toDomain()
    }

    override suspend fun createSession(session: WorkoutSession): Result<Unit> {
        return try {
            android.util.Log.d("SessionDB", "createSession START: id=${session.id}, date=${session.date}, exercises=${session.exercises.size}")

            val sessionDb = session.toDb()
            val exercisesDb = session.exercises.map { exercise ->
                exercise.toDb(session.id)
            }
            val setsDb = session.exercises.flatMap { exercise ->
                exercise.sets.map { set ->
                    set.toDb(exercise.id)
                }
            }

            android.util.Log.d("SessionDB", "Inserting: sessionDb.deletedAt=${sessionDb.deletedAt}, exercises=${exercisesDb.size}, sets=${setsDb.size}")

            workoutDao.insertFullWorkout(
                session = sessionDb,
                exercises = exercisesDb,
                sets = setsDb
            )

            android.util.Log.d("SessionDB", "createSession SUCCESS: id=${session.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SessionDB", "createSession FAILED: id=${session.id}, error=${e.message}", e)
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

    override suspend fun updateSessionStatus(sessionId: String, status: com.example.reptrack.domain.workout.entities.WorkoutStatus): Result<Unit> {
        return try {
            workoutDao.updateSessionStatus(
                sessionId = sessionId,
                status = status.name,
                updatedAt = LocalDateTime.now()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        android.util.Log.d("SessionDB", "deleteSession: sessionId=$sessionId")
        return try {
            // CASCADE в FOREIGN KEY должен автоматически удалить упражнения и сеты
            workoutDao.deleteSession(sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SessionDB", "deleteSession failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
