package com.example.reptrack.presentation.main.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.CalendarMonth
import com.example.reptrack.domain.workout.CalendarWeek
import com.example.reptrack.domain.workout.WorkoutSession
import com.example.reptrack.presentation.main.stores.MainScreenStore.Intent
import com.example.reptrack.presentation.main.stores.MainScreenStore.Label
import com.example.reptrack.presentation.main.stores.MainScreenStore.State
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate

internal interface MainScreenStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class SelectDate(val date: LocalDate) : Intent
        data class NavigateWeek(val offset: Int) : Intent
        data class NavigateMonth(val offset: Int) : Intent
        object ExpandCalendar : Intent
        object CollapseCalendar : Intent
    }

    data class State constructor(
        val currentDate: LocalDate = LocalDate.now(),
        val displayDate: LocalDate = LocalDate.now(),
        val weekCalendar: CalendarWeek? = null,
        val weekCalendarPrev: CalendarWeek? = null,
        val weekCalendarNext: CalendarWeek? = null,
        val monthCalendar: CalendarMonth? = null,
        val monthCalendarPrev: CalendarMonth? = null,
        val monthCalendarNext: CalendarMonth? = null,
        val selectedWorkout: WorkoutSession? = null,
        val isCalendarExpanded: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Label {
        data class Error(val message: String) : Label
    }
}

