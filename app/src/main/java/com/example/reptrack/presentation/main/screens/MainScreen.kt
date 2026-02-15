package com.example.reptrack.presentation.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.presentation.main.components.Calendar
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
    store: Store<MainScreenStore.Intent, MainScreenStore.State, Nothing>,
    calendarUseCase: CalendarUseCase
) {
    val state by store.states.collectAsState(MainScreenStore.State())

    // Save selected date locally to survive screen rotation
    var selectedDate by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(state.currentDate)
    }

    // Observe calendar week to get the selected workout
    val weekCalendar by calendarUseCase.observeWeekCalendar(selectedDate).collectAsState(
        initial = null
    )
    val selectedWorkout = weekCalendar?.days?.find { it.date == selectedDate }?.workoutSession

    // Update store when selected date changes
    LaunchedEffect(selectedDate) {
        store.accept(MainScreenStore.Intent.SelectDate(selectedDate))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Calendar(
            initialDate = selectedDate,
            selectedDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
            },
            calendarUseCase = calendarUseCase
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedWorkout != null) {
            WorkoutDetails(
                workout = selectedWorkout,
                modifier = Modifier.weight(1f)
            )
        } else {
            Text(
                "No workout scheduled for this day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun WorkoutDetails(
    workout: com.example.reptrack.domain.workout.WorkoutSession,
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

        workout.exercises.forEach { exercise ->
            Text(
                "• ${exercise.exerciseId}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
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