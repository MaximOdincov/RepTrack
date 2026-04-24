package com.example.reptrack.presentation.template.list.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.usecases.templates.DeleteWorkoutTemplateUseCase
import com.example.reptrack.domain.workout.usecases.templates.ObserveAllWorkoutTemplatesUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

interface TemplateListStore : Store<TemplateListStore.Intent, TemplateListStore.State, TemplateListStore.Label> {

    sealed interface Intent {
        data class Initialize(val mode: TemplateListMode) : Intent
        data class TemplateClicked(val template: WorkoutTemplate) : Intent
        data class SearchChanged(val query: String) : Intent
        object AddTemplateClicked : Intent
        data class DeleteTemplate(val templateId: String) : Intent
    }

    enum class TemplateListMode {
        VIEW_MODE,
        SELECT_MODE
    }

    data class State(
        val mode: TemplateListMode = TemplateListMode.VIEW_MODE,
        val isLoading: Boolean = false,
        val templates: List<WorkoutTemplate> = emptyList(),
        val searchQuery: String = "",
        val filteredTemplates: List<WorkoutTemplate> = emptyList()
    ) {
        val isSelectMode: Boolean get() = mode == TemplateListMode.SELECT_MODE
    }

    sealed interface Label {
        data class NavigateToDetail(val templateId: String) : Label
        data class SelectTemplateAndBack(val template: WorkoutTemplate) : Label
        object NavigateToAddTemplate : Label
    }
}

internal class TemplateListStoreFactoryImpl(
    private val storeFactory: StoreFactory,
    private val observeAllTemplatesUseCase: ObserveAllWorkoutTemplatesUseCase,
    private val deleteTemplateUseCase: DeleteWorkoutTemplateUseCase
) {

    fun create(): TemplateListStore =
        object : TemplateListStore, Store<TemplateListStore.Intent, TemplateListStore.State, TemplateListStore.Label> by storeFactory.create(
            name = "TemplateListStore",
            initialState = TemplateListStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class LoadTemplates(val mode: TemplateListStore.TemplateListMode) : Action
    }

    private sealed interface Msg {
        data class ModeInitialized(val mode: TemplateListStore.TemplateListMode) : Msg
        object LoadingStarted : Msg
        data class TemplatesLoaded(val templates: List<WorkoutTemplate>) : Msg
        data class SearchQueryChanged(val query: String) : Msg
        data class FilteredTemplatesUpdated(val templates: List<WorkoutTemplate>) : Msg
        data class TemplateDeleted(val templateId: String) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadTemplates(TemplateListStore.TemplateListMode.VIEW_MODE))
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<TemplateListStore.Intent, Action, TemplateListStore.State, Msg, TemplateListStore.Label>() {
        override fun executeIntent(intent: TemplateListStore.Intent, getState: () -> TemplateListStore.State) {
            when (intent) {
                is TemplateListStore.Intent.Initialize -> {
                    dispatch(Msg.ModeInitialized(intent.mode))
                    loadTemplates()
                }
                is TemplateListStore.Intent.TemplateClicked -> handleTemplateClick(intent.template, getState)
                is TemplateListStore.Intent.SearchChanged -> handleSearchChanged(intent.query, getState)
                TemplateListStore.Intent.AddTemplateClicked -> publish(TemplateListStore.Label.NavigateToAddTemplate)
                is TemplateListStore.Intent.DeleteTemplate -> handleDeleteTemplate(intent.templateId)
            }
        }

        override fun executeAction(action: Action, getState: () -> TemplateListStore.State) {
            when (action) {
                is Action.LoadTemplates -> {
                    dispatch(Msg.ModeInitialized(action.mode))
                    loadTemplates()
                }
            }
        }

        private fun loadTemplates() {
            dispatch(Msg.LoadingStarted)
            scope.launch {
                observeAllTemplatesUseCase()
                    .catch { /* TODO: Handle error */ }
                    .collect { templates ->
                        dispatch(Msg.TemplatesLoaded(templates))
                        dispatch(Msg.FilteredTemplatesUpdated(filterTemplates(templates, "")))
                    }
            }
        }

        private fun handleTemplateClick(template: WorkoutTemplate, getState: () -> TemplateListStore.State) {
            val state = getState()
            when (state.mode) {
                TemplateListStore.TemplateListMode.VIEW_MODE -> {
                    publish(TemplateListStore.Label.NavigateToDetail(template.id))
                }
                TemplateListStore.TemplateListMode.SELECT_MODE -> {
                    publish(TemplateListStore.Label.SelectTemplateAndBack(template))
                }
            }
        }

        private fun handleSearchChanged(query: String, getState: () -> TemplateListStore.State) {
            dispatch(Msg.SearchQueryChanged(query))
            val filtered = filterTemplates(getState().templates, query)
            dispatch(Msg.FilteredTemplatesUpdated(filtered))
        }

        private fun handleDeleteTemplate(templateId: String) {
            scope.launch {
                val result = deleteTemplateUseCase(templateId)
                if (result.isSuccess) {
                    dispatch(Msg.TemplateDeleted(templateId))
                }
                // TODO: Handle error case
            }
        }

        private fun filterTemplates(
            templates: List<WorkoutTemplate>,
            query: String
        ): List<WorkoutTemplate> {
            if (query.isBlank()) return templates

            val lowerQuery = query.lowercase()

            return templates.filter { template ->
                template.name.lowercase().contains(lowerQuery) ||
                template.description.lowercase().contains(lowerQuery) ||
                template.muscleGroups.any { it.name.lowercase().contains(lowerQuery) }
            }
        }
    }

    private object ReducerImpl : Reducer<TemplateListStore.State, Msg> {
        override fun TemplateListStore.State.reduce(msg: Msg): TemplateListStore.State =
            when (msg) {
                is Msg.ModeInitialized -> copy(mode = msg.mode)
                is Msg.LoadingStarted -> copy(isLoading = true)
                is Msg.TemplatesLoaded -> copy(
                    isLoading = false,
                    templates = msg.templates
                )
                is Msg.SearchQueryChanged -> copy(searchQuery = msg.query)
                is Msg.FilteredTemplatesUpdated -> copy(filteredTemplates = msg.templates)
                is Msg.TemplateDeleted -> copy(
                    templates = templates.filter { it.id != msg.templateId },
                    filteredTemplates = filteredTemplates.filter { it.id != msg.templateId }
                )
            }
    }
}
