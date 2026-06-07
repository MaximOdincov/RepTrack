package com.example.reptrack.presentation.statistics.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.presentation.statistics.utils.colorToArgb
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.domain.friends.usecases.GetFriendsUseCase
import com.example.reptrack.domain.statistics.entities.DateRange
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.presentation.statistics.components.ExerciseChartSection
import com.example.reptrack.presentation.statistics.components.ExerciseInfo
import com.example.reptrack.presentation.statistics.components.MuscleGroupChartSection
import com.example.reptrack.presentation.statistics.components.WeightChartSection
import com.example.reptrack.presentation.statistics.components.dialogs.AddFriendDialog as FriendPickerDialog
import com.example.reptrack.presentation.statistics.components.dialogs.FriendExerciseErrorDialog
import com.example.reptrack.presentation.statistics.stores.StatisticsStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun StatisticsScreen(
    store: StatisticsStore,
    getFriendsUseCase: GetFriendsUseCase,
    exercises: List<Exercise>,
    friends: List<Friend>,
    isGuest: Boolean = false,
    modifier: Modifier = Modifier
) {
    val state: StatisticsStore.State = store.states.collectAsState(StatisticsStore.State()).value

    // Load friends locally in StatisticsScreen
    var availableFriends by remember { mutableStateOf<List<Friend>>(emptyList()) }

    LaunchedEffect(Unit) {
        android.util.Log.d("important", "=== Loading friends in StatisticsScreen ===")
        getFriendsUseCase().catch { e ->
            android.util.Log.e("important", "❌ ERROR loading friends: ${e.message}")
        }.collect { friends ->
            android.util.Log.d("important", "✅ Friends loaded in StatisticsScreen: ${friends.size}")
            android.util.Log.d("important", "Friends: ${friends.map { it.friendUserId to (it.username ?: "Unknown") }}")
            availableFriends = friends
        }
    }

    // Log friends when received
    LaunchedEffect(availableFriends) {
        android.util.Log.d("important", "=== Friends state changed in StatisticsScreen ===")
        android.util.Log.d("important", "availableFriends size: ${availableFriends.size}")
        android.util.Log.d("important", "availableFriends: ${availableFriends.map { it.friendUserId to (it.username ?: "Unknown") }}")
    }

    // Log state changes for debugging
    LaunchedEffect(state.selectedExerciseId, state.exerciseData, state.setColors) {
        android.util.Log.d("StatisticsScreen", "=== State changed ===")
        android.util.Log.d("StatisticsScreen", "selectedExerciseId: ${state.selectedExerciseId}")
        android.util.Log.d("StatisticsScreen", "exerciseData keys: ${state.exerciseData.keys}")
        android.util.Log.d("StatisticsScreen", "exerciseData size: ${state.exerciseData.size}")
        android.util.Log.d("StatisticsScreen", "visibleSets: ${state.visibleSets}")
        android.util.Log.d("StatisticsScreen", "setColors: ${state.setColors}")
        state.setColors.forEach { (setIndex, argb) ->
            val alpha = ((argb shr 24) and 0xFF).toInt() / 255f
            val red = ((argb shr 16) and 0xFF).toInt() / 255f
            val green = ((argb shr 8) and 0xFF).toInt() / 255f
            val blue = (argb and 0xFF).toInt() / 255f
            android.util.Log.d("StatisticsScreen", "  Set $setIndex: ARGB=0x${argb.toString(16)}, Float A=$alpha, R=$red, G=$green, B=$blue")
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })

    // Handle labels (for friend error dialogs)
    var showFriendExerciseError by remember { mutableStateOf(false) }
    var friendErrorText by remember { mutableStateOf("") }

    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                is StatisticsStore.Label.ShowFriendExerciseError -> {
                    friendErrorText = label.message
                    showFriendExerciseError = true
                }
            }
        }
    }

    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text("Вес") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("Упражнения") }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    text = { Text("Мышцы") }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> WeightTab(store, state, availableFriends, isGuest)
                    1 -> ExerciseTab(store, state, exercises, availableFriends, isGuest)
                    2 -> MuscleTab(store, state, availableFriends, getFriendsUseCase, isGuest)
                }
            }
        }
    }

    // Friend exercise error dialog - показываем только на exercise экране
    if (showFriendExerciseError && pagerState.currentPage == 1) { // 1 = Exercise tab
        FriendExerciseErrorDialog(
            message = friendErrorText,
            onDismiss = { showFriendExerciseError = false }
        )
    }
}

