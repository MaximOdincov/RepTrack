package com.example.reptrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.reptrack.data.local.aggregates.WorkoutExerciseWithSets
import com.example.reptrack.data.local.aggregates.WorkoutSessionWithExercises
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.data.local.models.WorkoutSessionDb
import com.example.reptrack.data.local.models.WorkoutSetDb
import com.example.reptrack.data.local.models.WeightRecordDb
import com.example.reptrack.data.local.models.statistics.BestSetData
import com.example.reptrack.data.local.models.statistics.ExerciseProgressData
import com.example.reptrack.data.local.models.statistics.MuscleGroupFrequencyData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface WorkoutDao {

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC")
    fun observeSessions(userId: String): Flow<List<WorkoutSessionWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    fun observeSessionById(sessionId: String): Flow<WorkoutSessionWithExercises?>

    @Transaction
    @Query("""
        SELECT * FROM workout_sessions
        WHERE userId = :userId
        AND date BETWEEN :fromDate AND :toDate
        ORDER BY date DESC
    """)
    fun observeSessionsInRange(
        userId: String,
        fromDate: LocalDateTime,
        toDate: LocalDateTime
    ): Flow<List<WorkoutSessionWithExercises>>

    @Transaction
    @Query("""
        SELECT * FROM workout_sessions
        WHERE userId = :userId
        AND date BETWEEN :startOfDay AND :endOfDay
        LIMIT 1
    """)
    fun observeSessionByDate(
        userId: String,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Flow<WorkoutSessionWithExercises?>

    @Transaction
    @Query("""
        SELECT * FROM workout_sessions
        WHERE userId = :userId
        AND date BETWEEN :startOfDay AND :endOfDay
        LIMIT 1
    """)
    suspend fun getSessionByDate(
        userId: String,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): WorkoutSessionWithExercises?

    @Transaction
    @Query("""
        SELECT * FROM workout_sessions
        WHERE templateId = :templateId
        AND status = 'PLANNED'
        ORDER BY date ASC
    """)
    fun observeSessionsByTemplateId(templateId: String): Flow<List<WorkoutSessionWithExercises>>

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC")
    suspend fun debugGetAllSessions(userId: String): List<WorkoutSessionDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionDb)

    @Query("UPDATE workout_sessions SET updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateSessionTimestamp(sessionId: String, updatedAt: java.time.LocalDateTime = java.time.LocalDateTime.now())

    @Query("UPDATE workout_sessions SET status = :status, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateSessionStatus(sessionId: String, status: String, updatedAt: java.time.LocalDateTime)

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): WorkoutSessionDb?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<WorkoutExerciseDb>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrUpdateExercise(exercise: WorkoutExerciseDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetDb>)

    @Query("SELECT * FROM workout_exercises WHERE id = :exerciseId LIMIT 1")
    suspend fun getWorkoutExerciseById(exerciseId: String): WorkoutExerciseDb?

    @Query("SELECT * FROM workout_sets WHERE workoutExerciseId = :exerciseId")
    suspend fun getAllSetsForWorkoutExercise(exerciseId: String): List<WorkoutSetDb>

    @Transaction
    suspend fun insertFullWorkout(
        session: WorkoutSessionDb,
        exercises: List<WorkoutExerciseDb>,
        sets: List<WorkoutSetDb>
    ) {
        android.util.Log.d("SessionDB", "insertFullWorkout: sessionId=${session.id}, deletedAt=${session.deletedAt}")
        insertSession(session)
        insertExercises(exercises)
        insertSets(sets)
        updateSessionTimestamp(session.id)
        android.util.Log.d("SessionDB", "insertFullWorkout DONE: inserted ${exercises.size} exercises, ${sets.size} sets")
    }

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM workout_exercises WHERE workoutSessionId = :sessionId")
    suspend fun deleteExercisesBySession(sessionId: String)

    @Query("DELETE FROM workout_sets WHERE workoutExerciseId IN (SELECT id FROM workout_exercises WHERE workoutSessionId = :sessionId)")
    suspend fun deleteSetsBySession(sessionId: String)

    @Query("DELETE FROM workout_exercises WHERE id = :exerciseId")
    suspend fun deleteExerciseById(exerciseId: String)

    @Query("DELETE FROM workout_sets WHERE workoutExerciseId = :exerciseId")
    suspend fun deleteSetsByExercise(exerciseId: String)

    @Query("DELETE FROM workout_sets WHERE id = :setId")
    suspend fun deleteSet(setId: String)

    @Transaction
    @Query("SELECT * FROM workout_exercises WHERE id = :exerciseId LIMIT 1")
    suspend fun getWorkoutExerciseWithSets(exerciseId: String): WorkoutExerciseWithSets?

    @Query("SELECT * FROM workout_sets")
    suspend fun getAllSets(): List<WorkoutSetDb>

    @Query("SELECT * FROM workout_exercises")
    suspend fun getAllExercises(): List<WorkoutExerciseDb>

    @Transaction
    @Query("SELECT * FROM workout_exercises WHERE id = :exerciseId LIMIT 1")
    fun observeWorkoutExerciseWithSets(exerciseId: String): Flow<WorkoutExerciseWithSets?>

    @Transaction
    @Query("""
        SELECT we.* FROM workout_exercises we
        INNER JOIN workout_sessions ws ON we.workoutSessionId = ws.id
        WHERE we.exerciseId = :exerciseId
        AND ws.status = 'COMPLETED'
        ORDER BY ws.date DESC
        LIMIT 1
    """)
    fun observeLastCompletedExerciseWithSets(exerciseId: String): Flow<WorkoutExerciseWithSets?>

    @Transaction
    @Query("""
        SELECT * FROM workout_exercises
        WHERE workoutSessionId = :sessionId
        ORDER BY id
    """)
    fun observeExercisesBySession(sessionId: String): Flow<List<WorkoutExerciseWithSets>>

    @Transaction
    @Query("""
        SELECT ws.* FROM workout_sets ws
        INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
        INNER JOIN workout_sessions session ON we.workoutSessionId = session.id
        WHERE we.exerciseId = :exerciseId
        AND session.status = 'COMPLETED'
        AND ws.isCompleted = 1
        ORDER BY session.date DESC, ws.weight DESC
        LIMIT 1
    """)
    fun observeBestSetFromLastWorkout(exerciseId: String): Flow<WorkoutSetDb?>

    // ========== Statistics Queries ==========

    // Записи веса за период
    @Query("""
        SELECT * FROM weight_records
        WHERE userId = :userId
        AND deletedAt IS NULL
        AND date BETWEEN :fromDate AND :toDate
        ORDER BY date ASC
    """)
    fun observeWeightRecordsInRange(userId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<WeightRecordDb>>

    // Прогресс упражнения по дате и номеру подхода
    @Query("""
        SELECT s.date, ws.weight, ws.setOrder, we.exerciseName
        FROM workout_sets ws
        INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
        INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
        WHERE s.userId = :userId
        AND we.exerciseId = :exerciseId
        AND s.status IN ('COMPLETED', 'IN_PROGRESS')
        AND ws.isCompleted = 1
        AND s.date >= :fromDate
        AND s.date < :toDate
        ORDER BY s.date ASC, ws.setOrder ASC
    """)
    fun observeExerciseProgress(
        userId: String, exerciseId: String, fromDate: LocalDateTime, toDate: LocalDateTime
    ): Flow<List<ExerciseProgressData>>

    // Лучший подход по упражнению за день
    @Query("""
        SELECT s.date, MAX(ws.weight) as bestWeight, ws.reps
        FROM workout_sets ws
        INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
        INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
        WHERE s.userId = :userId
        AND we.exerciseId = :exerciseId
        AND s.status IN ('COMPLETED', 'IN_PROGRESS')
        AND ws.isCompleted = 1
        AND s.date BETWEEN :fromDate AND :toDate
        GROUP BY s.date, ws.reps
        ORDER BY s.date ASC
    """)
    fun observeBestSetPerDay(
        userId: String, exerciseId: String, fromDate: LocalDateTime, toDate: LocalDateTime
    ): Flow<List<BestSetData>>

    // Частота групп мышц за период
    @Query("""
        SELECT we.muscleGroup, COUNT(DISTINCT s.id) as frequency
        FROM workout_exercises we
        INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
        WHERE s.userId = :userId
        AND s.date BETWEEN :fromDate AND :toDate
        GROUP BY we.muscleGroup
    """)
    fun observeMuscleGroupFrequency(
        userId: String, fromDate: LocalDateTime, toDate: LocalDateTime
    ): Flow<List<MuscleGroupFrequencyData>>

    // Проверка наличия упражнения у друга
    @Query("""
        SELECT COUNT(*) > 0
        FROM workout_sets ws
        INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
        INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
        WHERE s.userId = :friendId
        AND we.exerciseId = :exerciseId
        AND s.status = 'COMPLETED'
        AND ws.isCompleted = 1
    """)
    suspend fun friendHasExercise(friendId: String, exerciseId: String): Boolean

    // Получение всех упражнений друга для отладки
    @Query("""
        SELECT DISTINCT we.exerciseId as id, we.exerciseName as name
        FROM workout_exercises we
        INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
        WHERE s.userId = :friendId
        AND s.status = 'COMPLETED'
        ORDER BY we.exerciseName
    """)
    suspend fun debugGetFriendExercises(friendId: String): List<com.example.reptrack.data.local.models.statistics.ExerciseIdNamePair>

    // Детальный дебаг - какие подходы есть у друга для конкретного упражнения
    @Query("""
        SELECT s.id as sessionId, s.status as sessionStatus,
               we.id as exerciseId, we.exerciseId as exerciseRefId,
               ws.id as setId, ws.isCompleted as setCompleted, ws.weight
        FROM workout_sets ws
        INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
        INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
        WHERE s.userId = :friendId
        AND we.exerciseId = :exerciseId
        LIMIT 10
    """)
    suspend fun debugGetFriendExerciseDetails(
        friendId: String,
        exerciseId: String
    ): List<com.example.reptrack.data.local.models.statistics.FriendExerciseDebugDetails>

    // Лучший подход друга для упражнения
    @Query("""
        SELECT s.date, MAX(ws.weight) as bestWeight, ws.reps
        FROM workout_sets ws
        INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
        INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
        WHERE s.userId = :friendId
        AND we.exerciseId = :exerciseId
        AND s.status = 'COMPLETED'
        AND ws.isCompleted = 1
        AND s.date BETWEEN :fromDate AND :toDate
        GROUP BY s.date, ws.reps
        ORDER BY s.date ASC
    """)
    fun observeFriendBestSetPerDay(
        friendId: String, exerciseId: String, fromDate: LocalDateTime, toDate: LocalDateTime
    ): Flow<List<BestSetData>>

    // Записи веса друга
    @Query("""
        SELECT * FROM weight_records
        WHERE userId = :friendId
        AND deletedAt IS NULL
        AND date BETWEEN :fromDate AND :toDate
        ORDER BY date ASC
    """)
    fun observeFriendWeightRecords(friendId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<WeightRecordDb>>

    // ========== Friend Data Management ==========

    // Delete all workout sessions for a user
    @Query("DELETE FROM workout_sessions WHERE userId = :userId")
    suspend fun deleteAllSessionsForUser(userId: String)

    // Delete all workout exercises for a user (via userId in workout_sessions)
    @Query("""
        DELETE FROM workout_exercises
        WHERE id IN (
            SELECT we.id FROM workout_exercises we
            INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
            WHERE s.userId = :userId
        )
    """)
    suspend fun deleteAllExercisesForUser(userId: String)

    // Delete all workout sets for a user (via userId in workout_sessions)
    @Query("""
        DELETE FROM workout_sets
        WHERE workoutExerciseId IN (
            SELECT ws.workoutExerciseId FROM workout_sets ws
            INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
            INNER JOIN workout_sessions s ON we.workoutSessionId = s.id
            WHERE s.userId = :userId
        )
    """)
    suspend fun deleteAllSetsForUser(userId: String)
}