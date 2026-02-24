package com.example.reptrack.presentation.exercise.list.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.R
import com.example.reptrack.presentation.exercise.list.components.ExerciseSearchBar
import com.example.reptrack.presentation.exercise.list.components.MuscleGroupCard
import com.example.reptrack.presentation.exercise.list.components.MuscleGroupExpansionState
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore
import com.example.reptrack.presentation.theme.LightAccentOrange
import kotlinx.coroutines.delay

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

    // Use rememberSaveable to preserve search query across configuration changes
    var searchInput by rememberSaveable { mutableStateOf("") }

    // Sync searchInput with state when screen is first created or restored
    LaunchedEffect(state.searchQuery) {
        if (searchInput.isBlank() && state.searchQuery.isNotBlank()) {
            searchInput = state.searchQuery
        }
    }

    // Apply saved search query immediately after initialization
    LaunchedEffect(Unit) {
        onInitialize()
        // Small delay to ensure store is initialized
        kotlinx.coroutines.delay(50)
        if (searchInput.isNotBlank()) {
            store.accept(ExerciseListStore.Intent.SearchChanged(searchInput))
        }
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

    ExerciseListContent(
        state = state,
        searchInput = searchInput,
        onSearchInputChange = { searchInput = it },
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
 * Saver for MuscleGroupExpansionState
 */
private val ExpansionStateSaver = Saver<MuscleGroupExpansionState, Map<String, Boolean>>(
    save = { it.getState() },
    restore = { MuscleGroupExpansionState().apply { restoreState(it) } }
)

/**
 * Exercise List content with search, accordion list, and FAB
 */
@Composable
private fun ExerciseListContent(
    state: ExerciseListStore.State,
    searchInput: String,
    onSearchInputChange: (String) -> Unit,
    onExerciseClick: (com.example.reptrack.domain.workout.entities.Exercise) -> Unit,
    onSearchChanged: (String) -> Unit,
    onAddExerciseClick: () -> Unit
) {
    // Save expansion state across configuration changes
    val expansionState = rememberSaveable(saver = ExpansionStateSaver) {
        MuscleGroupExpansionState()
    }

    // Debounce search with 300ms delay
    LaunchedEffect(searchInput) {
        delay(300)
        if (searchInput != state.searchQuery) {
            onSearchChanged(searchInput)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExerciseClick,
                containerColor = LightAccentOrange
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_up_icon),
                    contentDescription = "Add Exercise",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                ExerciseSearchBar(
                    query = searchInput,
                    onQueryChange = { newValue ->
                        onSearchInputChange(newValue)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                if (state.isLoading) {
                    // TODO: Add loading indicator
                } else {
                    // Use filteredExercises if searchInput is not empty, even if debounce hasn't completed yet
                    // This prevents flickering on configuration changes
                    val exercisesToShow = if (searchInput.isBlank()) {
                        state.exercisesByGroup
                    } else {
                        // If searchInput has text but filteredExercises is empty (debounce pending),
                        // use the previous filter result from state
                        state.filteredExercises
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = exercisesToShow.entries.toList(),
                            key = { it.key.name }
                        ) { (muscleGroup, exercises) ->
                            MuscleGroupCard(
                                muscleGroup = muscleGroup,
                                exercises = exercises,
                                onExerciseClick = onExerciseClick,
                                expansionState = expansionState
                            )
                        }
                    }
                }
            }
        }
    }
}
