package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.data.backup.FirebaseBackupDataSource
import com.example.reptrack.data.statistics.repositories.StatisticsRepositoryImpl
import com.example.reptrack.domain.friends.usecases.GetFriendsUseCase
import com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase
import com.example.reptrack.domain.statistics.repositories.StatisticsRepository
import com.example.reptrack.domain.statistics.usecases.GetExerciseChartDataUseCase
import com.example.reptrack.domain.statistics.usecases.GetFriendExerciseChartDataUseCase
import com.example.reptrack.domain.statistics.usecases.GetFriendExerciseDataFromFirebaseUseCase
import com.example.reptrack.domain.statistics.usecases.GetFriendMuscleGroupChartDataUseCase
import com.example.reptrack.domain.statistics.usecases.GetFriendMuscleGroupDataFromFirebaseUseCase
import com.example.reptrack.domain.statistics.usecases.GetFriendWeightChartDataUseCase
import com.example.reptrack.domain.statistics.usecases.GetMuscleGroupChartDataUseCase
import com.example.reptrack.domain.statistics.usecases.GetWeightChartDataUseCase
import com.example.reptrack.domain.statistics.usecases.UpdateWeightUseCase
import com.example.reptrack.presentation.statistics.stores.StatisticsStore
import com.example.reptrack.presentation.statistics.stores.StatisticsStoreFactory
import org.koin.dsl.module

val statisticsModule = module {
    // Repository
    single<StatisticsRepository> {
        StatisticsRepositoryImpl(
            workoutDao = get(),
            weightRecordDao = get(),
            statisticDao = get()
        )
    }

    // Use Cases
    factory {
        GetWeightChartDataUseCase(
            repository = get()
        )
    }

    factory {
        GetFriendWeightChartDataUseCase(
            repository = get()
        )
    }

    factory {
        UpdateWeightUseCase(
            repository = get()
        )
    }

    factory {
        GetExerciseChartDataUseCase(
            repository = get()
        )
    }

    factory {
        GetFriendExerciseDataFromFirebaseUseCase(
            firebaseDataSource = get(),
            exerciseDao = get()
        )
    }

    factory {
        GetMuscleGroupChartDataUseCase(
            repository = get()
        )
    }

    factory {
        GetFriendMuscleGroupChartDataUseCase(
            repository = get()
        )
    }

    factory {
        GetFriendMuscleGroupDataFromFirebaseUseCase(
            firebaseDataSource = get()
        )
    }

    // Store Factory
    factory<StatisticsStoreFactory> {
        StatisticsStoreFactory(
            storeFactory = get(),
            getWeightChartDataUseCase = get(),
            getFriendWeightChartDataUseCase = get(),
            updateWeightUseCase = get(),
            getExerciseChartDataUseCase = get(),
            getFriendExerciseDataFromFirebaseUseCase = get(),
            getMuscleGroupChartDataUseCase = get(),
            getFriendMuscleGroupChartDataUseCase = get(),
            getFriendMuscleGroupDataFromFirebaseUseCase = get(),
            getFriendsUseCase = get(),
            getCurrentUserProfileUseCase = get(),
            savedFriendsManager = get()
        )
    }

    // Store
    single<StatisticsStore> {
        get<StatisticsStoreFactory>().create()
    }
}