internal class MainScreenStoreFactory(
    private val storeFactory: StoreFactory,
    private val calendarUseCase: CalendarUseCase
) {

    fun create(): MainScreenStore =
        object : MainScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MainScreenStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl(calendarUseCase) },
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class LoadWeekCalendar(val date: LocalDate) : Action
        data class LoadMonthCalendar(val date: LocalDate) : Action
        data class SetSelectedWorkout(val workout: WorkoutSession?) : Action
    }

    private sealed interface Msg {
        data class CalendarWeekLoaded(val calendar: CalendarWeek) : Msg
        data class CalendarWeekPrevLoaded(val calendar: CalendarWeek) : Msg
        data class CalendarWeekNextLoaded(val calendar: CalendarWeek) : Msg
        data class CalendarMonthLoaded(val calendar: CalendarMonth) : Msg
        data class CalendarMonthPrevLoaded(val calendar: CalendarMonth) : Msg
        data class CalendarMonthNextLoaded(val calendar: CalendarMonth) : Msg
        data class WorkoutSelected(val workout: WorkoutSession?) : Msg
        data class DateChanged(val newDate: LocalDate) : Msg
        data class ExpandedChanged(val expanded: Boolean) : Msg
        data class LoadingChanged(val isLoading: Boolean) : Msg
        data class ErrorOccurred(val message: String) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadWeekCalendar(LocalDate.now()))
            dispatch(Action.LoadMonthCalendar(LocalDate.now()))
        }
    }

    private class ExecutorImpl(
        private val calendarUseCase: CalendarUseCase
    ) : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.SelectDate -> {
                    dispatch(Msg.DateChanged(intent.date))
                    loadWorkoutForDate(intent.date)
                }

                is Intent.NavigateWeek -> {
                    val newDate = getState().displayDate.plusDays(intent.offset.toLong() * 7)
                    dispatch(Msg.DateChanged(newDate))
                    loadWeekCalendar(newDate)
                }

                is Intent.NavigateMonth -> {
                    val newDate = getState().displayDate.plusMonths(intent.offset.toLong())
                    dispatch(Msg.DateChanged(newDate))
                    loadMonthCalendar(newDate)
                }

                is Intent.ExpandCalendar -> {
                    val state = getState()
                    dispatch(Msg.ExpandedChanged(true))
                    loadMonthCalendar(state.displayDate)
                }

                is Intent.CollapseCalendar -> {
                    dispatch(Msg.ExpandedChanged(false))
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.LoadWeekCalendar -> {
                    loadWeekCalendar(action.date)
                }

                is Action.LoadMonthCalendar -> {
                    loadMonthCalendar(action.date)
                }

                is Action.SetSelectedWorkout -> {
                    dispatch(Msg.WorkoutSelected(action.workout))
                }
            }
        }

        private fun loadWeekCalendar(date: LocalDate) {
            dispatch(Msg.LoadingChanged(true))
            scope.launch {
                try {
                    // Load current week
                    val result = calendarUseCase.getWeekCalendar(date)
                    result.onSuccess { calendar ->
                        dispatch(Msg.CalendarWeekLoaded(calendar))
                        dispatch(Msg.LoadingChanged(false))

                        // Load previous week
                        val prevDate = date.minusDays(7)
                        calendarUseCase.getWeekCalendar(prevDate).onSuccess { prevCalendar ->
                            dispatch(Msg.CalendarWeekPrevLoaded(prevCalendar))
                        }

                        // Load next week
                        val nextDate = date.plusDays(7)
                        calendarUseCase.getWeekCalendar(nextDate).onSuccess { nextCalendar ->
                            dispatch(Msg.CalendarWeekNextLoaded(nextCalendar))
                        }
                    }
                    result.onFailure { exception ->
                        dispatch(Msg.ErrorOccurred(exception.message ?: "Unknown error"))
                        dispatch(Msg.LoadingChanged(false))
                    }
                } catch (e: Exception) {
                    dispatch(Msg.ErrorOccurred(e.message ?: "Unknown error"))
                    dispatch(Msg.LoadingChanged(false))
                }
            }
        }

        private fun loadMonthCalendar(date: LocalDate) {
            scope.launch {
                try {
                    // Load current month
                    val result = calendarUseCase.getMonthCalendar(date)
                    result.onSuccess { calendar ->
                        dispatch(Msg.CalendarMonthLoaded(calendar))

                        // Load previous month
                        val prevDate = date.minusMonths(1)
                        calendarUseCase.getMonthCalendar(prevDate).onSuccess { prevCalendar ->
                            dispatch(Msg.CalendarMonthPrevLoaded(prevCalendar))
                        }

                        // Load next month
                        val nextDate = date.plusMonths(1)
                        calendarUseCase.getMonthCalendar(nextDate).onSuccess { nextCalendar ->
                            dispatch(Msg.CalendarMonthNextLoaded(nextCalendar))
                        }
                    }
                    result.onFailure { exception ->
                        dispatch(Msg.ErrorOccurred(exception.message ?: "Unknown error"))
                    }
                } catch (e: Exception) {
                    dispatch(Msg.ErrorOccurred(e.message ?: "Unknown error"))
                }
            }
        }

        private fun loadWorkoutForDate(date: LocalDate) {
            scope.launch {
                try {
                    val result = calendarUseCase.getWeekCalendar(date)
                    result.onSuccess { calendar ->
                        val selectedDay = calendar.days.find { it.date == date }
                        dispatch(Msg.WorkoutSelected(selectedDay?.workoutSession))
                    }
                } catch (e: Exception) {
                    dispatch(Msg.ErrorOccurred(e.message ?: "Unknown error"))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State =
            when (message) {
                is Msg.CalendarWeekLoaded -> copy(weekCalendar = message.calendar)
                is Msg.CalendarWeekPrevLoaded -> copy(weekCalendarPrev = message.calendar)
                is Msg.CalendarWeekNextLoaded -> copy(weekCalendarNext = message.calendar)
                is Msg.CalendarMonthLoaded -> copy(monthCalendar = message.calendar)
                is Msg.CalendarMonthPrevLoaded -> copy(monthCalendarPrev = message.calendar)
                is Msg.CalendarMonthNextLoaded -> copy(monthCalendarNext = message.calendar)
                is Msg.WorkoutSelected -> copy(selectedWorkout = message.workout)
                is Msg.DateChanged -> copy(currentDate = message.newDate, displayDate = message.newDate)
                is Msg.ExpandedChanged -> copy(isCalendarExpanded = message.expanded)
                is Msg.LoadingChanged -> copy(isLoading = message.isLoading)
                is Msg.ErrorOccurred -> copy(error = message.message)
            }
    }
}
