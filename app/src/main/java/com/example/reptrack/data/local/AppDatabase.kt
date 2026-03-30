package com.example.reptrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reptrack.data.local.converters.DateTimeConverters
import com.example.reptrack.data.local.converters.ExerciseConverters
import com.example.reptrack.data.local.MIGRATION_2_3
import com.example.reptrack.data.local.dao.ExerciseDao
import com.example.reptrack.data.local.dao.StatisticDao
import com.example.reptrack.data.local.dao.UserDao
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.dao.WorkoutTemplateDao
import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.GdprConsentDb
import com.example.reptrack.data.local.models.TemplateExerciseDb
import com.example.reptrack.data.local.models.UserDb
import com.example.reptrack.data.local.models.WeightRecordDb
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.data.local.models.WorkoutSessionDb
import com.example.reptrack.data.local.models.WorkoutSetDb
import com.example.reptrack.data.local.models.WorkoutTemplateDb
import com.example.reptrack.data.local.models.statistics.ChartTemplateDb
import com.example.reptrack.data.local.models.statistics.ExerciseLineConfigDb
import com.example.reptrack.data.local.models.statistics.FriendConfigDb
import com.example.reptrack.data.local.models.statistics.SetConfigDb

@Database(
    entities = [
        ExerciseDb::class,
        UserDb::class,
        WorkoutSessionDb::class,
        WorkoutExerciseDb::class,
        WorkoutSetDb::class,
        WeightRecordDb::class,
        WorkoutTemplateDb::class,
        TemplateExerciseDb::class,
        GdprConsentDb::class,
        ChartTemplateDb::class,
        ExerciseLineConfigDb::class,
        FriendConfigDb::class,
        SetConfigDb::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class, ExerciseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun templateDao(): WorkoutTemplateDao
    abstract fun userDao(): UserDao
    abstract fun statisticDao(): StatisticDao

    companion object {
        fun getInstance(context: Context, userId: String): AppDatabase {
            val dbName = "workout_db_$userId"
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                dbName
            )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        populateDatabaseSql(db)
                    }
                })
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()
        }

        fun deleteUserDatabase(context: Context, userId: String): Boolean {
            return try {
                val dbName = "workout_db_$userId"
                context.deleteDatabase(dbName)
            } catch (e: Exception) {
                android.util.Log.e("AppDatabase", "Failed to delete database: ${e.message}")
                false
            }
        }

        private fun populateDatabaseSql(db: SupportSQLiteDatabase) {
            val exercises = listOf(

                Triple("bench_press", "Bench Press", "CHEST"),
                Triple("incline_bench_press", "Incline Bench Press", "CHEST"),
                Triple("dumbbell_fly", "Dumbbell Fly", "CHEST"),
                Triple("cable_fly", "Cable Fly", "CHEST"),
                Triple("push_ups", "Push Ups", "CHEST"),

                Triple("deadlift", "Deadlift", "BACK"),
                Triple("pull_ups", "Pull Ups", "BACK"),
                Triple("barbell_row", "Barbell Row", "BACK"),
                Triple("lat_pulldown", "Lat Pulldown", "BACK"),
                Triple("seated_cable_row", "Seated Cable Row", "BACK"),

                Triple("squat", "Squat", "LEGS"),
                Triple("leg_press", "Leg Press", "LEGS"),
                Triple("lunges", "Lunges", "LEGS"),
                Triple("leg_curl", "Leg Curl", "LEGS"),
                Triple("calf_raises", "Calf Raises", "LEGS"),

                Triple("bicep_curls", "Bicep Curls", "ARMS"),
                Triple("tricep_pushdown", "Tricep Pushdown", "ARMS"),
                Triple("hammer_curls", "Hammer Curls", "ARMS"),
                Triple("overhead_press", "Overhead Press", "ARMS"),
                Triple("skull_crushers", "Skull Crushers", "ARMS"),

                Triple("crunches", "Crunches", "ABS"),
                Triple("plank", "Plank", "ABS"),
                Triple("leg_raises", "Leg Raises", "ABS"),
                Triple("russian_twist", "Russian Twist", "ABS"),
                Triple("cable_crunch", "Cable Crunch", "ABS"),

                Triple("treadmill", "Treadmill", "CARDIO"),
                Triple("cycling", "Cycling", "CARDIO"),
                Triple("elliptical", "Elliptical", "CARDIO"),
                Triple("rowing_machine", "Rowing Machine", "CARDIO"),
                Triple("jump_rope", "Jump Rope", "CARDIO")
            )

            exercises.forEach { (id, name, muscleGroup) ->
                db.execSQL(
                    """
                    INSERT INTO exercise (id, name, muscleGroup, type, iconRes, iconColor, backgroundRes, backgroundColor, isCustom, updatedAt, deletedAt)
                    VALUES (?, ?, ?, 'WEIGHT_REPS', NULL, NULL, NULL, NULL, 0, 0, NULL)
                    """.trimIndent(),
                    arrayOf(id, name, muscleGroup)
                )
            }
        }

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
                    }
                    try {
                        database.execSQL("ALTER TABLE $table ADD COLUMN deletedAt INTEGER")
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}
