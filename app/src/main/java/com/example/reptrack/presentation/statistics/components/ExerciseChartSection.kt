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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reptrack.R
import com.example.reptrack.domain.statistics.entities.FriendConfig
import com.example.reptrack.presentation.statistics.components.charts.LineChartView
import com.example.reptrack.presentation.statistics.components.common.FriendChip

@Composable
fun ExerciseChartSection(
    key: String? = null,
    selectedExerciseId: String?,
    exercises: List<ExerciseInfo>,
    exerciseData: Map<Int, List<Pair<Float, Float>>>, // setIndex -> (timestamp, weight)
    visibleSets: Set<Int>,
    setColors: Map<Int, Long>,
    friends: List<FriendConfig>,
    friendExerciseData: Map<String, List<Pair<Float, Float>>>, // friendId -> data points
    dateRange: String,
    onExerciseSelect: (String) -> Unit,
    onToggleSetVisibility: (Int) -> Unit,
    onSetColorChange: (Int, Color) -> Unit,
    onAddFriend: () -> Unit,
    onRemoveFriend: (String) -> Unit,
    onFriendColorChange: (String, Color) -> Unit,
    onChangeDateRange: () -> Unit,
    isGuest: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Log on every composition
    android.util.Log.d("important", "=== ExerciseChartSection recomposed ===")
    android.util.Log.d("important", "selectedExerciseId: $selectedExerciseId")
    android.util.Log.d("important", "exerciseData size: ${exerciseData.size}")
    android.util.Log.d("important", "exerciseData keys: ${exerciseData.keys}")
    android.util.Log.d("important", "friendExerciseData size: ${friendExerciseData.size}")
    android.util.Log.d("important", "friendExerciseData keys: ${friendExerciseData.keys}")
    android.util.Log.d("important", "friends list: ${friends.map { it.friendId to it.friendName }}")
    android.util.Log.d("important", "visibleSets: $visibleSets")
    android.util.Log.d("important", "exercises count: ${exercises.size}")

    var showExerciseDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with exercise selector and date range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exercise selector dropdown
                Box {
                    Surface(
                        modifier = Modifier,
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { showExerciseDropdown = !showExerciseDropdown }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedExerciseId?.let { id ->
                                    exercises.find { it.id == id }?.name ?: "Упражнение"
                                } ?: "Упражнение",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = showExerciseDropdown,
                        onDismissRequest = { showExerciseDropdown = false },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        exercises.forEach { exercise ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = exercise.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (exercise.id == selectedExerciseId) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    android.util.Log.d(
                                        "ExerciseChartSection",
                                        "Exercise selected: ${exercise.id}, name: ${exercise.name}"
                                    )
                                    android.util.Log.d(
                                        "ExerciseChartSection",
                                        "Current exerciseData size: ${exerciseData.size}"
                                    )
                                    android.util.Log.d(
                                        "ExerciseChartSection",
                                        "Current selectedExerciseId: $selectedExerciseId"
                                    )
                                    onExerciseSelect(exercise.id)
                                    showExerciseDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedButton(onClick = onChangeDateRange) {
                    Text(dateRange)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sets configuration
            if (selectedExerciseId != null && exerciseData.isNotEmpty()) {
                Text(
                    text = "Подходы",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = exerciseData.keys.sorted().toList().take(10),
                        key = { setIndex: Int -> setIndex.toString() }
                    ) { setIndex ->
                        val isVisible = setIndex in visibleSets

                        // Default colors for sets (deterministic by index) - same as in StatisticsStore
                        val defaultColors = listOf(
                            Color(0xFF6366F1), // Indigo
                            Color(0xFFEC4899), // Pink
                            Color(0xFF10B981), // Emerald
                            Color(0xFFF59E0B), // Amber
                            Color(0xFFEF4444), // Red
                            Color(0xFF8B5CF6), // Violet
                            Color(0xFF06B6D4), // Cyan
                            Color(0xFF84CC16), // Lime
                            Color(0xFFF97316), // Orange
                            Color(0xFF0EA5E9)  // Sky
                        )

                        val color = if (setColors[setIndex] == null) {
                            android.util.Log.d(
                                "ExerciseChartSection",
                                "Set $setIndex: color from state is null, using default color"
                            )
                            val defaultColor = defaultColors.getOrElse(setIndex) {
                                defaultColors[setIndex % defaultColors.size]
                            }
                            android.util.Log.d(
                                "ExerciseChartSection",
                                "Default color for set $setIndex: $defaultColor"
                            )
                            defaultColor
                        } else {
                            // Unpack ARGB from Long - divide by 255 to get Float (0.0-1.0)
                            val argb = setColors[setIndex]!!
                            val alpha = ((argb shr 24) and 0xFF).toInt() / 255f
                            val red = ((argb shr 16) and 0xFF).toInt() / 255f
                            val green = ((argb shr 8) and 0xFF).toInt() / 255f
                            val blue = (argb and 0xFF).toInt() / 255f
                            val unpackedColor = Color(red, green, blue, alpha)
                            android.util.Log.d(
                                "ExerciseChartSection",
                                "Set $setIndex: ARGB from DB = 0x${argb.toString(16)}"
                            )
                            android.util.Log.d(
                                "ExerciseChartSection",
                                "  Float A=$alpha, R=$red, G=$green, B=$blue"
                            )
                            android.util.Log.d(
                                "ExerciseChartSection",
                                "  Unpacked Color = $unpackedColor"
                            )
                            unpackedColor
                        }

                        SetConfigChip(
                            setNumber = setIndex + 1,
                            color = color,
                            isVisible = isVisible,
                            onToggleVisibility = { onToggleSetVisibility(setIndex) },
                            onChangeColor = { newColor -> onSetColorChange(setIndex, newColor) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                android.util.Log.d(
                    "ExerciseChartSection",
                    "Render check - exerciseData.isEmpty: ${exerciseData.isEmpty()}, selectedExerciseId: $selectedExerciseId"
                )
                android.util.Log.d(
                    "ExerciseChartSection",
                    "exerciseData keys: ${exerciseData.keys}"
                )
                android.util.Log.d(
                    "ExerciseChartSection",
                    "exerciseData values: ${exerciseData.values}"
                )

                if (exerciseData.isEmpty() || selectedExerciseId == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                                text = "Нет данных об упражнении",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    android.util.Log.d("important", "=== Building visibleData ===")
                    android.util.Log.d("important", "exerciseData: $exerciseData")
                    android.util.Log.d("important", "friendExerciseData: $friendExerciseData")
                    android.util.Log.d(
                        "important",
                        "friends: ${friends.map { it.friendId to it.friendName }}"
                    )

                    // Combine user exercise data and friend exercise data
                    val visibleData: Map<String, List<Pair<Float, Float>>> = buildMap {
                        // Add user's exercise data
                        exerciseData
                            .filter { (setIndex, _) -> setIndex in visibleSets }
                            .forEach { (setIndex, points) ->
                                android.util.Log.d(
                                    "important",
                                    "Adding user set $setIndex with ${points.size} points"
                                )
                                put("Set ${setIndex + 1}", points)
                            }

                        // Add friends' exercise data
                        friendExerciseData.forEach { (friendId, points) ->
                            val friend = friends.find { it.friendId == friendId }
                            if (friend != null) {
                                android.util.Log.d(
                                    "important",
                                    "Adding friend ${friend.friendName} (${friendId}) with ${points.size} points"
                                )
                                put(friend.friendName, points)
                            } else {
                                android.util.Log.d(
                                    "important",
                                    "Friend $friendId found in friendExerciseData but not in friends list"
                                )
                            }
                        }
                    }

                    android.util.Log.d("important", "Final visibleData keys: ${visibleData.keys}")
                    android.util.Log.d("important", "Final visibleData size: ${visibleData.size}")
                    android.util.Log.d(
                        "important",
                        "Final visibleData values: ${visibleData.values.map { it.size }}"
                    )

                    if (visibleData.isNotEmpty() && visibleData.keys.isNotEmpty()) {
                        // Build map of series name to set index for color lookup
                        val seriesToSetIndex: Map<String, Int> = exerciseData
                            .filter { (setIndex, _) -> setIndex in visibleSets }
                            .keys
                            .associateBy { setIndex -> "Set ${setIndex + 1}" }

                        // Default colors for sets (deterministic by index) - same as in StatisticsStore
                        val defaultColors = listOf(
                            Color(0xFF6366F1), // Indigo
                            Color(0xFFEC4899), // Pink
                            Color(0xFF10B981), // Emerald
                            Color(0xFFF59E0B), // Amber
                            Color(0xFFEF4444), // Red
                            Color(0xFF8B5CF6), // Violet
                            Color(0xFF06B6D4), // Cyan
                            Color(0xFF84CC16), // Lime
                            Color(0xFFF97316), // Orange
                            Color(0xFF0EA5E9)  // Sky
                        )

                        val colors = visibleData.keys.associateWith { seriesName ->
                            // Check if this is a friend's data
                            val friend = friends.find { it.friendName == seriesName }
                            if (friend != null) {
                                // Use friend's configured color
                                val argb = friend.color
                                val alpha = ((argb shr 24) and 0xFF).toInt() / 255f
                                val red = ((argb shr 16) and 0xFF).toInt() / 255f
                                val green = ((argb shr 8) and 0xFF).toInt() / 255f
                                val blue = (argb and 0xFF).toInt() / 255f
                                val unpackedColor = Color(red, green, blue, alpha)
                                android.util.Log.d(
                                    "ExerciseChartSection",
                                    "Chart Friend $seriesName: ARGB from friendConfig = 0x${
                                        argb.toString(16)
                                    }"
                                )
                                android.util.Log.d(
                                    "ExerciseChartSection",
                                    "  Float A=$alpha, R=$red, G=$green, B=$blue"
                                )
                                android.util.Log.d(
                                    "ExerciseChartSection",
                                    "  Unpacked Color = $unpackedColor"
                                )
                                unpackedColor
                            } else {
                                // This is user's set data
                                val setIndex = seriesToSetIndex[seriesName] ?: 0
                                val color = if (setColors[setIndex] == null) {
                                    android.util.Log.d(
                                        "ExerciseChartSection",
                                        "Chart Set $setIndex: color from state is null, using default color"
                                    )
                                    val defaultColor = defaultColors.getOrElse(setIndex) {
                                        defaultColors[(setIndex) % defaultColors.size]
                                    }
                                    android.util.Log.d(
                                        "ExerciseChartSection",
                                        "Default color for set $setIndex: $defaultColor"
                                    )
                                    defaultColor
                                } else {
                                    val argb = setColors[setIndex]!!
                                    val alpha = ((argb shr 24) and 0xFF).toInt() / 255f
                                    val red = ((argb shr 16) and 0xFF).toInt() / 255f
                                    val green = ((argb shr 8) and 0xFF).toInt() / 255f
                                    val blue = (argb and 0xFF).toInt() / 255f
                                    val unpackedColor = Color(red, green, blue, alpha)
                                    android.util.Log.d(
                                        "ExerciseChartSection",
                                        "Chart Set $setIndex: ARGB from DB = 0x${argb.toString(16)}"
                                    )
                                    android.util.Log.d(
                                        "ExerciseChartSection",
                                        "  Float A=$alpha, R=$red, G=$green, B=$blue"
                                    )
                                    android.util.Log.d(
                                        "ExerciseChartSection",
                                        "  Unpacked Color = $unpackedColor"
                                    )
                                    unpackedColor
                                }
                                color
                            }
                        }

                        // Add key to force recomposition when data changes
                        android.util.Log.d(
                            "important",
                            "LineChartView key: ${
                                visibleData.keys.joinToString(",") + visibleData.values.map { it.size }
                                    .joinToString(",")
                            }"
                        )
                        android.util.Log.d(
                            "important",
                            "Passing to LineChartView: data keys=${visibleData.keys}, colors keys=${colors.keys}"
                        )
                        LineChartView(
                            data = visibleData,
                            seriesColors = colors,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Все подходы скрыты",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                                Text("Добавить")
                            }
                        }
                    }

                    if (friends.isEmpty()) {
                        Text(
                            text = "Нет друзей для сравнения",
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
                                // Unpack ARGB from Long for friend color - divide by 255
                                val argb = friend.color
                                val alpha = ((argb shr 24) and 0xFF).toInt() / 255f
                                val red = ((argb shr 16) and 0xFF).toInt() / 255f
                                val green = ((argb shr 8) and 0xFF).toInt() / 255f
                                val blue = (argb and 0xFF).toInt() / 255f
                                val friendColor = Color(red, green, blue, alpha)

                                FriendChip(
                                    name = friend.friendName,
                                    color = friendColor,
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
    @Composable
    fun SetConfigChip(
        setNumber: Int,
        color: Color,
        isVisible: Boolean,
        onToggleVisibility: () -> Unit,
        onChangeColor: (Color) -> Unit
    ) {
        var showColorPicker by remember { mutableStateOf(false) }
        val availableColors = remember {
            listOf(
                Color(0xFF6366F1), // Indigo
                Color(0xFFEC4899), // Pink
                Color(0xFF10B981), // Emerald
                Color(0xFFF59E0B), // Amber
                Color(0xFFEF4444), // Red
                Color(0xFF8B5CF6), // Violet
                Color(0xFF06B6D4), // Cyan
                Color(0xFF84CC16)  // Lime
            )
        }

        android.util.Log.d("SetConfigChip", "=== Rendered ===")
        android.util.Log.d("SetConfigChip", "Set number: $setNumber")
        android.util.Log.d("SetConfigChip", "Input color: $color")
        android.util.Log.d(
            "SetConfigChip",
            "  Float A=${color.alpha}, R=${color.red}, G=${color.green}, B=${color.blue}"
        )
        android.util.Log.d(
            "SetConfigChip",
            "  Int A=${(color.alpha * 255).toInt()}, R=${(color.red * 255).toInt()}, G=${(color.green * 255).toInt()}, B=${(color.blue * 255).toInt()}"
        )
        android.util.Log.d(
            "SetConfigChip",
            "  Color value (Long): 0x${color.value.toLong().toString(16)}"
        )

        Box {
            Row(
                modifier = Modifier
                    .background(
                        color = color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = if (isVisible) 2.dp else 1.dp,
                        color = color,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable { showColorPicker = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Set $setNumber",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = color
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        painter = if (isVisible) painterResource(R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24) else painterResource(
                            R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                        ),
                        contentDescription = if (isVisible) "Hide set" else "Show set",
                        tint = color,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = showColorPicker,
                onDismissRequest = { showColorPicker = false }
            ) {
                Text(
                    text = "Выберите цвет",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
                availableColors.forEach { c ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(c, CircleShape)
                                )
                            }
                        },
                        onClick = {
                            android.util.Log.d(
                                "SetConfigChip",
                                "=== Color selected from dropdown ==="
                            )
                            android.util.Log.d("SetConfigChip", "Set: $setNumber")
                            android.util.Log.d("SetConfigChip", "Selected color: $c")
                            android.util.Log.d(
                                "SetConfigChip",
                                "  A=${c.alpha}, R=${c.red}, G=${c.green}, B=${c.blue}"
                            )
                            android.util.Log.d(
                                "SetConfigChip",
                                "  Color value (Long): 0x${c.value.toLong().toString(16)}"
                            )
                            onChangeColor(c)
                            showColorPicker = false
                        }
                    )
                }
            }
        }
    }

data class ExerciseInfo(
    val id: String,
    val name: String
)
