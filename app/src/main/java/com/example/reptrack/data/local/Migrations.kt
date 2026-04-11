package com.example.reptrack.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS exercise_line_configs_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                templateId INTEGER NOT NULL,
                exerciseId TEXT NOT NULL,
                updatedAt INTEGER NOT NULL,
                deletedAt INTEGER,
                FOREIGN KEY(templateId) REFERENCES chart_templates(id) ON DELETE CASCADE
            )
        """.trimIndent())

        database.execSQL("""
            INSERT INTO exercise_line_configs_new (id, templateId, exerciseId, updatedAt, deletedAt)
            SELECT id, templateId, exerciseId, updatedAt, deletedAt FROM exercise_line_configs
        """.trimIndent())

        database.execSQL("DROP TABLE exercise_line_configs")

        database.execSQL("ALTER TABLE exercise_line_configs_new RENAME TO exercise_line_configs")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns to workout_templates table
        try {
            database.execSQL("ALTER TABLE workout_templates ADD COLUMN description TEXT")
        } catch (e: Exception) {
            // Column might already exist
        }

        try {
            database.execSQL("ALTER TABLE workout_templates ADD COLUMN iconRes INTEGER")
        } catch (e: Exception) {
            // Column might already exist
        }

        try {
            database.execSQL("ALTER TABLE workout_templates ADD COLUMN iconColor TEXT")
        } catch (e: Exception) {
            // Column might already exist
        }
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add exerciseOrder column to template_exercises table
        try {
            database.execSQL("ALTER TABLE template_exercises ADD COLUMN exerciseOrder INTEGER NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            // Column might already exist
        }
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // No schema changes, just version bump for code changes
    }
}
