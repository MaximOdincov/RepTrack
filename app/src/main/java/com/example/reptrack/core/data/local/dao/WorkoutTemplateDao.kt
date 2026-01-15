package com.example.reptrack.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reptrack.core.data.local.aggregates.WorkoutTemplateWithExercises
import com.example.reptrack.core.data.local.models.TemplateExerciseDb
import com.example.reptrack.core.data.local.models.WorkoutTemplateDb
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTemplateDao {

    @Transaction
    @Query("SELECT * FROM workout_templates")
    fun observeTemplates(): Flow<List<WorkoutTemplateWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercises(
        refs: List<TemplateExerciseDb>
    )

    @Transaction
    suspend fun insertFullTemplate(
        template: WorkoutTemplateDb,
        exerciseIds: List<String>
    ) {
        insertTemplate(template)
        insertTemplateExercises(
            exerciseIds.map {
                TemplateExerciseDb(
                    templateId = template.id,
                    exerciseId = it
                )
            }
        )
    }
}