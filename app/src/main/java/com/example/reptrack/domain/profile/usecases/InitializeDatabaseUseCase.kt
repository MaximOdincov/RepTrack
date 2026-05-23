package com.example.reptrack.domain.profile.usecases

import com.example.reptrack.data.local.AppDatabase
import com.example.reptrack.data.seeder.DatabaseSeeder

/**
 * Initialize database with default data
 */
class InitializeDatabaseUseCase(
    private val context: android.content.Context,
    private val database: AppDatabase
) {
    suspend operator fun invoke() {
        val seeder = DatabaseSeeder(
            context = context,
            exerciseDao = database.exerciseDao(),
            templateDao = database.templateDao()
        )

        if (!seeder.isDatabaseSeeded()) {
            seeder.seedDatabase()
        }
    }
}