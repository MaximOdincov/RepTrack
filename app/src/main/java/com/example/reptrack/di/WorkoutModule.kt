package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.data.workout.mock.FakeWorkoutSessionRepository
import com.example.reptrack.data.workout.mock.FakeWorkoutTemplateRepository
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.main.stores.MainScreenStoreFactory
import org.koin.dsl.module

val workoutModule = module {
    // Repository implementations (using fake/mock data for testing)
    single<WorkoutSessionRepository> {
        FakeWorkoutSessionRepository()
    }

    single<WorkoutTemplateRepository> {
        FakeWorkoutTemplateRepository()
    }

    // Use cases
    factory { CalendarUseCase(get(), get()) }

    // Store
    factory<MainScreenStore> {
        MainScreenStoreFactory(
            storeFactory = get<StoreFactory>()
        ).create()
    }
}
