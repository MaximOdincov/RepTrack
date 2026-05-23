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
            android.util.Log.d("StatisticsStore", "========================================")
            android.util.Log.d("StatisticsStore", "=== loadData called ===")
            android.util.Log.d("StatisticsStore", "========================================")
            android.util.Log.d("StatisticsStore", "userId in state: ${state.userId}")
            android.util.Log.d("StatisticsStore", "selectedExerciseId in state: ${state.selectedExerciseId}")
            android.util.Log.d("StatisticsStore", "executor: ${this@ExecutorImpl}")

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

                // Load weight data
                val userId = state.userId ?: return@launch
                getWeightChartDataUseCase(userId, state.dateRange.from, state.dateRange.to)
                    .catch { e -> dispatch(Msg.Error(e.message ?: "Failed to load weight data")) }
                    .collect { data -> dispatch(Msg.WeightDataLoaded(data)) }

                // Load muscle group data
                getMuscleGroupChartDataUseCase(userId, state.dateRange.from, state.dateRange.to)
                    .catch { e -> dispatch(Msg.Error(e.message ?: "Failed to load muscle group data")) }
                    .collect { data -> dispatch(Msg.MuscleGroupDataLoaded(data)) }

                // Load exercise data if selected
                state.selectedExerciseId?.let { exerciseId ->
                    loadExerciseData(exerciseId, state, userId, state.userName ?: "You")
                }
            }
        }

        private fun changeDateRange(from: LocalDateTime, to: LocalDateTime, state: StatisticsStore.State) {
            dispatch(Msg.DateRangeChanged(DateRange(from, to)))
            loadData(state.copy(dateRange = DateRange(from, to)))
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
            android.util.Log.d("StatisticsStore", "=== selectExercise called ===")
            android.util.Log.d("StatisticsStore", "exerciseId: $exerciseId")
            android.util.Log.d("StatisticsStore", "current selectedExerciseId in state: ${state.selectedExerciseId}")
            android.util.Log.d("StatisticsStore", "current exerciseData in state: ${state.exerciseData}")
            android.util.Log.d("StatisticsStore", "current exerciseData size: ${state.exerciseData.size}")
            android.util.Log.d("StatisticsStore", "userId: ${state.userId}, userName: ${state.userName}")

            dispatch(Msg.ExerciseSelected(exerciseId))
            android.util.Log.d("StatisticsStore", "Msg.ExerciseSelected dispatched")

            val userId = state.userId ?: return
            android.util.Log.d("StatisticsStore", "Calling loadExerciseData with userId: $userId, exerciseId: $exerciseId")
            loadExerciseData(exerciseId, state, userId, state.userName ?: "You")
        }

        private fun loadExerciseData(exerciseId: String, state: StatisticsStore.State, userId: String, userName: String) {
            android.util.Log.d("StatisticsStore", "=== loadExerciseData called ===")
            android.util.Log.d("StatisticsStore", "exerciseId: $exerciseId, userId: $userId, userName: $userName")
            android.util.Log.d("StatisticsStore", "Date range: ${state.dateRange.from} to ${state.dateRange.to}")

            scope.launch {
                android.util.Log.d("StatisticsStore", "Calling getExerciseChartDataUseCase...")

                getExerciseChartDataUseCase(userId, userName, exerciseId, state.dateRange.from, state.dateRange.to)
                    .catch { e ->
                        android.util.Log.e("StatisticsStore", "ERROR: Failed to load exercise data: ${e.message}", e)
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
            scope.launch {
                try {
                    // Get friend info
                    getFriendsUseCase().collect { friends ->
                        val friend = friends.find { it.friendUserId == friendId }
                        if (friend != null) {
                            val friendConfig = FriendConfig(
                                friendId = friendId,
                                friendName = friend.username ?: "Friend",
                                color = color
                            )

                            // For exercise chart, check if friend has this exercise
                            if (chartType == ChartType.EXERCISE_LINE && state.selectedExerciseId != null) {
                                val hasExercise = friendHasExerciseUseCase(friendId, state.selectedExerciseId)
                                if (!hasExercise) {
                                    publish(StatisticsStore.Label.ShowFriendExerciseError("Friend doesn't have this exercise"))
                                    return@collect
                                }
                            }

                            when (chartType) {
                                ChartType.WEIGHT_LINE -> {
                                    dispatch(Msg.FriendWeightAdded(friendConfig))
                                    loadFriendWeightData(friendId, friendConfig.friendName, state)
                                }
                                ChartType.EXERCISE_LINE -> {
                                    dispatch(Msg.FriendExerciseAdded(friendConfig))
                                    loadFriendExerciseData(friendId, friendConfig.friendName, state.selectedExerciseId, state)
                                }
                                ChartType.SPIDER -> {
                                    dispatch(Msg.FriendMuscleGroupAdded(friendConfig))
                                    loadFriendMuscleGroupData(friendId, state)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
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
            if (exerciseId == null) return
            scope.launch {
                getFriendExerciseChartDataUseCase(friendId, friendName, exerciseId, state.dateRange.from, state.dateRange.to)
                    .catch { e -> dispatch(Msg.Error(e.message ?: "Failed to load friend exercise data")) }
                    .collect { data ->
                        // Friend exercise data is always setIndex 0 (best set)
                        val groupedData = mapOf(0 to data)
                        dispatch(Msg.ExerciseDataLoaded(groupedData))
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
                    dateRange = msg.dateRange
                ).also {
                    android.util.Log.d(
                        "StatisticsStore",
                        "After DateRangeChanged reducer: userId=${it.userId}"
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
                        // Clear exercise data only if switching to a different exercise
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
                        }
                    ).also {
                        android.util.Log.d(
                            "StatisticsStore",
                            "After ExerciseSelected reducer: userId=${it.userId}, userName=${it.userName}"
                        )
                    }
                }

                is Msg.ExerciseDataLoaded -> {
                    android.util.Log.d("StatisticsStore", "=== Reducer: ExerciseDataLoaded ===")
                    android.util.Log.d(
                        "StatisticsStore",
                        "Data keys (setIndices): ${msg.data.keys}"
                    )
                    android.util.Log.d("StatisticsStore", "Data size: ${msg.data.size}")
                    android.util.Log.d("StatisticsStore", "New visibleSets: ${msg.data.keys}")

                    copy(
                        exerciseData = msg.data,
                        visibleSets = msg.data.keys,
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
                    val newSetColors = setColors + (msg.setIndex to msg.color)
                    android.util.Log.d("StatisticsStore", "New setColors: $newSetColors")
                    copy(setColors = newSetColors)
                }

                is Msg.FriendExerciseAdded -> copy(
                    exerciseFriends = exerciseFriends + msg.friendConfig
                )

                is Msg.FriendExerciseRemoved -> copy(
                    exerciseFriends = exerciseFriends.filter { it.friendId != msg.friendId }
                )

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