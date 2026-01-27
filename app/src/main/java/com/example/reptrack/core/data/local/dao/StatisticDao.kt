package com.example.reptrack.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reptrack.core.data.local.aggregates.ChartTemplateWithFriends
import com.example.reptrack.core.data.local.aggregates.ExerciseLineTemplate
import com.example.reptrack.core.data.local.models.statistics.ChartTemplateDb
import com.example.reptrack.core.data.local.models.statistics.ExerciseLineConfigDb
import com.example.reptrack.core.data.local.models.statistics.FriendConfigDb
import com.example.reptrack.core.data.local.models.statistics.SetConfigDb
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: ChartTemplateDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendConfigs(configs: List<FriendConfigDb>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLineConfigs(configs: List<ExerciseLineConfigDb>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetConfigs(configs: List<SetConfigDb>)

    @Query("UPDATE chart_templates SET deletedAt = (strftime('%s','now') * 1000), updatedAt = (strftime('%s','now') * 1000) WHERE userId = :userId AND id = :id")
    suspend fun deleteTemplate(userId: String, id: Long)

    @Query("UPDATE friend_configs SET deletedAt = (strftime('%s','now') * 1000), updatedAt = (strftime('%s','now') * 1000) WHERE templateId = :templateId")
    suspend fun deleteFriendConfigsByTemplate(templateId: Long)

    @Query("""
        UPDATE set_configs
        SET deletedAt = (strftime('%s','now') * 1000), updatedAt = (strftime('%s','now') * 1000)
        WHERE exerciseConfigId IN (
            SELECT id FROM exercise_line_configs WHERE templateId = :templateId
        )
    """)
    suspend fun deleteSetConfigsByTemplate(templateId: Long)

    @Query("UPDATE exercise_line_configs SET deletedAt = (strftime('%s','now') * 1000), updatedAt = (strftime('%s','now') * 1000) WHERE templateId = :templateId")
    suspend fun deleteExerciseLineConfigsByTemplate(templateId: Long)

    @Transaction
    @Query("SELECT * FROM chart_templates WHERE userId = :userId")
    fun getTemplatesWithFriends(
        userId: String
    ): Flow<List<ChartTemplateWithFriends>>

    @Transaction
    @Query("""
        SELECT * FROM chart_templates 
        WHERE userId = :userId 
        AND type = 'EXERCISE_LINE'
    """)
    suspend fun getExerciseLineTemplates(
        userId: String
    ): List<ExerciseLineTemplate>

    @Query("SELECT * FROM chart_templates")
    suspend fun getAllTemplates(): List<ChartTemplateDb>

    @Query("SELECT * FROM friend_configs")
    suspend fun getAllFriendConfigs(): List<FriendConfigDb>

    @Query("SELECT * FROM exercise_line_configs")
    suspend fun getAllExerciseLineConfigs(): List<ExerciseLineConfigDb>

    @Query("SELECT * FROM set_configs")
    suspend fun getAllSetConfigs(): List<SetConfigDb>
}

