package com.example.reptrack.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reptrack.data.local.Database
import com.example.reptrack.data.local.MIGRATION_2_3
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val tables = listOf(
            "exercise",
            "users",
            "workout_sessions",
            "workout_exercises",
            "workout_sets",
            "weight_records",
            "workout_templates",
            "template_exercises",
            "gdpr_consent",
            "chart_templates",
            "exercise_line_configs",
            "friend_configs",
            "set_configs"
        )

        tables.forEach { table ->
            try {
                database.execSQL("ALTER TABLE $table ADD COLUMN updatedAt INTEGER")
            } catch (e: Exception) {
                // column may already exist
            }
            try {
                database.execSQL("ALTER TABLE $table ADD COLUMN deletedAt INTEGER")
            } catch (e: Exception) {
                // column may already exist
            }
        }
    }
}

val databaseModule = module {

    single<Database> {
        Room.databaseBuilder(
            androidContext(),
            Database::class.java,
            "workout_db"
        )
            .fallbackToDestructiveMigration(false)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .createFromAsset("workout_db_prepopulated.db")
            .build()
    }

    single { get<Database>().exerciseDao() }
    single { get<Database>().workoutDao() }
    single { get<Database>().templateDao() }
    single { get<Database>().userDao() }
}
