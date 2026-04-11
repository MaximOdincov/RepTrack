package com.example.reptrack.presentation.template.list.stores

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.domain.workout.usecases.templates.DeleteWorkoutTemplateUseCase
import com.example.reptrack.domain.workout.usecases.templates.ObserveAllWorkoutTemplatesUseCase

/**
 * Factory for creating TemplateListStore instances
 */
class TemplateListStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeAllTemplatesUseCase: ObserveAllWorkoutTemplatesUseCase,
    private val deleteTemplateUseCase: DeleteWorkoutTemplateUseCase
) {
    fun create(): TemplateListStore = TemplateListStoreFactoryImpl(
        storeFactory,
        observeAllTemplatesUseCase,
        deleteTemplateUseCase
    ).create()
}
