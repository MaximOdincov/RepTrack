package com.example.reptrack.presentation.main.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.domain.workout.usecases.exercises.ObserveExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveBestSetFromLastWorkoutUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDate

internal interface MainScreenStore : Store<MainScreenStore.Intent, MainScreenStore.State, MainScreenStore.Label> {

    sealed interface Intent {
        data class SelectDate(val date: LocalDate) : Intent
        data class ExerciseClicked(val workoutExerciseId: String) : Intent
    }

    data class State constructor(
        val currentDate: LocalDate = LocalDate.now(),
        val isLoading: Boolean = false,
        val workoutSession: WorkoutSession? = null,
        val exerciseData: Map<String, ExerciseData> = emptyMap()
    )

    data class ExerciseData(
        val exercise: Exercise,
        val workoutExercise: WorkoutExercise,
        val bestSet: WorkoutSet?
    )

    sealed interface Label {
        data class NavigateToExerciseDetail(val workoutExerciseId: String) : Label
    }
}

internal class MainScreenStoreFactory(
    private val storeFactory: StoreFactory,
    private val calendarUseCase: CalendarUseCase,
    private val observeExerciseByIdUseCase: ObserveExerciseByIdUseCase,
    private val observeBestSetFromLastWorkoutUseCase: ObserveBestSetFromLastWorkoutUseCase
) {

    fun create(): MainScreenStore =
        object : MainScreenStore, Store<MainScreenStore.Intent, MainScreenStore.State, MainScreenStore.Label> by storeFactory.create(
            name = "MainScreenStore",
            initialState = MainScreenStore.State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data class DateChanged(val newDate: LocalDate) : Msg
        data class WorkoutSessionLoaded(val session: WorkoutSession?) : Msg
        data class ExerciseDataLoaded(val exerciseId: String, val data: MainScreenStore.ExerciseData) : Msg
        data class LoadingChanged(val isLoading: Boolean) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<MainScreenStore.Intent, Nothing, MainScreenStore.State, Msg, MainScreenStore.Label>() {

        override fun executeIntent(intent: MainScreenStore.Intent, getState: () -> MainScreenStore.State) {
            when (intent) {
                is MainScreenStore.Intent.SelectDate -> {
                    dispatch(Msg.DateChanged(intent.date))
                    loadWorkoutSession(intent.date)
                }
                is MainScreenStore.Intent.ExerciseClicked -> {
                    publish(MainScreenStore.Label.NavigateToExerciseDetail(intent.workoutExerciseId))
                }
            }
        }

        private fun loadWorkoutSession(date: LocalDate) {
            dispatch(Msg.LoadingChanged(true))
            scope.launch {
                calendarUseCase.observeWeekCalendar(date).flowOn(Dispatchers.IO).collect { weekCalendar ->
                    val session = weekCalendar?.days?.find { it.date == date }?.workoutSession
                    dispatch(Msg.WorkoutSessionLoaded(session))
                    dispatch(Msg.LoadingChanged(false))

                    // Load data for all exercises in the session
                    session?.exercises?.forEach { workoutExercise ->
                        loadExerciseData(workoutExercise)
                    }
                }
            }
        }

        private fun loadExerciseData(workoutExercise: WorkoutExercise) {
            scope.launch {
                observeExerciseByIdUseCase(workoutExercise.exerciseId).flowOn(Dispatchers.IO).collect { exercise ->
                    observeBestSetFromLastWorkoutUseCase(workoutExercise.exerciseId).flowOn(Dispatchers.IO).collect { bestSet ->
                        android.util.Log.d(
                            "MainScreenStore",
                            "Exercise loaded: ${workoutExercise.exerciseId}, bestSet: ${bestSet?.weight}kg ${bestSet?.reps}reps"
                        )
                        dispatch(
                            Msg.ExerciseDataLoaded(
                                exerciseId = workoutExercise.exerciseId,
                                data = MainScreenStore.ExerciseData(
                                    exercise = exercise,
                                    workoutExercise = workoutExercise,
                                    bestSet = bestSet
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<MainScreenStore.State, Msg> {
        override fun MainScreenStore.State.reduce(message: Msg): MainScreenStore.State =
            when (message) {
                is Msg.DateChanged -> copy(currentDate = message.newDate)
                is Msg.WorkoutSessionLoaded -> copy(workoutSession = message.session)
                is Msg.ExerciseDataLoaded -> copy(
                    exerciseData = exerciseData + (message.exerciseId to message.data)
                )
                is Msg.LoadingChanged -> copy(isLoading = message.isLoading)
            }
    }
}
