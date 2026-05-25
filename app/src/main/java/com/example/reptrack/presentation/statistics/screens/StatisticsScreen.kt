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
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.domain.friends.usecases.GetFriendsUseCase
import com.example.reptrack.domain.statistics.entities.DateRange
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.presentation.statistics.components.ExerciseChartSection
import com.example.reptrack.presentation.statistics.components.ExerciseInfo
import com.example.reptrack.presentation.statistics.components.MuscleGroupChartSection
import com.example.reptrack.presentation.statistics.components.WeightChartSection
import com.example.reptrack.presentation.statistics.components.dialogs.AddFriendDialog
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

    // Load data on first composition
    LaunchedEffect(Unit) {
        android.util.Log.d("StatisticsScreen", "=== LaunchedEffect for LoadData triggered ===")
        store.accept(StatisticsStore.Intent.LoadData)
        android.util.Log.d("StatisticsScreen", "LoadData intent sent")
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
                    text = { Text("Weight") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("Exercises") }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    text = { Text("Muscles") }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> WeightTab(store, state, availableFriends)
                    1 -> ExerciseTab(store, state, exercises, availableFriends)
                    2 -> MuscleTab(store, state, availableFriends, getFriendsUseCase)
                }
            }
        }
    }

    // Friend exercise error dialog
    if (showFriendExerciseError) {
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
    friends: List<Friend>
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showDateRangeDialog by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf(state.dateRange) }
    var dateRangeText by remember { mutableStateOf(getDateRangeText(selectedDateRange)) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WeightChartSection(
                currentWeight = state.currentWeight,
                weightData = state.weightData.map {
                    Pair(
                        it.date.toEpochDay().toFloat(),
                        it.value
                    )
                },
                friends = state.weightFriends,
                dateRange = dateRangeText,
                onWeightChange = { weight ->
                    store.accept(StatisticsStore.Intent.UpdateWeight(weight))
                },
                onAddFriend = { showAddFriendDialog = true },
                onRemoveFriend = { friendId ->
                    store.accept(StatisticsStore.Intent.RemoveFriendFromChart(
                        com.example.reptrack.domain.statistics.entities.ChartType.WEIGHT_LINE,
                        friendId
                    ))
                },
                onChangeDateRange = { showDateRangeDialog = true }
            )
        }
    }

    if (showAddFriendDialog) {
        AddFriendDialog(
            availableFriends = friends,
            addedFriends = state.weightFriends.map { it.friendId },
            onDismiss = { showAddFriendDialog = false },
            onAddFriend = { friendId, color ->
                store.accept(StatisticsStore.Intent.AddFriendToChart(
                    com.example.reptrack.domain.statistics.entities.ChartType.WEIGHT_LINE,
                    friendId,
                    color
                ))
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
    friends: List<Friend>
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showDateRangeDialog by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf(state.dateRange) }
    var dateRangeText by remember { mutableStateOf(getDateRangeText(selectedDateRange)) }

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
                friends = state.exerciseFriends,
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
                    // Convert Color to Long by packing ARGB components
                    // Compose Color uses Float (0.0-1.0), need to multiply by 255 for Int (0-255)
                    val argb = ((color.alpha * 255).toInt().toLong() shl 24) or
                               ((color.red * 255).toInt().toLong() shl 16) or
                               ((color.green * 255).toInt().toLong() shl 8) or
                               (color.blue * 255).toInt().toLong()
                    android.util.Log.d("StatisticsScreen", "=== onSetColorChange called ===")
                    android.util.Log.d("StatisticsScreen", "Set index: $setIndex")
                    android.util.Log.d("StatisticsScreen", "Input Color: $color")
                    android.util.Log.d("StatisticsScreen", "  A=${color.alpha}, R=${color.red}, G=${color.green}, B=${color.blue}")
                    android.util.Log.d("StatisticsScreen", "  After *255: A=${(color.alpha * 255).toInt()}, R=${(color.red * 255).toInt()}, G=${(color.green * 255).toInt()}, B=${(color.blue * 255).toInt()}")
                    android.util.Log.d("StatisticsScreen", "Packed ARGB Long: 0x${argb.toString(16)}")
                    android.util.Log.d("StatisticsScreen", "  Current setColors: ${state.setColors}")
                    store.accept(StatisticsStore.Intent.ChangeSetLineColor(setIndex, argb))
                },
                onAddFriend = { showAddFriendDialog = true },
                onRemoveFriend = { friendId ->
                    store.accept(StatisticsStore.Intent.RemoveFriendFromChart(
                        com.example.reptrack.domain.statistics.entities.ChartType.EXERCISE_LINE,
                        friendId
                    ))
                },
                onChangeDateRange = { showDateRangeDialog = true }
            )
        }
    }

    if (showAddFriendDialog) {
        AddFriendDialog(
            availableFriends = friends,
            addedFriends = state.exerciseFriends.map { it.friendId },
            onDismiss = { showAddFriendDialog = false },
            onAddFriend = { friendId, color ->
                store.accept(StatisticsStore.Intent.AddFriendToChart(
                    com.example.reptrack.domain.statistics.entities.ChartType.EXERCISE_LINE,
                    friendId,
                    color
                ))
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
    getFriendsUseCase: GetFriendsUseCase
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showDateRangeDialog by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf(state.dateRange) }
    var dateRangeText by remember { mutableStateOf(getDateRangeText(selectedDateRange)) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MuscleGroupChartSection(
                muscleGroupData = state.muscleGroupData,
                friends = state.muscleGroupFriends,
                friendMuscleData = emptyMap(), // TODO: Load friend muscle data
                dateRange = dateRangeText,
                onAddFriend = { showAddFriendDialog = true },
                onRemoveFriend = { friendId ->
                    store.accept(StatisticsStore.Intent.RemoveFriendFromChart(
                        com.example.reptrack.domain.statistics.entities.ChartType.SPIDER,
                        friendId
                    ))
                },
                onChangeDateRange = { showDateRangeDialog = true }
            )
        }
    }

    if (showAddFriendDialog) {
        AddFriendDialog(
            availableFriends = friends,
            addedFriends = state.muscleGroupFriends.map { it.friendId },
            onDismiss = { showAddFriendDialog = false },
            onAddFriend = { friendId, color ->
                store.accept(StatisticsStore.Intent.AddFriendToChart(
                    com.example.reptrack.domain.statistics.entities.ChartType.SPIDER,
                    friendId,
                    color
                ))
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
        title = { Text("Select Date Range") },
        text = {
            Column {
                val ranges = remember {
                    listOf(
                        DateRange.last7Days() to "Last 7 days",
                        DateRange.last30Days() to "Last 30 days",
                        DateRange.last3Months() to "Last 3 months",
                        DateRange.lastYear() to "Last year",
                        DateRange.allTime() to "All time"
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
                Text("Cancel")
            }
        }
    )
}

private fun getDateRangeText(range: DateRange): String {
    val now = java.time.LocalDateTime.now()

    return when {
        range.from.isAfter(now.minusDays(8)) -> "Last 7 days"
        range.from.isAfter(now.minusDays(31)) -> "Last 30 days"
        range.from.isAfter(now.minusMonths(4)) -> "Last 3 months"
        range.from.isAfter(now.minusYears(2)) -> "Last year"
        else -> "All time"
    }
}