package com.example.reptrack.presentation.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.R
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.presentation.main.components.Calendar
import com.example.reptrack.presentation.main.components.WorkoutExerciseCard
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.utils.painterResourceSafe
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Saver for LocalDate to enable rememberSaveable
 */
private val LocalDateSaver = Saver<LocalDate, String>(
    save = { it.toString() },
    restore = { LocalDate.parse(it) }
)

@Composable
internal fun MainScreen(
    store: MainScreenStore,
    calendarUseCase: com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase,
    onNavigateToExerciseDetail: (String) -> Unit = {},
    onNavigateToTemplates: () -> Unit = {}
) {
    val state by store.states.collectAsState(MainScreenStore.State())

    // Handle navigation labels
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                is MainScreenStore.Label.NavigateToExerciseDetail -> {
                    onNavigateToExerciseDetail(label.workoutExerciseId)
                }
                is MainScreenStore.Label.NavigateToTemplateExercise -> {
                    // This should not happen anymore as sessions are auto-created
                    onNavigateToExerciseDetail(label.exerciseId) // Fallback to exercise detail
                }
            }
        }
    }

    // Save selected date locally to survive screen rotation
    var selectedDate by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(state.currentDate)
    }

    // Update store when selected date changes
    if (selectedDate != state.currentDate) {
        store.accept(MainScreenStore.Intent.SelectDate(selectedDate))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToTemplates,
                containerColor = Color(0xFF2196F3)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Workout",
                    tint = Color.White
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Calendar(
                initialDate = selectedDate,
                selectedDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                },
                calendarUseCase = calendarUseCase
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
                state.workoutSession != null -> {
                    WorkoutDetails(
                        workout = state.workoutSession!!,
                        exerciseData = state.exerciseData,
                        onExerciseClick = { workoutExerciseId ->
                            store.accept(MainScreenStore.Intent.ExerciseClicked(workoutExerciseId))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                state.applicableTemplates.isNotEmpty() -> {
                    // Show exercises from templates
                    TemplateWorkoutDetails(
                        templates = state.applicableTemplates,
                        exerciseData = state.templateExerciseData,
                        onExerciseClick = { workoutExerciseId ->
                            store.accept(MainScreenStore.Intent.ExerciseClicked(workoutExerciseId))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                else -> {
                    NoWorkoutPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun TemplateWorkoutDetails(
    templates: List<WorkoutTemplate>,
    exerciseData: Map<String, Exercise>,
    onExerciseClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        templates.forEach { template ->
            Text(
                "Exercises: ${template.exerciseIds.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            template.exerciseIds.forEach { exerciseId ->
                val exercise = exerciseData[exerciseId]
                if (exercise != null) {
                    Box(
                        modifier = Modifier
                            .clickable { onExerciseClick(exerciseId) }
                    ) {
                        WorkoutExerciseCard(
                            exercise = exercise,
                            workoutExercise = com.example.reptrack.domain.workout.entities.WorkoutExercise(
                                id = "temp_${template.id}_$exerciseId",
                                workoutSessionId = template.id,
                                exerciseId = exerciseId,
                                sets = emptyList()
                            ),
                            bestSet = null,
                            muscleGroupColor = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun NoWorkoutPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "No workout planned for this day",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Add a template or start a new workout",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WorkoutDetails(
    workout: WorkoutSession,
    exerciseData: Map<String, MainScreenStore.ExerciseData>,
    onExerciseClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            "Workout: ${workout.name}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Date: ${workout.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "Status: ${workout.status}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "Duration: ${workout.durationSeconds / 60} minutes",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Exercises: ${workout.exercises.size}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        workout.exercises.forEach { workoutExercise ->
            val data = exerciseData[workoutExercise.exerciseId]

            if (data != null) {
                Box(
                    modifier = Modifier
                        .clickable { onExerciseClick(workoutExercise.id) }
                ) {
                    WorkoutExerciseCard(
                        exercise = data.exercise,
                        workoutExercise = data.workoutExercise,
                        bestSet = data.bestSet,
                        muscleGroupColor = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (workout.comment != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Comment: ${workout.comment}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
