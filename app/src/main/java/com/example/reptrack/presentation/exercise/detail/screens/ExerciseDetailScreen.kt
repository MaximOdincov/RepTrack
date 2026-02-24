package com.example.reptrack.presentation.exercise.detail.screens

import androidx.compose.runtime.Composable
import com.arkivanov.mvikotlin.core.store.Store
import com.example.reptrack.navigation.ExerciseDetailMode
import com.example.reptrack.presentation.exercise.detail.stores.ExerciseDetailStore

/**
 * Exercise Detail screen
 * TODO: Implement UI on next iteration
 *
 * @param store MVIKotlin store for state management
 * @param exerciseId ID of the exercise to display
 * @param mode Screen mode (DESIGN_MODE or WORKOUT_MODE)
 */
@Composable
fun ExerciseDetailScreen(
    store: Store<ExerciseDetailStore.Intent, ExerciseDetailStore.State, ExerciseDetailStore.Label>,
    exerciseId: String,
    mode: ExerciseDetailMode
) {
    // TODO: Implement UI
}
