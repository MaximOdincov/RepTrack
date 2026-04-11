package com.example.reptrack.presentation.workout_exercise.detail.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.usecases.exercises.ObserveExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveWorkoutExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.UpdateWorkoutExerciseUseCase
import com.example.reptrack.domain.workout.usecases.sessions.UpdateSessionStatusOnFirstSetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.UUID

internal class WorkoutExerciseDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeWorkoutExerciseByIdUseCase: ObserveWorkoutExerciseByIdUseCase,
    private val observeExerciseByIdUseCase: ObserveExerciseByIdUseCase,
    private val updateWorkoutExerciseUseCase: UpdateWorkoutExerciseUseCase,
    private val updateSessionStatusOnFirstSetUseCase: UpdateSessionStatusOnFirstSetUseCase
) {

    fun create(): WorkoutExerciseDetailStore =
        object : WorkoutExerciseDetailStore, Store<WorkoutExerciseDetailStore.Intent, WorkoutExerciseDetailStore.State, WorkoutExerciseDetailStore.Label> by storeFactory.create(
            name = "WorkoutExerciseDetailStore",
            initialState = WorkoutExerciseDetailStore.State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data class Initialized(val workoutExerciseId: String) : Msg
        data class WorkoutExerciseLoaded(val workoutExercise: WorkoutExercise) : Msg
        data class ExerciseLoaded(val exercise: Exercise) : Msg
        data class SetUpdated(val setId: String, val set: WorkoutSet) : Msg
        data class SetsListChanged(val sets: List<WorkoutSet>) : Msg
        data class LoadingChanged(val isLoading: Boolean) : Msg
        data class SavingChanged(val isSaving: Boolean) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<WorkoutExerciseDetailStore.Intent, Nothing, WorkoutExerciseDetailStore.State, Msg, WorkoutExerciseDetailStore.Label>() {

        // Local cache for workout exercise to avoid unnecessary updates
        private var cachedWorkoutExercise: WorkoutExercise? = null
        // Separate cache for sets that's updated during runtime
        private var cachedSets: List<WorkoutSet> = emptyList()

        override fun executeIntent(intent: WorkoutExerciseDetailStore.Intent, getState: () -> WorkoutExerciseDetailStore.State) {
            when (intent) {
                is WorkoutExerciseDetailStore.Intent.Initialize -> {
                    dispatch(Msg.Initialized(intent.workoutExerciseId))
                    loadWorkoutExercise(intent.workoutExerciseId)
                }
                is WorkoutExerciseDetailStore.Intent.WeightChanged -> {
                    updateSetWeight(intent.setId, intent.weight)
                }
                is WorkoutExerciseDetailStore.Intent.RepsChanged -> {
                    updateSetReps(intent.setId, intent.reps)
                }
                is WorkoutExerciseDetailStore.Intent.AddSet -> {
                    addSet(intent.weight, intent.reps)
                }
                is WorkoutExerciseDetailStore.Intent.RemoveSet -> {
                    removeSet(intent.setId)
                }
                is WorkoutExerciseDetailStore.Intent.SaveAndExit -> {
                    saveAndExit(getState())
                }
            }
        }

        private fun loadWorkoutExercise(workoutExerciseId: String) {
            dispatch(Msg.LoadingChanged(true))
            scope.launch {
                observeWorkoutExerciseByIdUseCase(workoutExerciseId).flowOn(Dispatchers.IO).collect { workoutExercise ->
                    if (cachedWorkoutExercise != workoutExercise) {
                        val isFirstLoad = cachedWorkoutExercise == null
                        cachedWorkoutExercise = workoutExercise
                        cachedSets = workoutExercise.sets
                        // Only load workoutExercise once, then track sets separately
                        if (isFirstLoad) {
                            dispatch(Msg.WorkoutExerciseLoaded(workoutExercise))
                        }
                        // Always dispatch sets updates
                        dispatch(Msg.SetsListChanged(workoutExercise.sets))
                    }
                    dispatch(Msg.LoadingChanged(false))

                    loadExercise(workoutExercise.exerciseId)
                }
            }
        }

        private fun loadExercise(exerciseId: String) {
            scope.launch {
                observeExerciseByIdUseCase(exerciseId).flowOn(Dispatchers.IO).collect { exercise ->
                    dispatch(Msg.ExerciseLoaded(exercise))
                }
            }
        }

        private fun updateSetWeight(setId: String, weight: Float) {
            val targetSet = cachedSets.find { it.id == setId } ?: return

            if (targetSet.weight == weight || (targetSet.weight == null && weight <= 0)) {
                return
            }

            val updatedSet = targetSet.copy(weight = if (weight > 0) weight else null)
            cachedSets = cachedSets.map { if (it.id == setId) updatedSet else it }

            dispatch(Msg.SetUpdated(setId, updatedSet))
        }

        private fun updateSetReps(setId: String, reps: Int) {
            val targetSet = cachedSets.find { it.id == setId } ?: return

            if (targetSet.reps == reps || (targetSet.reps == null && reps <= 0)) {
                return
            }

            val updatedSet = targetSet.copy(reps = if (reps > 0) reps else null)
            cachedSets = cachedSets.map { if (it.id == setId) updatedSet else it }

            dispatch(Msg.SetUpdated(setId, updatedSet))
        }

        private fun addSet(weight: Float?, reps: Int?) {
            val newIndex = cachedSets.maxOfOrNull { it.index }?.plus(1) ?: 1
            val newSet = WorkoutSet(
                id = UUID.randomUUID().toString(),
                index = newIndex,
                weight = weight,
                reps = reps,
                isCompleted = true
            )
            cachedSets = cachedSets + newSet

            dispatch(Msg.SetsListChanged(cachedSets))
        }

        private fun removeSet(setId: String) {
            val updatedSets = cachedSets.filterNot { it.id == setId }
            val reindexedSets = updatedSets.mapIndexed { index, set ->
                set.copy(index = index + 1)
            }
            cachedSets = reindexedSets

            dispatch(Msg.SetsListChanged(reindexedSets))
        }

        private fun saveAndExit(state: WorkoutExerciseDetailStore.State) {
            val workoutExercise = state.workoutExercise ?: return

            // Merge cached sets into workoutExercise before saving
            val workoutExerciseToSave = workoutExercise.copy(sets = cachedSets)

            // Only save if at least one set has weight or reps
            val hasCompletedSets = cachedSets.any { it.weight != null && it.weight!! > 0 || it.reps != null && it.reps!! > 0 }

            if (hasCompletedSets) {
                dispatch(Msg.SavingChanged(true))
                scope.launch {
                    // First, update session status if this is the first completed set
                    val previousSets = workoutExercise.sets
                    val hadCompletedSetsBefore = previousSets.any { it.isCompleted }

                    if (!hadCompletedSetsBefore) {
                        // This is the first completed set - update session status to IN_PROGRESS
                        updateSessionStatusOnFirstSetUseCase(workoutExercise.workoutSessionId)
                            .onFailure { e ->
                                android.util.Log.e("WorkoutExerciseDetailStore", "Failed to update session status: ${e.message}")
                            }
                    }

                    val result = updateWorkoutExerciseUseCase(workoutExerciseToSave)
                    dispatch(Msg.SavingChanged(false))
                    result.onSuccess {
                        publish(WorkoutExerciseDetailStore.Label.NavigateBack)
                    }.onFailure { error ->
                        publish(WorkoutExerciseDetailStore.Label.ShowError(error.message ?: "Failed to save"))
                    }
                }
            } else {
                // No data to save, just navigate back
                publish(WorkoutExerciseDetailStore.Label.NavigateBack)
            }
        }
    }

    private object ReducerImpl : Reducer<WorkoutExerciseDetailStore.State, Msg> {
        override fun WorkoutExerciseDetailStore.State.reduce(message: Msg): WorkoutExerciseDetailStore.State {
            return when (message) {
                is Msg.Initialized -> copy(workoutExerciseId = message.workoutExerciseId)
                is Msg.WorkoutExerciseLoaded -> {
                    copy(workoutExercise = message.workoutExercise, sets = message.workoutExercise.sets)
                }
                is Msg.ExerciseLoaded -> copy(exercise = message.exercise)
                is Msg.SetUpdated -> {
                    copy(
                        sets = sets.map { set ->
                            if (set.id == message.setId) message.set else set
                        }
                    )
                }
                is Msg.SetsListChanged -> {
                    copy(sets = message.sets)
                }
                is Msg.LoadingChanged -> copy(isLoading = message.isLoading)
                is Msg.SavingChanged -> copy(isSaving = message.isSaving)
            }
        }
    }
}
