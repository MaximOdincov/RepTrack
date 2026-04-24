package com.example.reptrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reptrack.data.local.aggregates.WorkoutTemplateWithExercises
import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.TemplateExerciseDb
import com.example.reptrack.data.local.models.WorkoutTemplateDb
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface WorkoutTemplateDao {

    @Transaction
    @Query("SELECT * FROM workout_templates WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeTemplates(): Flow<List<WorkoutTemplateWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_templates WHERE id = :templateId AND deletedAt IS NULL LIMIT 1")
    fun observeTemplateById(templateId: String): Flow<WorkoutTemplateWithExercises?>

    // New method: Get template with exercises in correct order
    @Query("""
        SELECT te.exerciseId, e.* FROM template_exercises te
        INNER JOIN exercise e ON te.exerciseId = e.id
        WHERE te.templateId = :templateId
        ORDER BY te.exerciseOrder ASC
    """)
    fun getExercisesForTemplateOrdered(templateId: String): Flow<List<ExerciseDb>>

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
        android.util.Log.i("WorkoutTemplateDao", "insertFullTemplate: id=${template.id}, week1Days=${template.week1Days}, week2Days=${template.week2Days}")
        insertTemplate(template)
        insertTemplateExercises(
            exerciseIds.mapIndexed { index, exerciseId ->
                TemplateExerciseDb(
                    templateId = template.id,
                    exerciseId = exerciseId,
                    exerciseOrder = index
                )
            }
        )
        android.util.Log.i("WorkoutTemplateDao", "insertFullTemplate: completed, inserted ${exerciseIds.size} exercises")
    }

    @Query("SELECT * FROM workout_templates")
    suspend fun getAllTemplates(): List<WorkoutTemplateDb>

    @Query("SELECT * FROM template_exercises")
    suspend fun getAllTemplateExercises(): List<TemplateExerciseDb>

    @Query("UPDATE workout_templates SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE id = :templateId")
    suspend fun deleteTemplate(templateId: String, deletedAt: LocalDateTime, updatedAt: LocalDateTime)

    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteTemplateExercises(templateId: String)

    @Query("""
        SELECT exerciseId FROM template_exercises
        WHERE templateId = :templateId
        ORDER BY exerciseOrder ASC
    """)
    suspend fun getOrderedExerciseIds(templateId: String): List<String>

    @Query("""
        SELECT exerciseId FROM template_exercises
        WHERE templateId = :templateId
        ORDER BY exerciseOrder ASC
    """)
    fun observeOrderedExerciseIds(templateId: String): Flow<List<String>>
}