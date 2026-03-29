package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.data.workout.repositories.ExerciseRepositoryImpl
import com.example.reptrack.data.workout.mock.FakeWorkoutExerciseRepository
import com.example.reptrack.data.workout.mock.FakeWorkoutSessionRepository
import com.example.reptrack.data.workout.mock.FakeWorkoutTemplateRepository
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import com.example.reptrack.domain.workout.repositories.WorkoutExerciseRepository
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.domain.workout.usecases.exercises.*
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
            exerciseDao = get()
        )
    }

    single<FakeWorkoutExerciseRepository> {
        FakeWorkoutExerciseRepository()
    }

    single<WorkoutExerciseRepository> {
        get<FakeWorkoutExerciseRepository>()
    }

    single<WorkoutSessionRepository> {
        FakeWorkoutSessionRepository(get())
    }

    single<WorkoutTemplateRepository> {
        FakeWorkoutTemplateRepository()
    }

    factory { ObserveAllExercisesUseCase(get()) }
    factory { ObserveExerciseByIdUseCase(get()) }
    factory { CreateExerciseUseCase(get()) }
    factory { UpdateExerciseUseCase(get()) }
    factory { DeleteExerciseUseCase(get()) }

    factory { ObserveWorkoutExerciseByIdUseCase(get()) }
    factory { CreateWorkoutExerciseUseCase(get()) }
    factory { UpdateWorkoutExerciseUseCase(get()) }
    factory { DeleteWorkoutExerciseUseCase(get()) }

    factory { ObserveWorkoutExercisesBySessionUseCase(get()) }
    factory { ObserveBestSetFromLastWorkoutUseCase(get()) }
    factory { ObserveLastCompletedWorkoutExerciseUseCase(get()) }

    factory { CalendarUseCase(get(), get()) }

    // Template use cases
    factory { ObserveAllWorkoutTemplatesUseCase(get()) }
    factory { ObserveWorkoutTemplateByIdUseCase(get()) }
    factory { CreateWorkoutTemplateUseCase(get()) }
    factory { UpdateWorkoutTemplateUseCase(get()) }
    factory { DeleteWorkoutTemplateUseCase(get()) }

    factory<MainScreenStore> {
        MainScreenStoreFactory(
            storeFactory = get<StoreFactory>(),
            calendarUseCase = get(),
            observeExerciseByIdUseCase = get(),
            observeBestSetFromLastWorkoutUseCase = get()
        ).create()
    }

    factory {
        WorkoutExerciseDetailStoreFactory(
            storeFactory = get(),
            observeWorkoutExerciseByIdUseCase = get(),
            observeExerciseByIdUseCase = get(),
            updateWorkoutExerciseUseCase = get()
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
            observeAllExercisesUseCase = get()
        )
    }
}
