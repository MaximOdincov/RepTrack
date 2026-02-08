package com.example.reptrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.reptrack.data.local.converters.DateTimeConverters
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
@TypeConverters(DateTimeConverters::class)
abstract class Database : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun templateDao(): WorkoutTemplateDao
    abstract fun userDao(): UserDao
    abstract fun statisticDao(): StatisticDao
}