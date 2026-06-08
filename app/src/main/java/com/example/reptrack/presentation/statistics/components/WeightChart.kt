package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.statistics.entities.WeightDataPoint
import java.time.format.DateTimeFormatter

@Composable
fun WeightChart(
    weightData: List<WeightDataPoint>,
    modifier: Modifier = Modifier
) {
    if (weightData.isEmpty()) {
        Text(
            text = "No weight data available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
        return
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Weight Progress",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        // Group data by user
        val groupedData = weightData.groupBy { it.userName }

        groupedData.forEach { (userName, data) ->
            Text(
                text = "$userName: ${data.lastOrNull()?.value?.toInt() ?: 0} kg",
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