package com.example.reptrack.presentation.statistics.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.statistics.entities.*
import com.example.reptrack.domain.statistics.usecases.*
import com.example.reptrack.domain.friends.usecases.GetFriendsUseCase
import com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime

interface StatisticsStore : Store<StatisticsStore.Intent, StatisticsStore.State, StatisticsStore.Label> {

    sealed class Intent {
        object LoadData : Intent()
        data class ChangeDateRange(val from: LocalDateTime, val to: LocalDateTime) : Intent()
        data class UpdateWeight(val value: Float) : Intent()
        data class SelectExercise(val exerciseId: String) : Intent()
        data class ToggleSetVisibility(val setIndex: Int) : Intent()
        data class ChangeSetLineColor(val setIndex: Int, val color: Long) : Intent()
        data class AddFriendToChart(val chartType: ChartType, val friendId: String, val color: Long) : Intent()
        data class RemoveFriendFromChart(val chartType: ChartType, val friendId: String) : Intent()
        data class ChangeFriendColor(val chartType: ChartType, val friendId: String, val color: Long) : Intent()
    }

    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val dateRange: DateRange = DateRange.last30Days(),
        // Weight
        val weightData: List<WeightDataPoint> = emptyList(),
        val currentWeight: Float? = null,
        val weightFriends: List<FriendConfig> = emptyList(),
        // Exercise
        val selectedExerciseId: String? = null,
        val exerciseData: Map<Int, List<ExerciseDataPoint>> = emptyMap(),
        val visibleSets: Set<Int> = emptySet(),
        val setColors: Map<Int, Long> = emptyMap(),
        val exerciseFriends: List<FriendConfig> = emptyList(),
        // Friend exercise data - map of friendId to list of exercise data points
        val friendExerciseData: Map<String, List<ExerciseDataPoint>> = emptyMap(),
        // Muscle Groups
        val muscleGroupData: List<MuscleGroupDataPoint> = emptyList(),
        val muscleGroupFriends: List<FriendConfig> = emptyList(),
        // User info
        val userId: String? = null,
        val userName: String? = null
    )

    sealed class Label {
        data class ShowFriendExerciseError(val message: String) : Label()
    }
}

