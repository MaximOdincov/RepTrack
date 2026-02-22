package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.data.workout.mock.FakeExerciseRepository
import com.example.reptrack.data.workout.mock.FakeWorkoutSessionRepository
import com.example.reptrack.data.workout.mock.FakeWorkoutTemplateRepository
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.domain.workout.usecases.exercises.*
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.main.stores.MainScreenStoreFactory
import org.koin.dsl.module

val workoutModule = module {
    // Repository implementations (using fake/mock data for testing)
    single<ExerciseRepository> {
        FakeExerciseRepository()
    }

    single<WorkoutSessionRepository> {
        FakeWorkoutSessionRepository()
    }

    single<WorkoutTemplateRepository> {
        FakeWorkoutTemplateRepository()
    }

    // Exercise Use Cases
    factory { ObserveAllExercisesUseCase(get()) }
    factory { ObserveExerciseByIdUseCase(get()) }
    factory { CreateExerciseUseCase(get()) }
    factory { UpdateExerciseUseCase(get()) }
    factory { DeleteExerciseUseCase(get()) }
    factory { ObserveWorkoutExerciseByIdUseCase(get()) }
    factory { CreateWorkoutExerciseUseCase(get()) }
    factory { UpdateWorkoutExerciseUseCase(get()) }
    factory { DeleteWorkoutExerciseUseCase(get()) }
    factory { ObserveLastExerciseProgressUseCase(get()) }

    // Other Use cases
    factory { CalendarUseCase(get(), get()) }

    // Store
    factory<MainScreenStore> {
        MainScreenStoreFactory(
            storeFactory = get<StoreFactory>()
        ).create()
    }
}
