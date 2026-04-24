package com.example.reptrack.presentation.template.detail.stores

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.domain.workout.usecases.exercises.ObserveAllExercisesUseCase
import com.example.reptrack.domain.workout.usecases.templates.CreateWorkoutTemplateUseCase
import com.example.reptrack.domain.workout.usecases.templates.DeleteWorkoutTemplateUseCase
import com.example.reptrack.domain.workout.usecases.templates.ObserveWorkoutTemplateByIdUseCase
import com.example.reptrack.domain.workout.usecases.templates.UpdateWorkoutTemplateUseCase

/**
 * Factory for creating TemplateDetailStore instances
 */
class TemplateDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val createTemplateUseCase: CreateWorkoutTemplateUseCase,
    private val updateTemplateUseCase: UpdateWorkoutTemplateUseCase,
    private val deleteTemplateUseCase: DeleteWorkoutTemplateUseCase,
    private val observeTemplateByIdUseCase: ObserveWorkoutTemplateByIdUseCase,
    private val observeAllExercisesUseCase: ObserveAllExercisesUseCase,
    private val errorHandler: ErrorHandler
) {
    fun create(): TemplateDetailStore = TemplateDetailStoreFactoryImpl(
        storeFactory,
        createTemplateUseCase,
        updateTemplateUseCase,
        deleteTemplateUseCase,
        observeTemplateByIdUseCase,
        observeAllExercisesUseCase,
        errorHandler
    ).create()
}
