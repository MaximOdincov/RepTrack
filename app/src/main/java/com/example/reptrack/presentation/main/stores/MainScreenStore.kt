package com.example.reptrack.presentation.main.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.domain.workout.usecases.exercises.ObserveExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveBestSetFromLastWorkoutUseCase
import com.example.reptrack.domain.workout.usecases.sessions.CreateWorkoutSessionFromTemplateUseCase
import com.example.reptrack.domain.workout.usecases.sessions.ShouldUpdateSessionFromTemplateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDate

internal interface MainScreenStore : Store<MainScreenStore.Intent, MainScreenStore.State, MainScreenStore.Label> {

    sealed interface Intent {
        data class SelectDate(val date: LocalDate) : Intent
        data class ExerciseClicked(val workoutExerciseId: String) : Intent
        data class TemplateExerciseClicked(val exerciseId: String, val templateId: String) : Intent
    }

    data class State constructor(
        val currentDate: LocalDate = LocalDate.now(),
        val isLoading: Boolean = false,
        val workoutSession: WorkoutSession? = null,
        val exerciseData: Map<String, ExerciseData> = emptyMap(),
        val applicableTemplates: List<WorkoutTemplate> = emptyList(),
        val templateExerciseData: Map<String, Exercise> = emptyMap()
    )

    data class ExerciseData(
        val exercise: Exercise,
        val workoutExercise: WorkoutExercise,
        val bestSet: WorkoutSet?
    )

    sealed interface Label {
        data class NavigateToExerciseDetail(val workoutExerciseId: String) : Label
        data class NavigateToTemplateExercise(val exerciseId: String, val templateId: String) : Label
    }
}

internal class MainScreenStoreFactory(
    private val storeFactory: StoreFactory,
    private val calendarUseCase: CalendarUseCase,
    private val observeExerciseByIdUseCase: ObserveExerciseByIdUseCase,
    private val observeBestSetFromLastWorkoutUseCase: ObserveBestSetFromLastWorkoutUseCase,
    private val createSessionFromTemplateUseCase: CreateWorkoutSessionFromTemplateUseCase,
    private val shouldUpdateSessionFromTemplateUseCase: ShouldUpdateSessionFromTemplateUseCase,
    private val authRepository: com.example.reptrack.domain.auth.AuthRepository
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
        data class TemplatesLoaded(val templates: List<WorkoutTemplate>) : Msg
        data class TemplateExerciseDataLoaded(val exerciseId: String, val exercise: Exercise) : Msg
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
                is MainScreenStore.Intent.TemplateExerciseClicked -> {
                    publish(MainScreenStore.Label.NavigateToTemplateExercise(intent.exerciseId, intent.templateId))
                }
            }
        }

        private fun loadWorkoutSession(date: LocalDate) {
            dispatch(Msg.LoadingChanged(true))
            scope.launch {
                calendarUseCase.observeWeekCalendar(date).flowOn(Dispatchers.IO).collect { weekCalendar ->
                    val calendarDay = weekCalendar?.days?.find { it.date == date }
                    var session = calendarDay?.workoutSession
                    val templates = calendarDay?.applicableTemplates ?: emptyList()

                    android.util.Log.d("MainScreenStore", "Calendar day: date=$date, session=${session?.id}, templates=${templates.size}")

                    // Auto-create/update session from template
                    if (templates.isNotEmpty()) {
                        val template = templates.first() // Берем первый шаблон (обычно он один)
                        val userId = authRepository.getCurrentUser()?.id

                        if (userId != null && shouldUpdateSessionFromTemplateUseCase(session, template)) {
                            android.util.Log.d("MainScreenStore", "Creating/updating session from template: ${template.id}")

                            // Удаляем старую сессию если есть
                            if (session != null) {
                                // TODO: Добавить удаление сессии
                                android.util.Log.d("MainScreenStore", "Deleting old session: ${session.id}")
                            }

                            // Создаем новую сессию из шаблона
                            val result = createSessionFromTemplateUseCase(
                                templateId = template.id,
                                userId = userId,
                                date = date
                            )

                            if (result.isSuccess) {
                                session = result.getOrNull()
                                android.util.Log.d("MainScreenStore", "Session created: ${session?.id}")
                            } else {
                                android.util.Log.e("MainScreenStore", "Failed to create session: ${result.exceptionOrNull()?.message}")
                            }
                        }
                    }

                    dispatch(Msg.WorkoutSessionLoaded(session))
                    dispatch(Msg.TemplatesLoaded(templates))
                    dispatch(Msg.LoadingChanged(false))

                    // Load data for all exercises in the session
                    session?.exercises?.forEach { workoutExercise ->
                        loadExerciseData(workoutExercise)
                    }

                    // If no session but have templates, load template exercises
                    if (session == null && templates.isNotEmpty()) {
                        templates.forEach { template ->
                            template.exerciseIds.forEach { exerciseId ->
                                loadTemplateExerciseData(exerciseId)
                            }
                        }
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

        private fun loadTemplateExerciseData(exerciseId: String) {
            scope.launch {
                observeExerciseByIdUseCase(exerciseId).flowOn(Dispatchers.IO).collect { exercise ->
                    android.util.Log.d("MainScreenStore", "Template exercise loaded: $exerciseId, name=${exercise.name}")
                    dispatch(Msg.TemplateExerciseDataLoaded(exerciseId, exercise))
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
                is Msg.TemplatesLoaded -> copy(applicableTemplates = message.templates)
                is Msg.TemplateExerciseDataLoaded -> copy(
                    templateExerciseData = templateExerciseData + (message.exerciseId to message.exercise)
                )
            }
    }
}
