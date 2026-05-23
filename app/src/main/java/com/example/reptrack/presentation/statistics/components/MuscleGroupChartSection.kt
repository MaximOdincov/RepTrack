package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.statistics.entities.FriendConfig
import com.example.reptrack.domain.statistics.entities.MuscleGroupDataPoint
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.presentation.statistics.components.charts.SpiderChartView
import com.example.reptrack.presentation.statistics.components.charts.SpiderChartData
import com.example.reptrack.presentation.statistics.components.common.FriendChip

@Composable
fun MuscleGroupChartSection(
    muscleGroupData: List<MuscleGroupDataPoint>,
    friends: List<FriendConfig>,
    friendMuscleData: Map<String, List<MuscleGroupDataPoint>>,
    dateRange: String,
    onAddFriend: () -> Unit,
    onRemoveFriend: (String) -> Unit,
    onChangeDateRange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and date range selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Muscle Group Focus",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = onChangeDateRange) {
                    Text(dateRange)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Spider Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                val labels = getMuscleGroupLabels()
                val maxValue = calculateMaxValue(muscleGroupData, friendMuscleData)

                val chartData = mutableListOf<SpiderChartData>()

                // User data
                if (muscleGroupData.isNotEmpty()) {
                    val userValues = labels.map { label ->
                        val groupName = getMuscleGroupFromLabel(label)
                        muscleGroupData.find { it.muscleGroup == groupName }?.frequency?.toFloat() ?: 0f
                    }
                    chartData.add(
                        SpiderChartData(
                            values = userValues,
                            color = MaterialTheme.colorScheme.primary,
                            label = "You"
                        )
                    )
                }

                // Friends data
                friends.forEach { friend ->
                    val friendData = friendMuscleData[friend.friendId]
                    if (friendData != null && friendData.isNotEmpty()) {
                        val friendValues = labels.map { label ->
                            val groupName = getMuscleGroupFromLabel(label)
                            friendData.find { it.muscleGroup == groupName }?.frequency?.toFloat() ?: 0f
                        }
                        chartData.add(
                            SpiderChartData(
                                values = friendValues,
                                color = Color(friend.color),
                                label = friend.friendName
                            )
                        )
                    }
                }

                SpiderChartView(
                    data = chartData,
                    labels = labels,
                    maxValue = maxValue,
                    modifier = Modifier.fillMaxSize()
                )

                // Legend
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    chartData.forEach { series ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(series.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = series.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Friends section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Friends",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (friends.size < 3) {
                        Button(onClick = onAddFriend) {
                            Text("Add Friend")
                        }
                    }
                }

                if (friends.isEmpty()) {
                    Text(
                        text = "No friends added yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        friends.forEach { friend ->
                            FriendChip(
                                name = friend.friendName,
                                color = Color(friend.color),
                                onRemove = { onRemoveFriend(friend.friendId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getMuscleGroupLabels(): List<String> {
    return listOf(
        "Chest",
        "Back",
        "Legs",
        "Cardio",
        "Arms",
        "Abs"
    )
}

private fun getMuscleGroupFromLabel(label: String): MuscleGroup {
    return when (label) {
        "Chest" -> MuscleGroup.CHEST
        "Back" -> MuscleGroup.BACK
        "Legs" -> MuscleGroup.LEGS
        "Cardio" -> MuscleGroup.CARDIO
        "Arms" -> MuscleGroup.ARMS
        "Abs" -> MuscleGroup.ABS
        else -> MuscleGroup.CHEST
    }
}

private fun calculateMaxValue(
    userData: List<MuscleGroupDataPoint>,
    friendData: Map<String, List<MuscleGroupDataPoint>>
): Float {
    var maxValue = 0f

    userData.forEach { maxValue = maxOf(maxValue, it.frequency.toFloat()) }
    friendData.values.forEach { data ->
        data.forEach { maxValue = maxOf(maxValue, it.frequency.toFloat()) }
    }

    return maxOf(maxValue, 10f) // Minimum scale of 10
}