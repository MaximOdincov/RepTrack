package com.example.reptrack.presentation.library.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.presentation.exercise.list.components.MuscleGroupExpansionState
import com.example.reptrack.presentation.exercise.list.screens.ExerciseListScreen
import com.example.reptrack.presentation.exercise.list.screens.ExpansionStateSaver
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore
import com.example.reptrack.presentation.template.components.TemplateCard
import com.example.reptrack.presentation.template.list.screens.TemplateListScreen
import com.example.reptrack.presentation.template.list.stores.TemplateListStore
import com.example.reptrack.presentation.theme.LightAccentOrange

enum class LibraryMode {
    VIEW,
    ADD_TO_WORKOUT
}

/**
 * Library screen with tabs for Exercises and Workout Templates
 *
 * @param exerciseStore MVIKotlin store for exercises
 * @param templateStore MVIKotlin store for templates
 * @param mode Library mode (VIEW or ADD_TO_WORKOUT)
 * @param onNavigateToExerciseDetail Callback when navigating to exercise detail
 * @param onNavigateToTemplateDetail Callback when navigating to template detail
 * @param onNavigateToAddExercise Callback when clicking add exercise button
 * @param onNavigateToAddTemplate Callback when clicking add template button
 * @param onAddExerciseToWorkout Callback when adding exercise to current workout
 * @param onAddTemplateToWorkout Callback when adding template to current workout
 */
