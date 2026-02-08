package com.example.reptrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reptrack.data.local.models.ExerciseDb
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise ORDER BY name")
    fun observeAll(): Flow<List<ExerciseDb>>

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getById(id: String): ExerciseDb?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseDb>)

    @Delete
    suspend fun delete(exercise: ExerciseDb)

    @Query("SELECT * FROM exercise")
    suspend fun getAllExercises(): List<ExerciseDb>
}