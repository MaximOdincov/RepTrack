package com.example.reptrack.di

import com.example.reptrack.data.local.AppDatabase
import com.example.reptrack.data.local.dao.ExerciseDao
import com.example.reptrack.data.local.dao.StatisticDao
import com.example.reptrack.data.local.dao.UserDao
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.dao.WorkoutTemplateDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        val authRepository = get<com.example.reptrack.domain.auth.AuthRepository>()
        val userId = authRepository.getCurrentUser()?.id

        if (userId == null) {
            throw IllegalStateException("User not authenticated. Cannot initialize database. This should only be called from authenticated screens.")
        }
        AppDatabase.getInstance(androidContext(), userId)
    }

    single { get<AppDatabase>().exerciseDao() }
    single { get<AppDatabase>().workoutDao() }
    single { get<AppDatabase>().templateDao() }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().statisticDao() }
}
