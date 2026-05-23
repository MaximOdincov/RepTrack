package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.statistics.entities.MuscleGroupDataPoint
import com.example.reptrack.domain.workout.entities.MuscleGroup

@Composable
fun SpiderChart(
    muscleGroupData: List<MuscleGroupDataPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Muscle Group Frequency",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (muscleGroupData.isEmpty()) {
            Text(
                text = "No muscle group data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            muscleGroupData.forEach { data ->
                Text(
                    text = "${data.muscleGroup.name}: ${data.frequency} workouts",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Placeholder for spider chart - to be implemented with Vico
            Text(
                text = "Spider chart will be displayed here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}