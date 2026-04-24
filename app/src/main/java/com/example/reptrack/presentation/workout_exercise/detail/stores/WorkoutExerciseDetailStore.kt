package com.example.reptrack.presentation.workout_exercise.detail.stores

import com.arkivanov.mvikotlin.core.store.Store
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet

interface WorkoutExerciseDetailStore : Store<WorkoutExerciseDetailStore.Intent, WorkoutExerciseDetailStore.State, WorkoutExerciseDetailStore.Label> {

    sealed interface Intent {
        data class Initialize(val workoutExerciseId: String) : Intent
        data class WeightChanged(val setId: String, val weight: Float) : Intent
        data class RepsChanged(val setId: String, val reps: Int) : Intent
        data class AddSet(val weight: Float? = null, val reps: Int? = null) : Intent
        data class RemoveSet(val setId: String) : Intent
        object SaveAndExit : Intent
    }

    data class State(
        val workoutExerciseId: String = "",
        val workoutExercise: WorkoutExercise? = null,
        val exercise: Exercise? = null,
        val sets: List<WorkoutSet> = emptyList(),
        val isLoading: Boolean = false,
        val isSaving: Boolean = false
    )

    sealed interface Label {
        data class ShowError(val message: String) : Label
        object NavigateBack : Label
    }
}
