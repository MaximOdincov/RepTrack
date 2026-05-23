package com.example.reptrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reptrack.data.local.models.WeightRecordDb
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface WeightRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: WeightRecordDb)

    @Query("SELECT * FROM weight_records WHERE userId = :userId AND deletedAt IS NULL ORDER BY date DESC")
    fun observeAllRecords(userId: String): Flow<List<WeightRecordDb>>

    @Query("""
        SELECT * FROM weight_records
        WHERE userId = :userId
        AND date BETWEEN :startOfDay AND :endOfDay
        AND deletedAt IS NULL
        LIMIT 1
    """)
    suspend fun getRecordForDate(userId: String, startOfDay: LocalDateTime, endOfDay: LocalDateTime): WeightRecordDb?

    @Query("""
        SELECT * FROM weight_records
        WHERE userId = :userId
        AND deletedAt IS NULL
        ORDER BY date DESC
        LIMIT 1
    """)
    suspend fun getLatestRecord(userId: String): WeightRecordDb?
}