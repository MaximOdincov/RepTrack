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
import com.example.reptrack.data.local.dao.FriendDao
import com.example.reptrack.data.local.dao.StatisticDao
import com.example.reptrack.data.local.dao.UserDao
import com.example.reptrack.data.local.dao.WeightRecordDao
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.dao.WorkoutTemplateDao
import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.FriendDb
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
import com.example.reptrack.data.seeder.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        SetConfigDb::class,
        FriendDb::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class, ExerciseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun templateDao(): WorkoutTemplateDao
    abstract fun userDao(): UserDao
    abstract fun statisticDao(): StatisticDao
    abstract fun friendDao(): FriendDao
    abstract fun weightRecordDao(): WeightRecordDao

    companion object {
        private val instances = mutableMapOf<String, AppDatabase>()

        fun getInstance(context: Context, userId: String): AppDatabase {
            return instances.getOrPut(userId) {
                val dbName = "workout_db_$userId"
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbName
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default data after database is created
                            // We'll get the instance after it's built
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Включаем Foreign Keys для CASCADE
                            db.execSQL("PRAGMA foreign_keys=ON")
                            android.util.Log.d("AppDatabase", "Foreign Keys enabled for user: $userId")
                        }
                    })
                    .build()
            }
        }

        fun closeInstance(userId: String) {
            instances.remove(userId)?.close()
            android.util.Log.d("AppDatabase", "Closed database for user: $userId")
        }

        fun closeAll() {
            instances.values.forEach { it.close() }
            instances.clear()
            android.util.Log.d("AppDatabase", "Closed all database instances")
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