@Composable
fun LibraryScreen(
    exerciseStore: ExerciseListStore,
    templateStore: TemplateListStore,
    mode: LibraryMode = LibraryMode.VIEW,
    onNavigateToExerciseDetail: (String) -> Unit = {},
    onNavigateToTemplateDetail: (String) -> Unit = {},
    onNavigateToAddExercise: () -> Unit = {},
    onNavigateToAddTemplate: () -> Unit = {},
    onAddExerciseToWorkout: (Exercise) -> Unit = {},
    onAddTemplateToWorkout: (WorkoutTemplate) -> Unit = {}
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf("Упражнения", "Шаблоны")

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color.Black,
            indicator = { },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) Color.Black else Color.Gray
                        )
                    },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // Exercises Tab
                    ExerciseTabContent(
                        exerciseStore = exerciseStore,
                        mode = mode,
                        onNavigateToExerciseDetail = onNavigateToExerciseDetail,
                        onNavigateToAddExercise = onNavigateToAddExercise,
                        onAddExerciseToWorkout = onAddExerciseToWorkout
                    )
                }
                1 -> {
                    // Templates Tab
                    TemplateTabContent(
                        templateStore = templateStore,
                        mode = mode,
                        onNavigateToTemplateDetail = onNavigateToTemplateDetail,
                        onNavigateToAddTemplate = onNavigateToAddTemplate,
                        onAddTemplateToWorkout = onAddTemplateToWorkout
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseTabContent(
    exerciseStore: ExerciseListStore,
    mode: LibraryMode,
    onNavigateToExerciseDetail: (String) -> Unit,
    onNavigateToAddExercise: () -> Unit,
    onAddExerciseToWorkout: (Exercise) -> Unit
) {
    val state by exerciseStore.states.collectAsState(ExerciseListStore.State())

    // Collect labels
    LaunchedEffect(Unit) {
        exerciseStore.labels.collect { label ->
            when (label) {
                is ExerciseListStore.Label.NavigateToDetail -> {
                    onNavigateToExerciseDetail(label.exerciseId)
                }
                is ExerciseListStore.Label.NavigateToAddExercise -> {
                    onNavigateToAddExercise()
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        exerciseStore.accept(ExerciseListStore.Intent.Initialize(
            com.example.reptrack.navigation.ExerciseListMode.VIEW_MODE
        ))
    }

    // Reuse ExerciseListContent from ExerciseListScreen
    var searchInput by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.searchQuery) {
        if (searchInput.isBlank() && state.searchQuery.isNotBlank()) {
            searchInput = state.searchQuery
        }
    }

    LaunchedEffect(Unit) {
        if (searchInput.isNotBlank()) {
            exerciseStore.accept(ExerciseListStore.Intent.SearchChanged(searchInput))
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    exerciseStore.accept(ExerciseListStore.Intent.AddExerciseClicked)
                },
                containerColor = LightAccentOrange
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = "Add Exercise",
                    tint = Color.White
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
                com.example.reptrack.presentation.exercise.list.components.ExerciseSearchBar(
                    query = searchInput,
                    onQueryChange = { newValue ->
                        searchInput = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                if (state.isLoading) {
                    // TODO: Add loading indicator
                } else {
                    val exercisesToShow = if (searchInput.isBlank()) {
                        state.exercisesByGroup
                    } else {
                        state.filteredExercises
                    }

                    val expansionState = rememberSaveable(
                        saver = ExpansionStateSaver
                    ) {
                        MuscleGroupExpansionState()
                    }

                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = exercisesToShow.entries.toList(),
                            key = { it.key.name }
                        ) { (muscleGroup, exercises) ->
                            com.example.reptrack.presentation.exercise.list.components.MuscleGroupCard(
                                muscleGroup = muscleGroup,
                                exercises = exercises,
                                onExerciseClick = { exercise ->
                                    if (mode == LibraryMode.ADD_TO_WORKOUT) {
                                        onAddExerciseToWorkout(exercise)
                                    } else {
                                        exerciseStore.accept(ExerciseListStore.Intent.ExerciseClicked(exercise))
                                    }
                                },
                                onDeleteExercise =
                                    { exerciseId ->
                                        exerciseStore.accept(ExerciseListStore.Intent.DeleteExercise(exerciseId))
                                    },
                                expansionState = expansionState
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateTabContent(
    templateStore: TemplateListStore,
    mode: LibraryMode,
    onNavigateToTemplateDetail: (String) -> Unit,
    onNavigateToAddTemplate: () -> Unit,
    onAddTemplateToWorkout: (WorkoutTemplate) -> Unit
) {
    val state by templateStore.states.collectAsState(TemplateListStore.State())

    // Collect labels
    LaunchedEffect(Unit) {
        templateStore.labels.collect { label ->
            when (label) {
                is TemplateListStore.Label.NavigateToDetail -> {
                    onNavigateToTemplateDetail(label.templateId)
                }
                is TemplateListStore.Label.NavigateToAddTemplate -> {
                    onNavigateToAddTemplate()
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        templateStore.accept(TemplateListStore.Intent.Initialize(
            TemplateListStore.TemplateListMode.VIEW_MODE
        ))
    }

    var searchInput by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.searchQuery) {
        if (searchInput.isBlank() && state.searchQuery.isNotBlank()) {
            searchInput = state.searchQuery
        }
    }

    LaunchedEffect(Unit) {
        if (searchInput.isNotBlank()) {
            templateStore.accept(TemplateListStore.Intent.SearchChanged(searchInput))
        }
    }

    var templateToDelete by androidx.compose.runtime.remember { mutableStateOf<WorkoutTemplate?>(null) }

    Scaffold(
        floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        templateStore.accept(TemplateListStore.Intent.AddTemplateClicked)
                    },
                    containerColor = LightAccentOrange
                ) {
                   Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Add,
                        contentDescription = "Add Template",
                        tint = Color.White
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
                androidx.compose.material3.TextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = {
                        androidx.compose.material3.Text(
                            "Поиск шаблонов...",
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = LightAccentOrange
                        )
                    }
                } else {
                    val templatesToShow = if (searchInput.isBlank()) {
                        state.templates
                    } else {
                        state.filteredTemplates
                    }

                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = templatesToShow,
                            key = { it.id }
                        ) { template ->
                            com.example.reptrack.presentation.template.components.SwipeToDeleteTemplateCard(
                                template = template,
                                onClick = {
                                    if (mode == LibraryMode.ADD_TO_WORKOUT) {
                                        onAddTemplateToWorkout(template)
                                    } else {
                                        templateStore.accept(TemplateListStore.Intent.TemplateClicked(template))
                                    }
                                },
                                onDeleteTemplate = { templateToDelete = template },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    templateToDelete?.let { template ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { templateToDelete = null },
            title = {
                Text("Удалить шаблон?")
            },
            text = {
                Text("Вы уверены, что хотите удалить шаблон \"${template.name}\"?")
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        templateStore.accept(TemplateListStore.Intent.DeleteTemplate(template.id))
                        templateToDelete = null
                    }
                ) {
                    Text("Удалить", color = Color(0xFFEF5350))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { templateToDelete = null }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}