@Composable
private fun WeightTab(
    store: StatisticsStore,
    state: StatisticsStore.State,
    friends: List<Friend>,
    isGuest: Boolean
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showDateRangeDialog by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf(state.dateRange) }
    var dateRangeText by remember { mutableStateOf(getDateRangeText(selectedDateRange)) }

    // Initialize data loading when the tab is first shown
    LaunchedEffect(Unit) {
        store.accept(StatisticsStore.Intent.LoadData)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Debug logging for weight data
            android.util.Log.d("StatisticsScreen", "=== Weight Data ===")
            android.util.Log.d("StatisticsScreen", "currentWeight: ${state.currentWeight}")
            android.util.Log.d("StatisticsScreen", "weightData size: ${state.weightData.size}")
            state.weightData.forEach { dataPoint ->
                android.util.Log.d("StatisticsScreen", "Local weight: date=${dataPoint.date}, value=${dataPoint.value}")
            }

            // Log weight data for debugging
            if (state.weightData.isNotEmpty()) {
                val latestWeight = state.weightData.last()
                android.util.Log.d("StatisticsScreen", "Latest weight from DB: date=${latestWeight.date}, value=${latestWeight.value}")
                // Compare with currentWeight
                android.util.Log.d("StatisticsScreen", "Current weight from state: ${state.currentWeight}")
                android.util.Log.d("StatisticsScreen", "Difference: ${state.currentWeight?.minus(latestWeight.value)}")
            } else {
                android.util.Log.d("StatisticsScreen", "WARNING: No weight data found!")
            }

            state.friendWeightData.forEach { (friendId, dataPoints) ->
                android.util.Log.d("StatisticsScreen", "Friend $friendId weight data:")
                dataPoints.forEach { dataPoint ->
                    android.util.Log.d("StatisticsScreen", "  date=${dataPoint.date}, value=${dataPoint.value}")
                }
            }

            WeightChartSection(
                currentWeight = state.currentWeight,
                weightData = state.weightData.map {
                    Pair(
                        it.date.toEpochDay().toFloat(),
                        it.value
                    )
                }.also { mappedData ->
                    android.util.Log.d("StatisticsScreen", "Mapped weight data:")
                    mappedData.forEach { (x, y) ->
                        android.util.Log.d("StatisticsScreen", "  x=$x (epoch day), y=$y")
                    }
                },
                friendWeightData = state.friendWeightData.mapValues { entry ->
                    entry.value.map { data ->
                        Pair(
                            data.date.toEpochDay().toFloat(),
                            data.value
                        )
                    }
                },
                friends = state.friends,
                dateRange = dateRangeText,
                onWeightSave = { weight ->
                    android.util.Log.d("StatisticsScreen", "Saving weight: $weight")
                    store.accept(StatisticsStore.Intent.UpdateWeight(weight))
                },
                onAddFriend = { if (!isGuest) showAddFriendDialog = true },
                onRemoveFriend = { friendId ->
                    store.accept(StatisticsStore.Intent.RemoveFriend(friendId))
                },
                onFriendColorChange = { friendId, newColor ->
                    // Convert Color to ARGB Long format using utility function
                    val argb = colorToArgb(newColor)
                    store.accept(StatisticsStore.Intent.ChangeFriendColor(friendId, argb))
                },
                onChangeDateRange = { showDateRangeDialog = true },
                isGuest = isGuest
            )
        }
    }

    if (showAddFriendDialog) {
        FriendPickerDialog(
            availableFriends = friends,
            addedFriends = state.friends.map { it.friendId },
            onDismiss = { showAddFriendDialog = false },
            onAddFriend = { friendId, friendName ->
                store.accept(StatisticsStore.Intent.AddFriend(friendId, friendName))
            }
        )
    }

    if (showDateRangeDialog) {
        DateRangeDialog(
            selectedRange = selectedDateRange,
            onRangeSelected = { range ->
                selectedDateRange = range
                dateRangeText = getDateRangeText(range)
                store.accept(StatisticsStore.Intent.ChangeDateRange(range.from, range.to))
            },
            onDismiss = { showDateRangeDialog = false }
        )
    }
}

