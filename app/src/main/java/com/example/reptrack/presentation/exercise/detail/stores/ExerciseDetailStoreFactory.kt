package com.example.reptrack.presentation.exercise.detail.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.usecases.exercises.CreateExerciseUseCase
import com.example.reptrack.domain.workout.usecases.exercises.ObserveExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.exercises.UpdateExerciseUseCase
import com.example.reptrack.navigation.ExerciseDetailMode
import com.example.reptrack.presentation.exercise.detail.stores.ExerciseDetailStore.Intent
import com.example.reptrack.presentation.exercise.detail.stores.ExerciseDetailStore.Label
import com.example.reptrack.presentation.exercise.detail.stores.ExerciseDetailStore.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import android.util.Log
import java.util.UUID

/**
 * Factory for creating ExerciseDetailStore instances
 *
 * The store observes the repository via use case and automatically updates
 * when data changes. No manual caching needed - repository handles that.
 *
 * For new exercises (exerciseId == "new"), the store creates a new exercise
 * instead of loading an existing one.
 */
class ExerciseDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeExerciseByIdUseCase: ObserveExerciseByIdUseCase,
    private val updateExerciseUseCase: UpdateExerciseUseCase,
    private val createExerciseUseCase: CreateExerciseUseCase
) {

    companion object {
        const val NEW_EXERCISE_ID = "new"
    }

    private val TAG = "ExerciseDetailStore"

    fun create(
        exerciseId: String,
        mode: ExerciseDetailMode
    ): ExerciseDetailStore {
        val isNewExercise = exerciseId == NEW_EXERCISE_ID
        Log.d(TAG, "✅ CREATE STORE: exerciseId=$exerciseId, isNew=$isNewExercise, mode=$mode, thread=${Thread.currentThread().name}")

        return object : ExerciseDetailStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ExerciseDetailStore",
            initialState = State(
                exerciseId = exerciseId,
                isLoading = !isNewExercise // Only show loading for existing exercises
            ),
            executorFactory = { ExecutorImpl(exerciseId, mode, isNewExercise) },
            reducer = ReducerImpl
        ) {}
    }

    private sealed interface Msg {
        data class LoadingStarted(val isLoading: Boolean) : Msg
        data class ExerciseLoaded(val exercise: Exercise) : Msg
        data class NameChanged(val name: String) : Msg
        data class MuscleGroupChanged(val group: MuscleGroup) : Msg
        data class CustomizationSheetVisibilityChanged(val isVisible: Boolean) : Msg
        data class SheetModeChanged(val mode: CustomizationSheetMode) : Msg
        data class DraftIconChanged(val iconRes: Int) : Msg
        data class DraftColorChanged(val color: String) : Msg
        data class IconSaved(val iconRes: Int) : Msg
        data class ColorSaved(val color: String) : Msg
        data class SavingChanged(val isSaving: Boolean) : Msg
    }

    private inner class ExecutorImpl(
        private val exerciseId: String,
        private val mode: ExerciseDetailMode,
        private val isNewExercise: Boolean
    ) : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) {
            Log.d(TAG, "🎯 INTENT: $intent, thread=${Thread.currentThread().name}")

            when (intent) {
                is Intent.Initialize -> {
                    Log.d(TAG, "🚀 INITIALIZING: exerciseId=${intent.exerciseId}, isNew=$isNewExercise")
                    if (isNewExercise) {
                        // For new exercises, initialize with default values
                        dispatch(Msg.ExerciseLoaded(createDefaultExercise()))
                    } else {
                        loadExercise(intent.exerciseId)
                    }
                }
                is Intent.NameChanged -> dispatch(Msg.NameChanged(intent.name))
                is Intent.MuscleGroupChanged -> dispatch(Msg.MuscleGroupChanged(intent.group))
                Intent.SaveAndExit -> saveAndExit(getState)
                Intent.OpenCustomizationSheet -> openCustomizationSheet(getState)
                Intent.CloseCustomizationSheet -> closeCustomizationSheet(getState)
                is Intent.IconSelected -> dispatch(Msg.DraftIconChanged(intent.iconRes))
                is Intent.ColorSelected -> dispatch(Msg.DraftColorChanged(intent.color))
                is Intent.SheetModeChanged -> dispatch(Msg.SheetModeChanged(intent.mode))
            }
        }

        private fun loadExercise(exerciseId: String) {
            Log.d(TAG, "📥 LOAD EXERCISE START: exerciseId=$exerciseId")

            scope.launch {
                try {
                    Log.d(TAG, "⏳ Waiting for first()...")
                    // Use first() to get only the first value and complete
                    // StateFlow emits immediately, so this will return the current value
                    val exercise = observeExerciseByIdUseCase(exerciseId)
                        .first()

                    Log.d(TAG, "✅ EXERCISE LOADED: ${exercise.name}")
                    dispatch(Msg.ExerciseLoaded(exercise))
                    dispatch(Msg.LoadingStarted(false))
                    Log.d(TAG, "✅ LOADING FINISHED")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ ERROR: ${e.message}", e)
                    dispatch(Msg.LoadingStarted(false))
                    publish(Label.ShowError("Failed to load exercise: ${e.message}"))
                }
            }
        }

        private fun createDefaultExercise(): Exercise {
            return Exercise(
                id = NEW_EXERCISE_ID,
                name = "",
                muscleGroup = MuscleGroup.CHEST,
                type = ExerciseType.WEIGHT_REPS,
                iconRes = null,
                iconColor = null,
                backgroundRes = null,
                backgroundColor = null,
                isCustom = true
            )
        }

        private fun saveAndExit(getState: () -> State) {
            val state = getState()
            Log.d(TAG, "💾 SAVING AND EXIT: ${state.name}, isNew=$isNewExercise")

            scope.launch {
                val exercise = Exercise(
                    id = if (isNewExercise) UUID.randomUUID().toString() else state.exerciseId,
                    name = state.name,
                    muscleGroup = state.muscleGroup,
                    type = ExerciseType.WEIGHT_REPS,
                    iconRes = state.iconRes,
                    iconColor = state.iconColor,
                    backgroundRes = null,
                    backgroundColor = null,
                    isCustom = true
                )

                val result = if (isNewExercise) {
                    createExerciseUseCase(exercise)
                } else {
                    updateExerciseUseCase(exercise)
                }

                result
                    .onSuccess {
                        Log.d(TAG, "✅ EXERCISE SAVED")
                        publish(Label.ShowSavedToast("Exercise saved"))
                        // Navigate back after successful save
                        publish(Label.NavigateBack)
                    }
                    .onFailure { e ->
                        Log.e(TAG, "❌ SAVE FAILED: ${e.message}", e)
                        publish(Label.ShowError("Failed to save: ${e.message}"))
                        // Still navigate back even on error
                        publish(Label.NavigateBack)
                    }
            }
        }

        private fun openCustomizationSheet(getState: () -> State) {
            val state = getState()
            // Initialize draft values with current values
            state.iconRes?.let { dispatch(Msg.DraftIconChanged(it)) }
            state.iconColor?.let { dispatch(Msg.DraftColorChanged(it)) }
            dispatch(Msg.CustomizationSheetVisibilityChanged(true))
        }

        private fun closeCustomizationSheet(getState: () -> State) {
            val state = getState()

            // Apply draft values without saving to database
            // They will be saved when user exits the screen
            state.draftIconRes?.let { dispatch(Msg.IconSaved(it)) }
            state.draftIconColor?.let { dispatch(Msg.ColorSaved(it)) }
            dispatch(Msg.CustomizationSheetVisibilityChanged(false))
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            val result = when (msg) {
                is Msg.LoadingStarted -> {
                    Log.d("ExerciseReducer", "🔄 LoadingStarted: isLoading=${msg.isLoading}")
                    copy(isLoading = msg.isLoading)
                }
                is Msg.ExerciseLoaded -> {
                    Log.d("ExerciseReducer", "✅ ExerciseLoaded: ${msg.exercise.name}")
                    copy(
                        name = msg.exercise.name,
                        muscleGroup = msg.exercise.muscleGroup,
                        iconRes = msg.exercise.iconRes,
                        iconColor = msg.exercise.iconColor
                    )
                }
                is Msg.NameChanged -> copy(name = msg.name)
                is Msg.MuscleGroupChanged -> copy(muscleGroup = msg.group)
                is Msg.CustomizationSheetVisibilityChanged -> copy(
                    isCustomizationSheetVisible = msg.isVisible,
                    draftIconRes = if (!msg.isVisible) null else draftIconRes,
                    draftIconColor = if (!msg.isVisible) null else draftIconColor
                )
                is Msg.SheetModeChanged -> copy(sheetMode = msg.mode)
                is Msg.DraftIconChanged -> copy(draftIconRes = msg.iconRes)
                is Msg.DraftColorChanged -> copy(draftIconColor = msg.color)
                is Msg.IconSaved -> copy(iconRes = msg.iconRes)
                is Msg.ColorSaved -> copy(iconColor = msg.color)
                is Msg.SavingChanged -> copy(isSaving = msg.isSaving)
            }
            return result
        }
    }
}
