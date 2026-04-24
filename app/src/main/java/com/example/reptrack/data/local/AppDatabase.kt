package com.example.reptrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback
import com.example.reptrack.data.local.converters.DateTimeConverters
import com.example.reptrack.data.local.converters.ExerciseConverters
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
    version = 1,
    exportSchema = false
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
                .addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Включаем Foreign Keys для CASCADE
                        db.execSQL("PRAGMA foreign_keys=ON")
                        android.util.Log.d("AppDatabase", "Foreign Keys enabled")
                    }
                })
                .build()
        }

        fun deleteUserDatabase(context: Context, userId: String): Boolean {
            android.util.Log.e("AppDatabase", "!!! deleteUserDatabase CALLED: userId=$userId !!!")
            android.util.Log.e("AppDatabase", "Stack trace:", Exception())
            return try {
                val dbName = "workout_db_$userId"
                val deleted = context.deleteDatabase(dbName)
                android.util.Log.e("AppDatabase", "Database deleted: $deleted")
                deleted
            } catch (e: Exception) {
                android.util.Log.e("AppDatabase", "Failed to delete database: ${e.message}")
                false
            }
        }
    }
}
