package com.example.reptrack.data.seeder

import android.content.Context
import android.util.Log
import com.example.reptrack.data.local.dao.ExerciseDao
import com.example.reptrack.data.local.dao.WorkoutTemplateDao
import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.WorkoutTemplateDb
import com.example.reptrack.data.local.models.TemplateExerciseDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Seeds the database with default exercises and templates
 * Should be called once per user when they first create their account
 */
class DatabaseSeeder(
    private val context: Context,
    private val exerciseDao: ExerciseDao,
    private val templateDao: WorkoutTemplateDao
) {

    companion object {
        private const val TAG = "DatabaseSeeder"
    }

    /**
     * Seed the database with all default exercises and templates
     */
    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting database seeding...")

            // Check if exercises already exist
            val existingExercises = exerciseDao.getAllExercises()
            if (existingExercises.isNotEmpty()) {
                Log.d(TAG, "Exercises already exist, skipping exercise seeding")
            } else {
                Log.d(TAG, "Seeding exercises...")
                seedExercises()
            }

            // Check if templates already exist
            val existingTemplates = templateDao.getAllTemplates()
            if (existingTemplates.isNotEmpty()) {
                Log.d(TAG, "Templates already exist, skipping template seeding")
            } else {
                Log.d(TAG, "Seeding templates...")
                seedTemplates()
            }

            Log.d(TAG, "Database seeding completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding database", e)
            throw e
        }
    }

    /**
     * Seed default exercises
     */
    private suspend fun seedExercises() {
        val exercises = DefaultExercises.getAllExercises().map { exercise ->
            val iconResourceName = exercise.iconRes!!
            val iconResId = iconResourceName

            ExerciseDb(
                id = exercise.id,
                name = exercise.name,
                muscleGroup = exercise.muscleGroup,
                type = exercise.type,
                iconRes = if (iconResId != 0) iconResId else null,
                iconColor = exercise.iconColor,
                backgroundRes = null,
                backgroundColor = null,
                isCustom = false,
                deletedAt = null
            )
        }

        exerciseDao.insertAll(exercises)
        Log.d(TAG, "Inserted ${exercises.size} default exercises")
    }

    /**
     * Seed default templates
     */
    private suspend fun seedTemplates() {
        val templates = DefaultTemplates.getAllTemplates()

        templates.forEach { template ->
            val iconResourceName = ExerciseIconMapper.getIconForTemplate(template.id)
            val iconResId = ExerciseIconMapper.getDrawableResourceId(context, iconResourceName)

            // Insert template
            val templateDb = WorkoutTemplateDb(
                id = template.id,
                name = template.name,
                description = template.description,
                iconId = template.iconId,
                iconRes = if (iconResId != 0) iconResId else null,
                iconColor = template.iconColor
            )

            templateDao.insert(templateDb)

            // Insert exercises for the template with order
            template.exerciseIds.forEachIndexed { index, exerciseId ->
                val templateExercise = TemplateExerciseDb(
                    templateId = template.id,
                    exerciseId = exerciseId,
                    exerciseOrder = index
                )
                templateDao.insertExerciseToTemplate(templateExercise)
            }
        }

        Log.d(TAG, "Inserted ${templates.size} default templates")
    }

    /**
     * Check if database is already seeded
     */
    suspend fun isDatabaseSeeded(): Boolean {
        return try {
            val exercises = exerciseDao.getAllExercises()
            val templates = templateDao.getAllTemplates()
            exercises.isNotEmpty() && templates.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if database is seeded", e)
            false
        }
    }

    /**
     * Force reseed database (use with caution - deletes all custom data)
     */
    suspend fun forceReseed() {
        Log.d(TAG, "Force reseeding database...")

        // Note: We're not deleting here to preserve user custom data
        // In production, you might want to implement a more sophisticated migration strategy

        seedExercises()
        seedTemplates()

        Log.d(TAG, "Database force reseed completed")
    }
}