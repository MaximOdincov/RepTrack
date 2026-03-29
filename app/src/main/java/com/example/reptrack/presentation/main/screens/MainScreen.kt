package com.example.reptrack.presentation.main.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.R
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.presentation.main.components.Calendar
import com.example.reptrack.presentation.main.components.WorkoutExerciseCard
import com.example.reptrack.presentation.main.stores.MainScreenStore
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
                    painter = painterResource(R.drawable.template_icon),
                    contentDescription = "Templates",
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
                else -> {
                    Text(
                        "No workout scheduled for this day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
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
