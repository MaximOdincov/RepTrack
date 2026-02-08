package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.data.workout.mock.FakeTrainingSessionRepository
import com.example.reptrack.data.workout.mock.FakeTrainingTemplateRepository
import com.example.reptrack.domain.workout.repositories.TrainingSessionRepository
import com.example.reptrack.domain.workout.repositories.TrainingTemplateRepository
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.main.stores.MainScreenStoreFactory
import org.koin.dsl.module

val workoutModule = module {
    // Repository implementations (using fake/mock data for testing)
    single<TrainingSessionRepository> {
        FakeTrainingSessionRepository()
    }

    single<TrainingTemplateRepository> {
        FakeTrainingTemplateRepository()
    }

    // Use cases
    factory { CalendarUseCase(get(), get()) }

    // Store
    factory<MainScreenStore> {
        MainScreenStoreFactory(
            storeFactory = get<StoreFactory>(),
            calendarUseCase = get()
        ).create()
    }
}
