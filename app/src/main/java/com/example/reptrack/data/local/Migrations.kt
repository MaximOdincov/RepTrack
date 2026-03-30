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
