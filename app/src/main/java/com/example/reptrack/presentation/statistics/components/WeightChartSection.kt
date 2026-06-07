package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.statistics.entities.FriendConfig
import com.example.reptrack.presentation.statistics.components.charts.LineChartView
import com.example.reptrack.presentation.statistics.components.common.FriendChip
import com.example.reptrack.presentation.statistics.utils.colorFromArgb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeightChartSection(
    currentWeight: Float?,
    weightData: List<Pair<Float, Float>>, // List of (timestamp, weight)
    friendWeightData: Map<String, List<Pair<Float, Float>>>, // friendId -> data
    friends: List<FriendConfig>,
    dateRange: String,
    onWeightSave: (Float) -> Unit,
    onAddFriend: () -> Unit,
    onRemoveFriend: (String) -> Unit,
    onFriendColorChange: (String, Color) -> Unit,
    onChangeDateRange: () -> Unit,
    isGuest: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Try to get the latest weight from weightData if currentWeight is null
    val latestWeightFromData = weightData.lastOrNull()?.second

    var editingWeight by remember { mutableStateOf((currentWeight ?: latestWeightFromData ?: 50f).coerceIn(10f, Float.MAX_VALUE)) }

    // Update editingWeight when data changes
    LaunchedEffect(currentWeight, weightData) {
        val newWeight = (currentWeight ?: weightData.lastOrNull()?.second ?: 50f).coerceIn(10f, Float.MAX_VALUE)
        if (editingWeight != newWeight) {
            android.util.Log.d("WeightChartSection", "Updating editingWeight: $editingWeight -> $newWeight")
            editingWeight = newWeight
        }
    }
    var showSaveButton by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
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
                    text = "Прогресс веса",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = onChangeDateRange) {
                    Text(dateRange)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Compact weight controller (like in exercise)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weight display with +/- buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minus button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable {
                                editingWeight = (editingWeight - 0.5f).coerceAtLeast(10f)
                                showSaveButton = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Weight display
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val displayWeight = String.format("%.1f", editingWeight)
                            android.util.Log.d("WeightChartSection", "Displaying weight: $editingWeight -> formatted: $displayWeight")
                            Text(
                                text = displayWeight,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                            text = "кг",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Plus button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable {
                                editingWeight += 0.5f
                                showSaveButton = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // Save button
                if (showSaveButton) {
                    Button(
                        onClick = {
                            onWeightSave(editingWeight)
                            showSaveButton = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Сохранить вес",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(20.dp))

            // Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                if (weightData.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет данных о весе",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val chartData = mutableMapOf(
                        "You" to weightData
                    )

                    // Add friends' data
                    friends.forEach { friend ->
                        friendWeightData[friend.friendId]?.let { data ->
                            chartData[friend.friendName] = data
                        }
                    }

                    val seriesColors = mutableMapOf(
                        "You" to MaterialTheme.colorScheme.primary
                    )

                    friends.forEach { friend ->
                        val friendColor = colorFromArgb(friend.color)
                        android.util.Log.d("WeightChart", "Friend ${friend.friendName} color: ARGB=0x${friend.color.toString(16)}, Color=$friendColor")
                        seriesColors[friend.friendName] = friendColor
                    }

                    LineChartView(
                        data = chartData,
                        seriesColors = seriesColors,
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
                                    onChangeColor = { newColor -> onFriendColorChange(friend.friendId, newColor) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    return date.format(formatter)
}

