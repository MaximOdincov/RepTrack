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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.reptrack.presentation.statistics.utils.colorFromArgb
import com.example.reptrack.presentation.statistics.utils.colorToArgb


@Composable
fun MuscleGroupChartSection(
    muscleGroupData: List<MuscleGroupDataPoint>,
    friends: List<FriendConfig>,
    friendMuscleData: Map<String, List<MuscleGroupDataPoint>>,
    dateRange: String,
    userColor: Long,
    isLoading: Boolean,
    onAddFriend: () -> Unit,
    onRemoveFriend: (String) -> Unit,
    onFriendColorChange: (String, Color) -> Unit,
    onUserColorChange: (Color) -> Unit,
    onChangeDateRange: () -> Unit,
    isGuest: Boolean = false,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Группы мышц",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onChangeDateRange) {
                            Text(dateRange)
                        }
                        // Color selector for user's chart
                        var showColorPicker by remember { mutableStateOf(false) }
                        val availableColors = remember {
                            listOf(
                                com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors.getPrimaryColor(
                                    MuscleGroup.CHEST
                                ),
                                com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors.getPrimaryColor(
                                    MuscleGroup.BACK
                                ),
                                com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors.getPrimaryColor(
                                    MuscleGroup.LEGS
                                ),
                                com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors.getPrimaryColor(
                                    MuscleGroup.ARMS
                                ),
                                com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors.getPrimaryColor(
                                    MuscleGroup.ABS
                                ),
                                com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors.getPrimaryColor(
                                    MuscleGroup.CARDIO
                                ),
                                Color(0xFF6366F1), // Indigo - fallback
                                Color(0xFFEC4899)  // Pink - fallback
                            )
                        }

                        // Convert available colors to ARGB Long for comparison
                        val availableColorsArgb = remember {
                            availableColors.map { colorToArgb(it) }
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = colorFromArgb(userColor),
                                    shape = CircleShape
                                )
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    shape = CircleShape
                                )
                                .clickable { showColorPicker = true }
                        )

                        if (showColorPicker) {
                            androidx.compose.ui.window.Dialog(onDismissRequest = {
                                showColorPicker = false
                            }) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Выберите ваш цвет",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(bottom = 12.dp)
                                        )
                                        availableColors.chunked(4).forEach { row ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                row.forEach { color ->
                                                    val isSelected = colorToArgb(color) == userColor
                                                    Box(
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .background(
                                                                color = color,
                                                                shape = RoundedCornerShape(8.dp)
                                                            )
                                                            .border(
                                                                width = 2.dp,
                                                                color = if (isSelected)
                                                                    MaterialTheme.colorScheme.primary
                                                                else
                                                                    Color.Transparent,
                                                                shape = RoundedCornerShape(8.dp)
                                                            )
                                                            .clickable {
                                                                android.util.Log.d(
                                                                    "MuscleChart",
                                                                    "User selected color: $color"
                                                                )
                                                                val argb = colorToArgb(color)
                                                                android.util.Log.d(
                                                                    "MuscleChart",
                                                                    "User selected ARGB: 0x${
                                                                        argb.toString(16)
                                                                    }"
                                                                )
                                                                onUserColorChange(color)
                                                                showColorPicker = false
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate chart data (needed for both chart and legend)
            val labels = getMuscleGroupLabels()
            val maxValue = calculateMaxValue(muscleGroupData, friendMuscleData)

            val chartData = mutableListOf<SpiderChartData>()

            // User data
            if (muscleGroupData.isNotEmpty()) {
                val userValues = labels.map { label ->
                    val groupName = getMuscleGroupFromLabel(label)
                    muscleGroupData.find { it.muscleGroup == groupName }?.frequency?.toFloat() ?: 0f
                }
                val userColorActual = colorFromArgb(userColor)
                android.util.Log.d(
                    "MuscleChart",
                    "User color: ARGB=0x${userColor.toString(16)}, Converted=$userColorActual"
                )
                chartData.add(
                    SpiderChartData(
                        values = userValues,
                        color = userColorActual,
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
                    val friendColorActual = colorFromArgb(friend.color)
                    android.util.Log.d(
                        "MuscleChart",
                        "Friend ${friend.friendName} color: ARGB=0x${friend.color.toString(16)}, Converted=$friendColorActual"
                    )
                    chartData.add(
                        SpiderChartData(
                            values = friendValues,
                            color = friendColorActual,
                            label = friend.friendName
                        )
                    )
                }
            }

            // Spider Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                if (isLoading) {
                    // Show loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    SpiderChartView(
                        data = chartData,
                        labels = labels,
                        maxValue = maxValue,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Friends section - only show if not guest
            if (!isGuest) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Друзья",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (friends.size < 3) {
                            Button(onClick = onAddFriend) {
                                Text("Добавить друга")
                            }
                        }
                    }

                    if (friends.isEmpty()) {
                        Text(
                            text = "Нет добавленных друзей",
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
                                    color = colorFromArgb(friend.color),
                                    onRemove = { onRemoveFriend(friend.friendId) },
                                    onChangeColor = { newColor ->
                                        onFriendColorChange(
                                            friend.friendId,
                                            newColor
                                        )
                                    }
                                )
                            }
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