@Composable
private fun ExerciseTab(
    store: StatisticsStore,
    state: StatisticsStore.State,
    exercises: List<Exercise>,
    friends: List<Friend>,
    isGuest: Boolean
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showDateRangeDialog by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf(state.dateRange) }
    var dateRangeText by remember { mutableStateOf(getDateRangeText(selectedDateRange)) }

    // Initialize data loading when the tab is first shown
    LaunchedEffect(Unit) {
        store.accept(StatisticsStore.Intent.LoadData)
    }

    val exerciseInfoList = exercises
        .sortedBy { it.name }
        .map { ExerciseInfo(it.id, it.name) }

    // Select first exercise if none selected and exercises are available
    LaunchedEffect(exercises, state.selectedExerciseId) {
        if (state.selectedExerciseId == null && exercises.isNotEmpty()) {
            val firstExercise = exercises.sortedBy { it.name }.first()
            android.util.Log.d("StatisticsScreen", "Auto-selecting first exercise: ${firstExercise.id}")
            store.accept(StatisticsStore.Intent.SelectExercise(firstExercise.id))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ExerciseChartSection(
                key = state.dateRange.from.toString() + state.dateRange.to.toString(),
                selectedExerciseId = state.selectedExerciseId,
                exercises = exerciseInfoList,
                exerciseData = state.exerciseData.mapValues { entry ->
                    android.util.Log.d("StatisticsScreen", "Mapping exercise data for setIndex=${entry.key}")
                    android.util.Log.d("StatisticsScreen", "Set ${entry.key} has ${entry.value.size} points")
                    entry.value.map { point ->
                        Pair(
                            point.date.toEpochDay().toFloat(),
                            point.value
                        )
                    }
                }.also {
                    android.util.Log.d("StatisticsScreen", "Final exerciseData for UI: keys=${it.keys}, size=${it.size}")
                },
                visibleSets = state.visibleSets,
                setColors = state.setColors,
                friends = state.friends,
                friendExerciseData = state.friendExerciseData.mapValues { entry ->
                    android.util.Log.d("StatisticsScreen", "Mapping friend exercise data for friendId=${entry.key}")
                    android.util.Log.d("StatisticsScreen", "Friend ${entry.key} has ${entry.value.size} points")
                    entry.value.map { point ->
                        Pair(
                            point.date.toEpochDay().toFloat(),
                            point.value
                        )
                    }
                }.also {
                    android.util.Log.d("StatisticsScreen", "Final friendExerciseData for UI: keys=${it.keys}, size=${it.size}")
                },
                dateRange = dateRangeText,
                onExerciseSelect = { exerciseId ->
                    android.util.Log.d("StatisticsScreen", "onExerciseSelect called with exerciseId: $exerciseId")
                    store.accept(StatisticsStore.Intent.SelectExercise(exerciseId))
                },
                onToggleSetVisibility = { setIndex ->
                    store.accept(StatisticsStore.Intent.ToggleSetVisibility(setIndex))
                },
                onSetColorChange = { setIndex, color ->
                    // Convert Color to Long using utility function
                    val argb = colorToArgb(color)
                    android.util.Log.d("StatisticsScreen", "=== onSetColorChange called ===")
                    android.util.Log.d("StatisticsScreen", "Set index: $setIndex")
                    android.util.Log.d("StatisticsScreen", "Input Color: $color")
                    android.util.Log.d("StatisticsScreen", "Packed ARGB Long: 0x${argb.toString(16)}")
                    android.util.Log.d("StatisticsScreen", "  Current setColors: ${state.setColors}")
                    store.accept(StatisticsStore.Intent.ChangeSetLineColor(setIndex, argb))
                },
                onAddFriend = { if (!isGuest) showAddFriendDialog = true },
                onRemoveFriend = { friendId ->
                    store.accept(StatisticsStore.Intent.RemoveFriend(friendId))
                },
                onFriendColorChange = { friendId, color ->
                    // Convert Color to Long by packing ARGB components
                    val argb = ((color.alpha * 255).toInt().toLong() shl 24) or
                               ((color.red * 255).toInt().toLong() shl 16) or
                               ((color.green * 255).toInt().toLong() shl 8) or
                               (color.blue * 255).toInt().toLong()
                    android.util.Log.d("StatisticsScreen", "=== onFriendColorChange called ===")
                    android.util.Log.d("StatisticsScreen", "Friend ID: $friendId")
                    android.util.Log.d("StatisticsScreen", "Input Color: $color")
                    android.util.Log.d("StatisticsScreen", "Packed ARGB Long: 0x${argb.toString(16)}")
                    store.accept(StatisticsStore.Intent.ChangeFriendColor(friendId, argb))
                },
                onChangeDateRange = { showDateRangeDialog = true },
                isGuest = isGuest
            )
        }
    }

    if (showAddFriendDialog) {
        FriendPickerDialog(
            availableFriends = friends,
            addedFriends = state.friends.map { it.friendId },
            onDismiss = { showAddFriendDialog = false },
            onAddFriend = { friendId, friendName ->
                store.accept(StatisticsStore.Intent.AddFriend(friendId, friendName))
            }
        )
    }

    if (showDateRangeDialog) {
        DateRangeDialog(
            selectedRange = selectedDateRange,
            onRangeSelected = { range ->
                selectedDateRange = range
                dateRangeText = getDateRangeText(range)
                store.accept(StatisticsStore.Intent.ChangeDateRange(range.from, range.to))
            },
            onDismiss = { showDateRangeDialog = false }
        )
    }
}

