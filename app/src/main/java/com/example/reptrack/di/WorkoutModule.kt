package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.data.workout.repositories.ExerciseRepositoryImpl
import com.example.reptrack.data.workout.repositories.RoomBasedWorkoutTemplateRepository
import com.example.reptrack.data.workout.repositories.WorkoutExerciseRepositoryImpl
import com.example.reptrack.data.workout.mock.FakeWorkoutExerciseRepository
import com.example.reptrack.data.workout.mock.FakeWorkoutSessionRepository
import com.example.reptrack.data.workout.repositories.WorkoutSessionRepositoryImpl
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.domain.workout.usecases.exercises.*
import com.example.reptrack.domain.workout.usecases.sessions.CreateWorkoutSessionFromTemplateUseCase
import com.example.reptrack.domain.workout.usecases.sessions.ShouldUpdateSessionFromTemplateUseCase
import com.example.reptrack.domain.workout.usecases.sessions.UpdateSessionStatusOnFirstSetUseCase
import com.example.reptrack.domain.workout.usecases.templates.*
import com.example.reptrack.domain.workout.usecases.workout_exercises.CreateWorkoutExerciseUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.DeleteWorkoutExerciseUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveBestSetFromLastWorkoutUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveLastCompletedWorkoutExerciseUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveWorkoutExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveWorkoutExercisesBySessionUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.UpdateWorkoutExerciseUseCase
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.main.stores.MainScreenStoreFactory
import com.example.reptrack.presentation.template.detail.stores.TemplateDetailStoreFactory
import com.example.reptrack.presentation.template.list.stores.TemplateListStoreFactory
import com.example.reptrack.presentation.workout_exercise.detail.stores.WorkoutExerciseDetailStoreFactory
import org.koin.dsl.module

val workoutModule = module {

    single<ExerciseRepository> {
        ExerciseRepositoryImpl(
            exerciseDao = get(),
            errorHandler = get()
        )
    }

    single<FakeWorkoutExerciseRepository> {
        FakeWorkoutExerciseRepository()
    }

    single<WorkoutExerciseRepository> {
        WorkoutExerciseRepositoryImpl(
            workoutDao = get(),
            exerciseDao = get(),
            errorHandler = get()
        )
    }

    single<WorkoutSessionRepository> {
        WorkoutSessionRepositoryImpl(
            workoutDao = get(),
            authRepository = get(),
            errorHandler = get()
        )
    }

    single<WorkoutTemplateRepository> {
        RoomBasedWorkoutTemplateRepository(
            database = get(),
            errorHandler = get()
        )
    }

    factory { ObserveAllExercisesUseCase(get()) }
    factory { ObserveExerciseByIdUseCase(get(), get()) }
    factory { CreateExerciseUseCase(get(), get()) }
    factory { UpdateExerciseUseCase(get(), get()) }
    factory { DeleteExerciseUseCase(get(), get()) }

    factory { ObserveWorkoutExerciseByIdUseCase(get(), get()) }
    factory { CreateWorkoutExerciseUseCase(get(), get()) }
    factory { UpdateWorkoutExerciseUseCase(get(), get()) }
    factory { DeleteWorkoutExerciseUseCase(get(), get()) }

    factory { ObserveWorkoutExercisesBySessionUseCase(get(), get()) }
    factory { ObserveBestSetFromLastWorkoutUseCase(get(), get()) }
    factory { ObserveLastCompletedWorkoutExerciseUseCase(get(), get()) }

    factory { CalendarUseCase(get(), get()) }

    // Session use cases
    factory { CreateWorkoutSessionFromTemplateUseCase(get(), get()) }
    factory { ShouldUpdateSessionFromTemplateUseCase() }
    factory { UpdateSessionStatusOnFirstSetUseCase(get()) }

    // Template use cases
    factory { ObserveAllWorkoutTemplatesUseCase(get(), get()) }
    factory { ObserveWorkoutTemplateByIdUseCase(get(), get()) }
    factory { CreateWorkoutTemplateUseCase(get(), get()) }
    factory { UpdateWorkoutTemplateUseCase(get(), get()) }
    factory { DeleteWorkoutTemplateUseCase(get(), get()) }
    factory { ObserveWorkoutCalendarUseCase(get(), get()) }

    factory<MainScreenStore> {
        MainScreenStoreFactory(
            storeFactory = get<StoreFactory>(),
            calendarUseCase = get(),
            observeExerciseByIdUseCase = get(),
            observeBestSetFromLastWorkoutUseCase = get(),
            createSessionFromTemplateUseCase = get(),
            shouldUpdateSessionFromTemplateUseCase = get(),
            authRepository = get()
        ).create()
    }

    factory {
        WorkoutExerciseDetailStoreFactory(
            storeFactory = get(),
            observeWorkoutExerciseByIdUseCase = get(),
            observeExerciseByIdUseCase = get(),
            updateWorkoutExerciseUseCase = get(),
            updateSessionStatusOnFirstSetUseCase = get()
        )
    }

    // Template stores
    factory {
        TemplateListStoreFactory(
            storeFactory = get(),
            observeAllTemplatesUseCase = get(),
            deleteTemplateUseCase = get()
        )
    }

    factory {
        TemplateDetailStoreFactory(
            storeFactory = get(),
            createTemplateUseCase = get(),
            updateTemplateUseCase = get(),
            observeTemplateByIdUseCase = get(),
            observeAllExercisesUseCase = get(),
            errorHandler = get(),
            deleteTemplateUseCase = get()
        )
    }
}