internal class StatisticsStoreFactory(
    private val storeFactory: StoreFactory,
    private val getWeightChartDataUseCase: GetWeightChartDataUseCase,
    private val getFriendWeightChartDataUseCase: GetFriendWeightChartDataUseCase,
    private val updateWeightUseCase: UpdateWeightUseCase,
    private val getExerciseChartDataUseCase: GetExerciseChartDataUseCase,
    private val getFriendExerciseChartDataUseCase: GetFriendExerciseChartDataUseCase,
    private val getMuscleGroupChartDataUseCase: GetMuscleGroupChartDataUseCase,
    private val getFriendMuscleGroupChartDataUseCase: GetFriendMuscleGroupChartDataUseCase,
    private val friendHasExerciseUseCase: FriendHasExerciseUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase
) {

    fun create(): StatisticsStore =
        object : StatisticsStore, Store<StatisticsStore.Intent, StatisticsStore.State, StatisticsStore.Label> by storeFactory.create(
            name = "StatisticsStore",
            initialState = StatisticsStore.State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        object Loading : Msg
        data class Error(val error: String) : Msg
        data class UserLoaded(val userId: String, val userName: String) : Msg
        data class DateRangeChanged(val dateRange: DateRange) : Msg
        data class WeightDataLoaded(val data: List<WeightDataPoint>) : Msg
        data class FriendWeightDataLoaded(val data: List<WeightDataPoint>) : Msg
        data class CurrentWeightUpdated(val weight: Float) : Msg
        data class FriendWeightAdded(val friendConfig: FriendConfig) : Msg
        data class FriendWeightRemoved(val friendId: String) : Msg
        data class FriendWeightColorChanged(val friendId: String, val color: Long) : Msg
        data class ExerciseSelected(val exerciseId: String) : Msg
        data class ExerciseDataLoaded(val data: Map<Int, List<ExerciseDataPoint>>) : Msg
        data class SetVisibilityToggled(val setIndex: Int) : Msg
        data class SetLineColorChanged(val setIndex: Int, val color: Long) : Msg
        data class FriendExerciseAdded(val friendConfig: FriendConfig) : Msg
        data class FriendExerciseRemoved(val friendId: String) : Msg
        data class FriendExerciseDataLoaded(val friendId: String, val data: List<ExerciseDataPoint>) : Msg
        data class MuscleGroupDataLoaded(val data: List<MuscleGroupDataPoint>) : Msg
        data class FriendMuscleGroupAdded(val friendConfig: FriendConfig) : Msg
        data class FriendMuscleGroupRemoved(val friendId: String) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<StatisticsStore.Intent, Nothing, StatisticsStore.State, Msg, StatisticsStore.Label>() {
        override fun executeIntent(intent: StatisticsStore.Intent, getState: () -> StatisticsStore.State) {
            android.util.Log.d("StatisticsStore", "=== executeIntent called ===")
            android.util.Log.d("StatisticsStore", "Intent: $intent")
            when (intent) {
                StatisticsStore.Intent.LoadData -> loadData(getState())
                is StatisticsStore.Intent.ChangeDateRange -> changeDateRange(intent.from, intent.to, getState())
                is StatisticsStore.Intent.UpdateWeight -> updateWeight(intent.value, getState())
                is StatisticsStore.Intent.SelectExercise -> selectExercise(intent.exerciseId, getState())
                is StatisticsStore.Intent.ToggleSetVisibility -> dispatch(Msg.SetVisibilityToggled(intent.setIndex))
                is StatisticsStore.Intent.ChangeSetLineColor -> dispatch(Msg.SetLineColorChanged(intent.setIndex, intent.color))
                is StatisticsStore.Intent.AddFriendToChart -> addFriendToChart(intent.chartType, intent.friendId, intent.color, getState())
                is StatisticsStore.Intent.RemoveFriendFromChart -> removeFriendFromChart(intent.chartType, intent.friendId, getState())
                is StatisticsStore.Intent.ChangeFriendColor -> changeFriendColor(intent.chartType, intent.friendId, intent.color, getState())
            }
        }

        private fun loadData(state: StatisticsStore.State) {
            android.util.Log.d("important", "========================================")
            android.util.Log.d("important", "=== loadData called ===")
            android.util.Log.d("important", "========================================")
            android.util.Log.d("important", "userId in state: ${state.userId}")
            android.util.Log.d("important", "selectedExerciseId in state: ${state.selectedExerciseId}")
            android.util.Log.d("important", "dateRange in state: ${state.dateRange.from} to ${state.dateRange.to}")
            android.util.Log.d("important", "exerciseFriends in state: ${state.exerciseFriends}")

            scope.launch {
                if (state.userId == null) {
                    try {
                        getCurrentUserProfileUseCase().collect { user ->
                            if (user != null) {
                                dispatch(Msg.UserLoaded(user.id, user.username ?: "You"))
                                // After loading user, load the data
                                dispatch(Msg.Loading)

                                getWeightChartDataUseCase(user.id, state.dateRange.from, state.dateRange.to)
                                    .catch { e -> dispatch(Msg.Error(e.message ?: "Failed to load weight data")) }
                                    .collect { data -> dispatch(Msg.WeightDataLoaded(data)) }

                                getMuscleGroupChartDataUseCase(user.id, state.dateRange.from, state.dateRange.to)
                                    .catch { e -> dispatch(Msg.Error(e.message ?: "Failed to load muscle group data")) }
                                    .collect { data -> dispatch(Msg.MuscleGroupDataLoaded(data)) }

                                state.selectedExerciseId?.let { exerciseId ->
                                    loadExerciseData(exerciseId, state, user.id, user.username ?: "You")
                                }

                                return@collect
                            }
                        }
                    } catch (e: Exception) {
                        dispatch(Msg.Error(e.message ?: "Failed to load user"))
                        return@launch
                    }
                }

                dispatch(Msg.Loading)

                val userId = state.userId ?: return@launch
                android.util.Log.d("important", "=== Starting data loading ===")
                android.util.Log.d("important", "User ID: $userId")
                android.util.Log.d("important", "selectedExerciseId: ${state.selectedExerciseId}")
                android.util.Log.d("important", "dateRange: ${state.dateRange.from} to ${state.dateRange.to}")

                // Load weight data in separate coroutine
                android.util.Log.d("important", "📊 Launching weight data collection...")
                scope.launch {
                    try {
                        getWeightChartDataUseCase(userId, state.dateRange.from, state.dateRange.to)
                            .catch { e ->
                                android.util.Log.e("important", "❌ ERROR loading weight data: ${e.message}")
                                dispatch(Msg.Error(e.message ?: "Failed to load weight data"))
                            }
                            .collect { data -> dispatch(Msg.WeightDataLoaded(data)) }
                    } catch (e: Exception) {
                        android.util.Log.e("important", "❌ EXCEPTION in weight data: ${e.message}")
                    }
                }

                // Load muscle group data in separate coroutine
                android.util.Log.d("important", "💪 Launching muscle group data collection...")
                scope.launch {
                    try {
                        getMuscleGroupChartDataUseCase(userId, state.dateRange.from, state.dateRange.to)
                            .catch { e ->
                                android.util.Log.e("important", "❌ ERROR loading muscle group data: ${e.message}")
                                dispatch(Msg.Error(e.message ?: "Failed to load muscle group data"))
                            }
                            .collect { data -> dispatch(Msg.MuscleGroupDataLoaded(data)) }
                    } catch (e: Exception) {
                        android.util.Log.e("important", "❌ EXCEPTION in muscle group data: ${e.message}")
                    }
                }

                // Load exercise data if selected (in same coroutine to ensure it happens)
                android.util.Log.d("important", "=== Checking for exercise data ===")
                android.util.Log.d("important", "selectedExerciseId: ${state.selectedExerciseId}")
                state.selectedExerciseId?.let { exerciseId ->
                    android.util.Log.d("important", "✓ Found selectedExerciseId: $exerciseId")
                    android.util.Log.d("important", "Calling loadExerciseData...")
                    loadExerciseData(exerciseId, state, userId, state.userName ?: "You")
                } ?: android.util.Log.d("important", "✗ No selected exercise, skipping")
            }
        }

        private fun changeDateRange(from: LocalDateTime, to: LocalDateTime, state: StatisticsStore.State) {
            android.util.Log.d("important", "=== changeDateRange called ===")
            android.util.Log.d("important", "New date range: $from to $to")
            android.util.Log.d("important", "Selected exerciseId: ${state.selectedExerciseId}")
            android.util.Log.d("important", "Exercise friends before: ${state.exerciseFriends}")

            val newState = state.copy(dateRange = DateRange(from, to))

            // First, reload user data
            dispatch(Msg.DateRangeChanged(DateRange(from, to)))
            loadData(newState)

            // Then reload friend exercise data with new date range (using current friends, not cleared ones)
            android.util.Log.d("important", "Reloading friend exercise data with new date range")
            if (newState.selectedExerciseId != null) {
                state.exerciseFriends.forEach { friend ->
                    android.util.Log.d("important", "Loading friend ${friend.friendId} (${friend.friendName}) exercise data")
                    loadFriendExerciseData(friend.friendId, friend.friendName, newState.selectedExerciseId, newState)
                }
            } else {
                android.util.Log.d("important", "No selected exercise, skipping friend data reload")
            }
        }

        private fun updateWeight(value: Float, state: StatisticsStore.State) {
            val userId = state.userId ?: return
            scope.launch {
                try {
                    updateWeightUseCase(userId, LocalDateTime.now(), value)
                    dispatch(Msg.CurrentWeightUpdated(value))
                } catch (e: Exception) {
                    dispatch(Msg.Error(e.message ?: "Failed to update weight"))
                }
            }
        }

        private fun selectExercise(exerciseId: String, state: StatisticsStore.State) {
            android.util.Log.d("friends", "=== 🏋️ Selecting exercise ===")
            android.util.Log.d("friends", "Exercise ID: $exerciseId")

            dispatch(Msg.ExerciseSelected(exerciseId))

            val userId = state.userId ?: return
            android.util.Log.d("EXERCISE_IDS", "=== 📋 USER SELECTED EXERCISE ===")
            android.util.Log.d("EXERCISE_IDS", "User ID: $userId")
            android.util.Log.d("EXERCISE_IDS", "Selected Exercise ID: $exerciseId")
            loadExerciseData(exerciseId, state, userId, state.userName ?: "You")
        }

        private fun loadExerciseData(exerciseId: String, state: StatisticsStore.State, userId: String, userName: String) {
            android.util.Log.d("important", "=== loadExerciseData called ===")
            android.util.Log.d("important", "exerciseId: $exerciseId, userId: $userId, userName: $userName")
            android.util.Log.d("important", "Date range: ${state.dateRange.from} to ${state.dateRange.to}")

            scope.launch {
                android.util.Log.d("important", "Inside coroutine, calling getExerciseChartDataUseCase...")

                getExerciseChartDataUseCase(userId, userName, exerciseId, state.dateRange.from, state.dateRange.to)
                    .catch { e ->
                        android.util.Log.e("important", "ERROR: Failed to load exercise data: ${e.message}", e)
                        dispatch(Msg.Error(e.message ?: "Failed to load exercise data"))
                    }
                    .collect { data ->
                        android.util.Log.d("StatisticsStore", "=== Exercise data received from use case ===")
                        android.util.Log.d("StatisticsStore", "Data points received: ${data.size}")
                        android.util.Log.d("StatisticsStore", "Data points: $data")

                        val groupedData = data.groupBy { it.setIndex }
                        android.util.Log.d("StatisticsStore", "=== Grouped data ===")
                        android.util.Log.d("StatisticsStore", "Grouped by setIndex: $groupedData")
                        android.util.Log.d("StatisticsStore", "Number of sets: ${groupedData.size}")
                        groupedData.forEach { (setIndex, points) ->
                            android.util.Log.d("StatisticsStore", "Set $setIndex: ${points.size} points")
                        }

                        android.util.Log.d("StatisticsStore", "Dispatching Msg.ExerciseDataLoaded...")
                        dispatch(Msg.ExerciseDataLoaded(groupedData))
                        android.util.Log.d("StatisticsStore", "Msg.ExerciseDataLoaded dispatched")
                    }
            }
        }

        private fun addFriendToChart(chartType: ChartType, friendId: String, color: Long, state: StatisticsStore.State) {
            android.util.Log.d("friends", "=== 👤 Adding friend to chart ===")
            android.util.Log.d("friends", "chartType: $chartType, friendId: $friendId, color: 0x${color.toString(16)}")
            android.util.Log.d("friends", "selectedExerciseId: ${state.selectedExerciseId}")

            scope.launch {
                try {
                    // Get friend info
                    getFriendsUseCase().collect { friends ->
                        android.util.Log.d("important", "Friends received from getFriendsUseCase: ${friends.size}")
                        android.util.Log.d("important", "Looking for friendId: $friendId")
                        val friend = friends.find { it.friendUserId == friendId }
                        android.util.Log.d("important", "Friend found: ${friend != null}")
                        if (friend != null) {
                            val friendConfig = FriendConfig(
                                friendId = friendId,
                                friendName = friend.username ?: "Friend",
                                color = color
                            )
                            android.util.Log.d("important", "FriendConfig created: $friendConfig")

                            // For exercise chart, check if friend has this exercise
                            if (chartType == ChartType.EXERCISE_LINE && state.selectedExerciseId != null) {
                                android.util.Log.d("friends", "🔍 Checking if friend $friendId has exercise: ${state.selectedExerciseId}")
                                val hasExercise = friendHasExerciseUseCase(friendId, state.selectedExerciseId)
                                android.util.Log.d("friends", "✓ Friend has exercise: $hasExercise")
                                if (!hasExercise) {
                                    android.util.Log.d("friends", "❌ Friend doesn't have exercise, showing error")
                                    publish(StatisticsStore.Label.ShowFriendExerciseError("Friend doesn't have this exercise"))
                                    return@collect
                                }
                            }

                            android.util.Log.d("important", "Dispatching friend added message...")
                            when (chartType) {
                                ChartType.WEIGHT_LINE -> {
                                    android.util.Log.d("important", "WEIGHT_LINE - dispatching FriendWeightAdded")
                                    dispatch(Msg.FriendWeightAdded(friendConfig))
                                    loadFriendWeightData(friendId, friendConfig.friendName, state)
                                }
                                ChartType.EXERCISE_LINE -> {
                                    android.util.Log.d("important", "EXERCISE_LINE - dispatching FriendExerciseAdded")
                                    dispatch(Msg.FriendExerciseAdded(friendConfig))
                                    loadFriendExerciseData(friendId, friendConfig.friendName, state.selectedExerciseId, state)
                                }
                                ChartType.SPIDER -> {
                                    android.util.Log.d("important", "SPIDER - dispatching FriendMuscleGroupAdded")
                                    dispatch(Msg.FriendMuscleGroupAdded(friendConfig))
                                    loadFriendMuscleGroupData(friendId, state)
                                }
                            }
                        } else {
                            android.util.Log.d("important", "Friend not found in friends list!")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("important", "ERROR in addFriendToChart: ${e.message}", e)
                    dispatch(Msg.Error(e.message ?: "Failed to add friend to chart"))
                }
            }
        }

        private fun removeFriendFromChart(chartType: ChartType, friendId: String, state: StatisticsStore.State) {
            when (chartType) {
                ChartType.WEIGHT_LINE -> dispatch(Msg.FriendWeightRemoved(friendId))
                ChartType.EXERCISE_LINE -> dispatch(Msg.FriendExerciseRemoved(friendId))
                ChartType.SPIDER -> dispatch(Msg.FriendMuscleGroupRemoved(friendId))
            }
        }

        private fun changeFriendColor(chartType: ChartType, friendId: String, color: Long, state: StatisticsStore.State) {
            when (chartType) {
                ChartType.WEIGHT_LINE -> dispatch(Msg.FriendWeightColorChanged(friendId, color))
                ChartType.EXERCISE_LINE -> {
                    // Update friend color in exercise friends
                    val updatedFriends = state.exerciseFriends.map {
                        if (it.friendId == friendId) it.copy(color = color) else it
                    }
                    // This would need to be handled differently in the reducer
                }
                ChartType.SPIDER -> {
                    // Similar for muscle groups
                }
            }
        }

        private fun loadFriendWeightData(friendId: String, friendName: String, state: StatisticsStore.State) {
            scope.launch {
                getFriendWeightChartDataUseCase(friendId, friendName, state.dateRange.from, state.dateRange.to)
                    .catch { e -> dispatch(Msg.Error(e.message ?: "Failed to load friend weight data")) }
                    .collect { data -> dispatch(Msg.FriendWeightDataLoaded(data)) }
            }
        }

        private fun loadFriendExerciseData(friendId: String, friendName: String, exerciseId: String?, state: StatisticsStore.State) {
            android.util.Log.d("friends", "=== 📊 Loading friend exercise data ===")
            android.util.Log.d("friends", "FriendId: $friendId, FriendName: $friendName, ExerciseId: $exerciseId")
            android.util.Log.d("friends", "Date range: ${state.dateRange.from} to ${state.dateRange.to}")

            if (exerciseId == null) {
                android.util.Log.d("friends", "❌ ExerciseId is null, returning")
                return
            }
            scope.launch {
                android.util.Log.d("friends", "Calling getFriendExerciseChartDataUseCase...")
                getFriendExerciseChartDataUseCase(friendId, friendName, exerciseId, state.dateRange.from, state.dateRange.to)
                    .catch { e ->
                        android.util.Log.e("friends", "❌ ERROR: ${e.message}", e)
                        dispatch(Msg.Error(e.message ?: "Failed to load friend exercise data"))
                    }
                    .collect { data ->
                        android.util.Log.d("friends", "✅ Friend exercise data loaded: ${data.size} points")
                        android.util.Log.d("friends", "Data: $data")
                        dispatch(Msg.FriendExerciseDataLoaded(friendId, data))
                    }
            }
        }

        private fun loadFriendMuscleGroupData(friendId: String, state: StatisticsStore.State) {
            scope.launch {
                getFriendMuscleGroupChartDataUseCase(friendId, state.dateRange.from, state.dateRange.to)
                    .catch { e -> dispatch(Msg.Error(e.message ?: "Failed to load friend muscle group data")) }
                    .collect { data -> dispatch(Msg.MuscleGroupDataLoaded(data)) }
            }
        }
    }

    private object ReducerImpl : Reducer<StatisticsStore.State, Msg> {
        override fun StatisticsStore.State.reduce(msg: Msg): StatisticsStore.State {
            android.util.Log.d("StatisticsStore", "=== Reducer called ===")
            android.util.Log.d("StatisticsStore", "Msg: $msg")
            android.util.Log.d("StatisticsStore", "Current userId: $userId, userName: $userName")

            return when (msg) {
                Msg.Loading -> copy(
                    isLoading = true,
                    error = null
                ).also {
                    android.util.Log.d(
                        "StatisticsStore",
                        "After Loading reducer: userId=${it.userId}"
                    )
                }

                is Msg.Error -> copy(
                    isLoading = false,
                    error = msg.error
                ).also {
                    android.util.Log.d(
                        "StatisticsStore",
                        "After Error reducer: userId=${it.userId}"
                    )
                }

                is Msg.UserLoaded -> copy(
                    userId = msg.userId,
                    userName = msg.userName
                ).also {
                    android.util.Log.d(
                        "StatisticsStore",
                        "After UserLoaded reducer: userId=${it.userId}, userName=${it.userName}"
                    )
                }

                is Msg.DateRangeChanged -> copy(
                    dateRange = msg.dateRange,
                    // Only clear friend data - it needs to be reloaded with new date range
                    // User exercise data will be replaced when new data loads
                    friendExerciseData = emptyMap()
                ).also {
                    android.util.Log.d(
                        "important",
                        "After DateRangeChanged reducer: userId=${it.userId}, user data kept, friend data cleared"
                    )
                }

                is Msg.WeightDataLoaded -> copy(
                    weightData = msg.data,
                    isLoading = false
                )

                is Msg.FriendWeightDataLoaded -> copy(
                    weightData = weightData + msg.data
                )

                is Msg.CurrentWeightUpdated -> copy(
                    currentWeight = msg.weight
                )

                is Msg.FriendWeightAdded -> copy(
                    weightFriends = weightFriends + msg.friendConfig
                )

                is Msg.FriendWeightRemoved -> copy(
                    weightFriends = weightFriends.filter { it.friendId != msg.friendId }
                )

                is Msg.FriendWeightColorChanged -> copy(
                    weightFriends = weightFriends.map {
                        if (it.friendId == msg.friendId) it.copy(color = msg.color) else it
                    }
                )

                is Msg.ExerciseSelected -> {
                    android.util.Log.d("StatisticsStore", "=== Reducer: ExerciseSelected ===")
                    android.util.Log.d("StatisticsStore", "New exerciseId: ${msg.exerciseId}")
                    android.util.Log.d(
                        "StatisticsStore",
                        "Old selectedExerciseId: $selectedExerciseId"
                    )
                    android.util.Log.d(
                        "StatisticsStore",
                        "Is different exercise: ${selectedExerciseId != msg.exerciseId}"
                    )

                    copy(
                        selectedExerciseId = msg.exerciseId,
                        // Clear exercise data and friends only if switching to a different exercise
                        exerciseData = if (selectedExerciseId != msg.exerciseId) {
                            android.util.Log.d("StatisticsStore", "Clearing exerciseData")
                            emptyMap()
                        } else {
                            android.util.Log.d("StatisticsStore", "Keeping existing exerciseData")
                            exerciseData
                        },
                        visibleSets = if (selectedExerciseId != msg.exerciseId) {
                            android.util.Log.d("StatisticsStore", "Clearing visibleSets")
                            emptySet()
                        } else {
                            android.util.Log.d("StatisticsStore", "Keeping existing visibleSets")
                            visibleSets
                        },
                        setColors = if (selectedExerciseId != msg.exerciseId) {
                            android.util.Log.d("StatisticsStore", "Clearing setColors")
                            emptyMap()
                        } else {
                            android.util.Log.d("StatisticsStore", "Keeping existing setColors")
                            setColors
                        },
                        friendExerciseData = if (selectedExerciseId != msg.exerciseId) {
                            android.util.Log.d("StatisticsStore", "Clearing friendExerciseData")
                            emptyMap()
                        } else {
                            android.util.Log.d("StatisticsStore", "Keeping existing friendExerciseData")
                            friendExerciseData
                        }
                    ).also {
                        android.util.Log.d(
                            "StatisticsStore",
                            "After ExerciseSelected reducer: userId=${it.userId}, userName=${it.userName}"
                        )
                    }
                }

                is Msg.ExerciseDataLoaded -> {
                    android.util.Log.d("friends", "=== 🏋️ User exercise data loaded ===")
                    android.util.Log.d("friends", "Data points: ${msg.data.size}, Sets: ${msg.data.keys}")
                    android.util.Log.d("friends", "User's exercise recordings:")
                    msg.data.forEach { (setIndex, points) ->
                        android.util.Log.d("friends", "   🎯 Set $setIndex: ${points.size} points")
                        points.forEach { point ->
                            android.util.Log.d("friends", "      📅 ${point.date} 💪 ${point.value}kg")
                        }
                    }

                    // Default colors for sets (deterministic by index)
                    // Format: 0xAARRGGBB where AA is alpha (0xFF = fully opaque)
                    val defaultColors = listOf(
                        0xFFFF6366F1L, // Indigo
                        0xFFFFEC4899L, // Pink
                        0xFFFF10B981L, // Emerald
                        0xFFFFF59E0BL, // Amber
                        0xFFFFEF4444L, // Red
                        0xFFFF8B5CF6L, // Violet
                        0xFFFF06B6D4L, // Cyan
                        0xFFFF84CC16L, // Lime
                        0xFFFFF97316L, // Orange
                        0xFFFF0EA5E9L  // Sky
                    )

                    // Check if sets have changed and if we need to update colors
                    val newSetIndices = msg.data.keys.toSet()
                    val existingSetIndices = setColors.keys.toSet()

                    android.util.Log.d("StatisticsStore", "New set indices: $newSetIndices")
                    android.util.Log.d("StatisticsStore", "Existing set indices: $existingSetIndices")

                    // Only update colors if there are new sets that don't have colors yet
                    val setsWithoutColors = newSetIndices - existingSetIndices
                    android.util.Log.d("StatisticsStore", "Sets without colors: $setsWithoutColors")

                    val newSetColors = if (setsWithoutColors.isEmpty() && newSetIndices == existingSetIndices) {
                        // No changes needed - keep existing colors
                        android.util.Log.d("StatisticsStore", "No new sets, keeping existing colors")
                        setColors
                    } else {
                        // Add colors for new sets
                        android.util.Log.d("StatisticsStore", "Adding colors for new sets")
                        val updatedColors = setColors.toMutableMap()
                        msg.data.keys.forEach { setIndex ->
                            if (!updatedColors.containsKey(setIndex)) {
                                updatedColors[setIndex] = defaultColors.getOrElse(setIndex) {
                                    // If out of default colors, cycle through them
                                    defaultColors[setIndex % defaultColors.size]
                                }
                            }
                        }
                        android.util.Log.d("StatisticsStore", "Updated setColors: $updatedColors")
                        updatedColors
                    }

                    copy(
                        exerciseData = msg.data,
                        visibleSets = msg.data.keys,
                        setColors = newSetColors,
                        isLoading = false
                    )
                }

                is Msg.SetVisibilityToggled -> copy(
                    visibleSets = if (msg.setIndex in visibleSets) {
                        visibleSets - msg.setIndex
                    } else {
                        visibleSets + msg.setIndex
                    }
                )

                is Msg.SetLineColorChanged -> {
                    android.util.Log.d("StatisticsStore", "=== Reducer: SetLineColorChanged ===")
                    android.util.Log.d("StatisticsStore", "Set index: ${msg.setIndex}")
                    android.util.Log.d("StatisticsStore", "Color Long: 0x${msg.color.toString(16)}")
                    android.util.Log.d("StatisticsStore", "Old setColors: $setColors")
                    android.util.Log.d("StatisticsStore", "Old setColors.size: ${setColors.size}")
                    android.util.Log.d("StatisticsStore", "Old setColors keys: ${setColors.keys}")
                    val newSetColors = setColors + (msg.setIndex to msg.color)
                    android.util.Log.d("StatisticsStore", "New setColors: $newSetColors")
                    android.util.Log.d("StatisticsStore", "New setColors.size: ${newSetColors.size}")
                    android.util.Log.d("StatisticsStore", "New setColors keys: ${newSetColors.keys}")
                    android.util.Log.d("StatisticsStore", "New color for set ${msg.setIndex}: 0x${newSetColors[msg.setIndex]!!.toString(16)}")
                    copy(setColors = newSetColors)
                }

                is Msg.FriendExerciseAdded -> copy(
                    exerciseFriends = exerciseFriends + msg.friendConfig
                ).also {
                    android.util.Log.d("friends", "✅ Friend added to chart: ${msg.friendConfig.friendName} (${msg.friendConfig.friendId})")
                }

                is Msg.FriendExerciseRemoved -> copy(
                    exerciseFriends = exerciseFriends.filter { it.friendId != msg.friendId },
                    friendExerciseData = friendExerciseData.filterKeys { it != msg.friendId }
                ).also {
                    android.util.Log.d("friends", "🗑️ Friend removed from chart: $msg.friendId")
                }

                is Msg.FriendExerciseDataLoaded -> {
                    android.util.Log.d("friends", "=== 📊 Friend exercise data loaded ===")
                    android.util.Log.d("friends", "FriendId: ${msg.friendId}")
                    android.util.Log.d("friends", "Exercise data points: ${msg.data.size}")
                    msg.data.forEach { point ->
                        android.util.Log.d("friends", "   📅 ${point.date} 💪 ${point.value}kg 🏋️ Set ${point.setIndex}")
                        android.util.Log.d("friends", "      Exercise ID in DB: ${point.userId}")
                    }

                    // Check what exercises friend has recorded
                    val friendExercises = msg.data.map { it.userId }.distinct()
                    android.util.Log.d("friends", "📋 Friend's recorded exercise IDs:")
                    friendExercises.forEach { exerciseId ->
                        android.util.Log.d("friends", "   - $exerciseId")
                    }

                    copy(
                        friendExerciseData = friendExerciseData + (msg.friendId to msg.data),
                        isLoading = false
                    ).also {
                        android.util.Log.d("friends", "✅ Friend data added to state")
                        android.util.Log.d("friends", "   Total friends with data: ${it.friendExerciseData.size}")
                    }
                }

                is Msg.MuscleGroupDataLoaded -> copy(
                    muscleGroupData = msg.data,
                    isLoading = false
                )

                is Msg.FriendMuscleGroupAdded -> copy(
                    muscleGroupFriends = muscleGroupFriends + msg.friendConfig
                )

                is Msg.FriendMuscleGroupRemoved -> copy(
                    muscleGroupFriends = muscleGroupFriends.filter { it.friendId != msg.friendId }
                ).also {
                    android.util.Log.d(
                        "StatisticsStore",
                        "After FriendMuscleGroupRemoved reducer: userId=${it.userId}"
                    )
                }
            }.also { newState ->
                android.util.Log.d("StatisticsStore", "=== Reducer finished ===")
                android.util.Log.d(
                    "StatisticsStore",
                    "Final userId: ${newState.userId}, userName: ${newState.userName}"
                )
            }
        }
    }
}