package com.example.reptrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reptrack.data.local.aggregates.WorkoutSessionWithExercises
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.data.local.models.WorkoutSessionDb
import com.example.reptrack.data.local.models.WorkoutSetDb
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC")
    fun observeSessions(userId: String): Flow<List<WorkoutSessionWithExercises>>

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

    @Query("UPDATE workout_sessions SET deletedAt = (strftime('%s','now') * 1000), updatedAt = (strftime('%s','now') * 1000) WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("SELECT * FROM workout_sets")
    suspend fun getAllSets(): List<WorkoutSetDb>

    @Query("SELECT * FROM workout_exercises")
    suspend fun getAllExercises(): List<WorkoutExerciseDb>
}