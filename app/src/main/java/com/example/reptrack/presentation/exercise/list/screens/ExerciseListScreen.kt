package com.example.reptrack.presentation.exercise.list.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore

/**
 * Exercise List screen
 *
 * @param store MVIKotlin store for state management
 * @param onNavigateToDetail Callback when navigating to exercise detail (VIEW_MODE)
 * @param onAddToWorkoutAndBack Callback when adding exercise to workout (WORKOUT_MODE)
 * @param onNavigateToAddExercise Callback when clicking add exercise button
 * @param onInitialize Callback to initialize store with mode
 */
@Composable
fun ExerciseListScreen(
    store: ExerciseListStore,
    onNavigateToDetail: (String) -> Unit = {},
    onAddToWorkoutAndBack: (com.example.reptrack.domain.workout.entities.Exercise) -> Unit = {},
    onNavigateToAddExercise: () -> Unit = {},
    onInitialize: () -> Unit = {}
) {
    val state by store.states.collectAsState(ExerciseListStore.State())

    LaunchedEffect(Unit) {
        onInitialize()
    }

    LaunchedEffect(Unit) {
        store.labels.collect { label ->
            when (label) {
                is ExerciseListStore.Label.NavigateToDetail -> {
                    onNavigateToDetail(label.exerciseId)
                }
                is ExerciseListStore.Label.AddToWorkoutAndBack -> {
                    onAddToWorkoutAndBack(label.exercise)
                }
                is ExerciseListStore.Label.NavigateToAddExercise -> {
                    onNavigateToAddExercise()
                }
            }
        }
    }

    // TODO: Implement UI
    // For now, just render state info for debugging
    ExerciseListContent(
        state = state,
        onExerciseClick = { exercise ->
            store.accept(ExerciseListStore.Intent.ExerciseClicked(exercise))
        },
        onSearchChanged = { query ->
            store.accept(ExerciseListStore.Intent.SearchChanged(query))
        },
        onAddExerciseClick = {
            store.accept(ExerciseListStore.Intent.AddExerciseClicked)
        }
    )
}

/**
 * Temporary content component for debugging
 * TODO: Replace with actual UI implementation
 */
@Composable
private fun ExerciseListContent(
    state: ExerciseListStore.State,
    onExerciseClick: (com.example.reptrack.domain.workout.entities.Exercise) -> Unit,
    onSearchChanged: (String) -> Unit,
    onAddExerciseClick: () -> Unit
) {
    // TODO: Implement actual UI with:
    // - Search bar
    // - Exercise list grouped by muscle group
    // - Add exercise button (FAB)
    // - Loading state
    // - Empty state

    androidx.compose.material3.Text(
        text = """
            Mode: ${state.mode}
            Loading: ${state.isLoading}
            Search: ${state.searchQuery}
            Exercises groups: ${state.exercisesByGroup.size}
            Filtered groups: ${state.filteredExercises.size}
        """.trimIndent()
    )
}
