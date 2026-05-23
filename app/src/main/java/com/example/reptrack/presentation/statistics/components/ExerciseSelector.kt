package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.workout.entities.Exercise

@Composable
fun ExerciseSelector(
    exercises: List<Exercise>,
    selectedExerciseId: String?,
    onExerciseSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Select Exercise",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            var searchText by remember { mutableStateOf("") }

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search exercises") },
                modifier = Modifier.fillMaxWidth()
            )

            val filteredExercises = exercises.filter {
                it.name.contains(searchText, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(filteredExercises) { exercise ->
                    ExerciseItem(
                        name = exercise.name,
                        muscleGroup = exercise.muscleGroup.name,
                        isSelected = exercise.id == selectedExerciseId,
                        onClick = { onExerciseSelected(exercise.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseItem(
    name: String,
    muscleGroup: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = muscleGroup,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}