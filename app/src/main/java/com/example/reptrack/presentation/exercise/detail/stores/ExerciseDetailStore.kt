package com.example.reptrack.presentation.exercise.detail.stores

import com.arkivanov.mvikotlin.core.store.Store
import com.example.reptrack.navigation.ExerciseDetailMode

/**
 * Store for Exercise Detail screen
 * TODO: Implement logic on next iteration
 */
interface ExerciseDetailStore : Store<ExerciseDetailStore.Intent, ExerciseDetailStore.State, ExerciseDetailStore.Label> {

    sealed interface Intent {
        // TODO: Add intents (LoadExercise, UpdateExercise, SaveExercise, AddToWorkout, etc.)
    }

    data class State(
        val exerciseId: String = "",
        val mode: ExerciseDetailMode = ExerciseDetailMode.DESIGN_MODE
        // TODO: Add state fields (exercise, isEditing, lastProgress, etc.)
    ) {
        companion object {
            val Default = State()
        }
    }

    sealed interface Label {
        // TODO: Add labels (NavigateBack, ShowError, etc.)
    }
}
