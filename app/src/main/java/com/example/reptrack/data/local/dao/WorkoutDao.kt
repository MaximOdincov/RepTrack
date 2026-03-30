package com.example.reptrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reptrack.data.local.aggregates.WorkoutExerciseWithSets
import com.example.reptrack.data.local.aggregates.WorkoutSessionWithExercises
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.data.local.models.WorkoutSessionDb
import com.example.reptrack.data.local.models.WorkoutSetDb
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface WorkoutDao {

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId AND deletedAt IS NULL ORDER BY date DESC")
    fun observeSessions(userId: String): Flow<List<WorkoutSessionWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId AND deletedAt IS NULL LIMIT 1")
    fun observeSessionById(sessionId: String): Flow<WorkoutSessionWithExercises?>

    @Transaction
    @Query("""
        SELECT * FROM workout_sessions
        WHERE userId = :userId
        AND date BETWEEN :fromDate AND :toDate
        AND deletedAt IS NULL
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
        AND deletedAt IS NULL
        LIMIT 1
    """)
    fun observeSessionByDate(
        userId: String,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Flow<WorkoutSessionWithExercises?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<WorkoutExerciseDb>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetDb>)

    @Transaction
    suspend fun insertFullWorkout(
        session: WorkoutSessionDb,
        exercises: List<WorkoutExerciseDb>,
        sets: List<WorkoutSetDb>
    ) {
        insertSession(session)
        insertExercises(exercises)
        insertSets(sets)
    }

    @Query("UPDATE workout_sessions SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String, deletedAt: LocalDateTime, updatedAt: LocalDateTime)

    @Query("UPDATE workout_exercises SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE workoutSessionId = :sessionId")
    suspend fun deleteExercisesBySession(sessionId: String, deletedAt: LocalDateTime, updatedAt: LocalDateTime)

    @Query("UPDATE workout_sets SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE workoutExerciseId IN (SELECT id FROM workout_exercises WHERE workoutSessionId = :sessionId)")
    suspend fun deleteSetsBySession(sessionId: String, deletedAt: LocalDateTime, updatedAt: LocalDateTime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: WorkoutExerciseDb)

    @Query("UPDATE workout_exercises SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE id = :exerciseId")
    suspend fun deleteExerciseById(exerciseId: String, deletedAt: LocalDateTime, updatedAt: LocalDateTime)

    @Transaction
    @Query("SELECT * FROM workout_exercises WHERE id = :exerciseId AND deletedAt IS NULL LIMIT 1")
    suspend fun getWorkoutExerciseWithSets(exerciseId: String): WorkoutExerciseWithSets?

    @Query("SELECT * FROM workout_sets")
    suspend fun getAllSets(): List<WorkoutSetDb>

    @Query("SELECT * FROM workout_exercises")
    suspend fun getAllExercises(): List<WorkoutExerciseDb>

    @Transaction
    @Query("SELECT * FROM workout_exercises WHERE id = :exerciseId AND deletedAt IS NULL LIMIT 1")
    fun observeWorkoutExerciseWithSets(exerciseId: String): Flow<WorkoutExerciseWithSets?>

    @Transaction
    @Query("""
        SELECT we.* FROM workout_exercises we
        INNER JOIN workout_sessions ws ON we.workoutSessionId = ws.id
        WHERE we.exerciseId = :exerciseId
        AND ws.status = 'COMPLETED'
        AND we.deletedAt IS NULL
        AND ws.deletedAt IS NULL
        ORDER BY ws.date DESC
        LIMIT 1
    """)
    fun observeLastCompletedExerciseWithSets(exerciseId: String): Flow<WorkoutExerciseWithSets?>

    @Transaction
    @Query("""
        SELECT * FROM workout_exercises
        WHERE workoutSessionId = :sessionId
        AND deletedAt IS NULL
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
        AND ws.deletedAt IS NULL
        AND we.deletedAt IS NULL
        AND session.deletedAt IS NULL
        AND ws.isCompleted = 1
        ORDER BY session.date DESC, ws.weight DESC
        LIMIT 1
    """)
    fun observeBestSetFromLastWorkout(exerciseId: String): Flow<WorkoutSetDb?>
}