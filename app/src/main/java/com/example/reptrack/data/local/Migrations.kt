package com.example.reptrack.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new table with CASCADE delete
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

        // Copy data from old table to new table
        database.execSQL("""
            INSERT INTO exercise_line_configs_new (id, templateId, exerciseId, updatedAt, deletedAt)
            SELECT id, templateId, exerciseId, updatedAt, deletedAt FROM exercise_line_configs
        """.trimIndent())

        // Drop old table
        database.execSQL("DROP TABLE exercise_line_configs")

        // Rename new table to original name
        database.execSQL("ALTER TABLE exercise_line_configs_new RENAME TO exercise_line_configs")
    }
}
