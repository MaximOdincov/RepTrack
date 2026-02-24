package com.example.reptrack.presentation.exercise.list.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.usecases.exercises.ObserveAllExercisesUseCase
import com.example.reptrack.navigation.ExerciseListMode
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore.Intent
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore.Label
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

interface ExerciseListStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class Initialize(val mode: ExerciseListMode) : Intent
        data class ExerciseClicked(val exercise: Exercise) : Intent
        data class SearchChanged(val query: String) : Intent
        object AddExerciseClicked : Intent
    }

    data class State(
        val mode: ExerciseListMode = ExerciseListMode.VIEW_MODE,
        val isLoading: Boolean = false,
        val exercisesByGroup: Map<MuscleGroup, List<Exercise>> = emptyMap(),
        val searchQuery: String = "",
        val filteredExercises: Map<MuscleGroup, List<Exercise>> = emptyMap()
    ) {
        val isWorkoutMode: Boolean get() = mode == ExerciseListMode.WORKOUT_MODE
    }

    sealed interface Label {
        data class NavigateToDetail(val exerciseId: String) : Label
        data class AddToWorkoutAndBack(val exercise: Exercise) : Label
        object NavigateToAddExercise : Label
    }
}

internal class ExerciseListStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeAllExercisesUseCase: ObserveAllExercisesUseCase
) {

    fun create(): ExerciseListStore =
        object : ExerciseListStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ExerciseListStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class LoadExercises(val mode: ExerciseListMode) : Action
    }

    private sealed interface Msg {
        data class ModeInitialized(val mode: ExerciseListMode) : Msg
        object LoadingStarted : Msg
        data class ExercisesLoaded(val exercises: Map<MuscleGroup, List<Exercise>>) : Msg
        data class SearchQueryChanged(val query: String) : Msg
        data class FilteredExercisesUpdated(val exercises: Map<MuscleGroup, List<Exercise>>) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadExercises(ExerciseListMode.VIEW_MODE))
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.Initialize -> {
                    dispatch(Msg.ModeInitialized(intent.mode))
                    loadExercises()
                }
                is Intent.ExerciseClicked -> handleExerciseClick(intent.exercise, getState)
                is Intent.SearchChanged -> handleSearchChanged(intent.query, getState)
                Intent.AddExerciseClicked -> publish(Label.NavigateToAddExercise)
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.LoadExercises -> {
                    dispatch(Msg.ModeInitialized(action.mode))
                    loadExercises()
                }
            }
        }

        private fun loadExercises() {
            dispatch(Msg.LoadingStarted)
            scope.launch {
                observeAllExercisesUseCase()
                    .catch { /* TODO: Handle error */ }
                    .collect { exercises ->
                        dispatch(Msg.ExercisesLoaded(exercises))
                        dispatch(Msg.FilteredExercisesUpdated(filterExercises(exercises, "")))
                    }
            }
        }

        private fun handleExerciseClick(exercise: Exercise, getState: () -> State) {
            val state = getState()
            when (state.mode) {
                ExerciseListMode.VIEW_MODE -> {
                    publish(Label.NavigateToDetail(exercise.id))
                }
                ExerciseListMode.WORKOUT_MODE -> {
                    publish(Label.AddToWorkoutAndBack(exercise))
                }
            }
        }

        private fun handleSearchChanged(query: String, getState: () -> State) {
            dispatch(Msg.SearchQueryChanged(query))
            val filtered = filterExercises(getState().exercisesByGroup, query)
            dispatch(Msg.FilteredExercisesUpdated(filtered))
        }

        private fun filterExercises(
            exercises: Map<MuscleGroup, List<Exercise>>,
            query: String
        ): Map<MuscleGroup, List<Exercise>> {
            if (query.isBlank()) return exercises

            val lowerQuery = query.lowercase()

            return exercises.mapValues { (group, exerciseList) ->
                exerciseList.filter { exercise ->
                    exercise.name.lowercase().contains(lowerQuery) ||
                    group.name.lowercase().contains(lowerQuery)
                }
            }.filterValues { it.isNotEmpty() }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.ModeInitialized -> copy(mode = msg.mode)
                is Msg.LoadingStarted -> copy(isLoading = true)
                is Msg.ExercisesLoaded -> copy(
                    isLoading = false,
                    exercisesByGroup = msg.exercises
                )
                is Msg.SearchQueryChanged -> copy(searchQuery = msg.query)
                is Msg.FilteredExercisesUpdated -> copy(filteredExercises = msg.exercises)
            }
    }
}
