package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.domain.workout.usecases.exercises.CreateExerciseUseCase
import com.example.reptrack.domain.workout.usecases.exercises.DeleteExerciseUseCase
import com.example.reptrack.domain.workout.usecases.exercises.ObserveAllExercisesUseCase
import com.example.reptrack.domain.workout.usecases.exercises.ObserveExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.exercises.UpdateExerciseUseCase
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStoreFactory
import com.example.reptrack.presentation.exercise.detail.stores.ExerciseDetailStore
import com.example.reptrack.presentation.exercise.detail.stores.ExerciseDetailStoreFactory
import org.koin.dsl.module

val exerciseModule = module {

    factory {
        ExerciseListStoreFactory(
            storeFactory = get<StoreFactory>(),
            observeAllExercisesUseCase = get<ObserveAllExercisesUseCase>(),
            deleteExerciseUseCase = get<DeleteExerciseUseCase>()
        )
    }

    single<ExerciseListStore> {
        get<ExerciseListStoreFactory>().create()
    }

    factory {
        ExerciseDetailStoreFactory(
            storeFactory = get(),
            observeExerciseByIdUseCase = get(),
            updateExerciseUseCase = get(),
            createExerciseUseCase = get()
        )
    }

    factory { params ->
        get<ExerciseDetailStoreFactory>().create(
            exerciseId = params.get(),
            mode = params.get()
        )
    }
}
