package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.statistics.entities.ExerciseDataPoint

@Composable
fun ExerciseChart(
    exerciseData: Map<Int, List<ExerciseDataPoint>>,
    visibleSets: Set<Int>,
    setColors: Map<Int, Long>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Exercise Progress",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (exerciseData.isEmpty()) {
            Text(
                text = "No exercise data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            visibleSets.forEach { setIndex ->
                val setData = exerciseData[setIndex] ?: emptyList()
                val maxWeight = setData.maxOfOrNull { it.value } ?: 0f
                Text(
                    text = "Set $setIndex: ${maxWeight.toInt()} kg",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Placeholder for chart - to be implemented with Vico
            Text(
                text = "Chart will be displayed here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}