@Composable
private fun MuscleTab(
    store: StatisticsStore,
    state: StatisticsStore.State,
    friends: List<Friend>,
    getFriendsUseCase: GetFriendsUseCase,
    isGuest: Boolean
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showDateRangeDialog by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf(state.dateRange) }
    var dateRangeText by remember { mutableStateOf(getDateRangeText(selectedDateRange)) }

    // Initialize data loading when the tab is first shown
    LaunchedEffect(Unit) {
        store.accept(StatisticsStore.Intent.LoadData)
    }

    // Добавляем логирование для диагностики muscle данных
    LaunchedEffect(state.muscleGroupData, state.friendMuscleGroupData) {
        android.util.Log.d("MuscleTab", "=== Muscle Data Updated ===")
        android.util.Log.d("MuscleTab", "User muscle data size: ${state.muscleGroupData.size}")
        state.muscleGroupData.forEach { data ->
            android.util.Log.d("MuscleTab", "  ${data.muscleGroup}: ${data.frequency}")
        }

        android.util.Log.d("MuscleTab", "Friend muscle data size: ${state.friendMuscleGroupData.size}")
        state.friendMuscleGroupData.forEach { (friendId, data) ->
            android.util.Log.d("MuscleTab", "  Friend $friendId: ${data.size} items")
            data.forEach { muscleData ->
                android.util.Log.d("MuscleTab", "    ${muscleData.muscleGroup}: ${muscleData.frequency}")
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MuscleGroupChartSection(
                muscleGroupData = state.muscleGroupData,
                friends = state.friends,
                friendMuscleData = state.friendMuscleGroupData,
                dateRange = dateRangeText,
                userColor = state.userColor,
                isLoading = state.isLoading,
                onAddFriend = { if (!isGuest) showAddFriendDialog = true },
                onRemoveFriend = { friendId ->
                    store.accept(StatisticsStore.Intent.RemoveFriend(friendId))
                },
                onFriendColorChange = { friendId, newColor ->
                    // Convert Color to ARGB Long format using utility function
                    val argb = colorToArgb(newColor)
                    store.accept(StatisticsStore.Intent.ChangeFriendColor(friendId, argb))
                },
                onUserColorChange = { newColor ->
                    // Convert Color to ARGB Long format using utility function
                    val argb = colorToArgb(newColor)
                    store.accept(StatisticsStore.Intent.ChangeUserColor(argb))
                },
                onChangeDateRange = { showDateRangeDialog = true },
                isGuest = isGuest
            )
        }
    }

    if (showAddFriendDialog) {
        FriendPickerDialog(
            availableFriends = friends,
            addedFriends = state.friends.map { it.friendId },
            onDismiss = { showAddFriendDialog = false },
            onAddFriend = { friendId, friendName ->
                store.accept(StatisticsStore.Intent.AddFriend(friendId, friendName))
            }
        )
    }

    if (showDateRangeDialog) {
        DateRangeDialog(
            selectedRange = selectedDateRange,
            onRangeSelected = { range ->
                selectedDateRange = range
                dateRangeText = getDateRangeText(range)
                store.accept(StatisticsStore.Intent.ChangeDateRange(range.from, range.to))
            },
            onDismiss = { showDateRangeDialog = false }
        )
    }
}

@Composable
private fun DateRangeDialog(
    selectedRange: DateRange,
    onRangeSelected: (DateRange) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите период") },
        text = {
            Column {
                val ranges = remember {
                    listOf(
                        DateRange.last7Days() to "Последние 7 дней",
                        DateRange.last30Days() to "Последние 30 дней",
                        DateRange.last3Months() to "Последние 3 месяца",
                        DateRange.lastYear() to "Последний год",
                        DateRange.allTime() to "Все время"
                    )
                }

                ranges.forEach { (range, label) ->
                    OutlinedButton(
                        onClick = {
                            onRangeSelected(range)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

private fun getDateRangeText(range: DateRange): String {
    val now = java.time.LocalDateTime.now()

        return when {
            range.from.isAfter(now.minusDays(8)) -> "Последние 7 дней"
            range.from.isAfter(now.minusDays(31)) -> "Последние 30 дней"
            range.from.isAfter(now.minusMonths(4)) -> "Последние 3 месяца"
            range.from.isAfter(now.minusYears(2)) -> "Последний год"
            else -> "Все время"
        }
}