package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.domain.workout.usecases.exercises.ObserveAllExercisesUseCase
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStoreFactory
import org.koin.dsl.module

/**
 * DI module for Exercise feature
 */
val exerciseModule = module {
    // Exercise List Store Factory
    factory {
        ExerciseListStoreFactory(
            storeFactory = get<StoreFactory>(),
            observeAllExercisesUseCase = get<ObserveAllExercisesUseCase>()
        )
    }

    // Exercise List Store
    factory<ExerciseListStore> {
        get<ExerciseListStoreFactory>().create()
    }

    // Exercise Detail Store
    // TODO: Implement factory when ExerciseDetailStore is ready
    // factory<ExerciseDetailStore> {
    //     ExerciseDetailStoreFactory(
    //         storeFactory = get<StoreFactory>(),
    //         observeExerciseByIdUseCase = get(),
    //         updateExerciseUseCase = get(),
    //         observeLastExerciseProgressUseCase = get()
    //     ).create()
    // }
}
