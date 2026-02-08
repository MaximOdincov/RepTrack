package com.example.reptrack.presentation.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.presentation.main.components.Calendar
import com.example.reptrack.presentation.main.stores.MainScreenStore
import java.time.format.DateTimeFormatter

@Composable
internal fun MainScreen(store: Store<MainScreenStore.Intent, MainScreenStore.State, MainScreenStore.Label>) {
    val state = store.states.collectAsState(MainScreenStore.State())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Calendar(
            currentDate = state.value.currentDate,
            displayDate = state.value.displayDate,
            weekCalendar = state.value.weekCalendar,
            monthCalendar = state.value.monthCalendar,
            isCalendarExpanded = state.value.isCalendarExpanded,
            onDateSelected = { selectedDate ->
                store.accept(MainScreenStore.Intent.SelectDate(selectedDate))
            },
            onNavigateWeek = { offset ->
                store.accept(MainScreenStore.Intent.NavigateWeek(offset))
            },
            onNavigateMonth = { offset ->
                store.accept(MainScreenStore.Intent.NavigateMonth(offset))
            },
            onExpandCalendar = {
                store.accept(MainScreenStore.Intent.ExpandCalendar)
            },
            onCollapseCalendar = {
                store.accept(MainScreenStore.Intent.CollapseCalendar)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        val selectedWorkout = state.value.selectedWorkout
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

        // Error handling
        if (state.value.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Error: ${state.value.error}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
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
