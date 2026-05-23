package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.statistics.entities.DateRange
import java.time.LocalDateTime

@Composable
fun DateRangeSelector(
    selectedRange: DateRange,
    onRangeSelected: (DateRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Time Period",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                DateRangeChip(
                    label = "7 Days",
                    isSelected = isRangeEqual(selectedRange, DateRange.last7Days()),
                    onClick = { onRangeSelected(DateRange.last7Days()) }
                )

                DateRangeChip(
                    label = "30 Days",
                    isSelected = isRangeEqual(selectedRange, DateRange.last30Days()),
                    onClick = { onRangeSelected(DateRange.last30Days()) }
                )

                DateRangeChip(
                    label = "3 Months",
                    isSelected = isRangeEqual(selectedRange, DateRange.last3Months()),
                    onClick = { onRangeSelected(DateRange.last3Months()) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                DateRangeChip(
                    label = "Year",
                    isSelected = isRangeEqual(selectedRange, DateRange.lastYear()),
                    onClick = { onRangeSelected(DateRange.lastYear()) }
                )

                DateRangeChip(
                    label = "All Time",
                    isSelected = isRangeEqual(selectedRange, DateRange.allTime()),
                    onClick = { onRangeSelected(DateRange.allTime()) }
                )
            }
        }
    }
}

@Composable
private fun DateRangeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall
            )
        },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

private fun isRangeEqual(range1: DateRange, range2: DateRange): Boolean {
    // Compare by checking if the ranges are approximately equal (within 1 day)
    val diffDays = kotlin.math.abs(
        java.time.Duration.between(range1.from, range2.from).toDays()
    )
    return diffDays <= 1
}