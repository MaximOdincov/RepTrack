package com.example.reptrack.di

import com.example.reptrack.data.backup.BackupRepository
import com.example.reptrack.data.backup.FirebaseBackupDataSource
import com.example.reptrack.data.backup.SyncPreferences
import com.example.reptrack.domain.backup.SyncUseCase
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val backupModule = module {

    single { FirebaseFirestore.getInstance() }

    single { FirebaseBackupDataSource(get()) }

    single { SyncPreferences(androidContext()) }

    single {
        BackupRepository(
            firebaseDataSource = get(),
            exerciseDao = get(),
            workoutDao = get(),
            templateDao = get(),
            userDao = get(),
            statisticDao = get()
        )
    }

    // Domain layer
    single { SyncUseCase(get(), get()) }
}
