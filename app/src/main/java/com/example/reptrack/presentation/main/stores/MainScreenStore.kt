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
import com.example.reptrack.domain.workout.usecases.workout_exercises.DeleteWorkoutExerciseUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveBestSetFromLastWorkoutUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveWorkoutExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.sessions.CompleteWorkoutSessionUseCase
import com.example.reptrack.domain.workout.usecases.sessions.CreateWorkoutSessionFromTemplateUseCase
import com.example.reptrack.domain.workout.usecases.sessions.ShouldUpdateSessionFromTemplateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDate

internal interface MainScreenStore : Store<MainScreenStore.Intent, MainScreenStore.State, MainScreenStore.Label> {

    sealed interface Intent {
        data class SelectDate(val date: java.time.LocalDate) : Intent
        data class ExerciseClicked(val workoutExerciseId: String) : Intent
        data class TemplateExerciseClicked(val exerciseId: String, val templateId: String) : Intent
        data class DeleteExercise(val workoutExerciseId: String) : Intent
        data object CompleteWorkout : Intent
    }

    data class State constructor(
        val currentDate: java.time.LocalDate = java.time.LocalDate.now(),
        val isLoading: Boolean = false,
        val isInitializing: Boolean = true,
        val workoutSession: com.example.reptrack.domain.workout.entities.WorkoutSession? = null,
        val exerciseData: Map<String, ExerciseData> = emptyMap(),
        val applicableTemplates: List<com.example.reptrack.domain.workout.entities.WorkoutTemplate> = emptyList(),
        val templateExerciseData: Map<String, com.example.reptrack.domain.workout.entities.Exercise> = emptyMap()
    )

    data class ExerciseData(
        val exercise: com.example.reptrack.domain.workout.entities.Exercise,
        val workoutExercise: com.example.reptrack.domain.workout.entities.WorkoutExercise,
        val bestSet: com.example.reptrack.domain.workout.entities.WorkoutSet?
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
    private val observeWorkoutExerciseByIdUseCase: com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveWorkoutExerciseByIdUseCase,
    private val createSessionFromTemplateUseCase: CreateWorkoutSessionFromTemplateUseCase,
    private val shouldUpdateSessionFromTemplateUseCase: ShouldUpdateSessionFromTemplateUseCase,
    private val deleteWorkoutExerciseUseCase: DeleteWorkoutExerciseUseCase,
    private val completeWorkoutSessionUseCase: CompleteWorkoutSessionUseCase,
    private val unlinkSessionFromTemplateUseCase: com.example.reptrack.domain.workout.usecases.sessions.UnlinkSessionFromTemplateUseCase,
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
        data class InitializingChanged(val isInitializing: Boolean) : Msg
        data class TemplateExerciseDataLoaded(val exerciseId: String, val exercise: Exercise) : Msg
        data class ExerciseDeleted(val workoutExerciseId: String) : Msg
        data object ExerciseDataCleared : Msg
        data object TemplateExerciseDataCleared : Msg
        data class WorkoutCompleted(val session: WorkoutSession?) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<MainScreenStore.Intent, Nothing, MainScreenStore.State, Msg, MainScreenStore.Label>() {
        // Track active loading jobs to cancel them when date changes
        private var currentLoadJob: Job? = null
        private val activeExerciseDataJobs = mutableMapOf<String, Job>()

        override fun executeIntent(intent: MainScreenStore.Intent, getState: () -> MainScreenStore.State) {
            android.util.Log.d("MainScreenStore", "executeIntent: $intent")
            when (intent) {
                is MainScreenStore.Intent.SelectDate -> {
                    android.util.Log.d("MainScreenStore", "SelectDate: intent=${intent.date}, current=${getState().currentDate}")
                    val currentState = getState()
                    
                    // Always cancel previous loading operations and reload
                    currentLoadJob?.cancel()
                    activeExerciseDataJobs.values.forEach { it.cancel() }
                    activeExerciseDataJobs.clear()
                    
                    // Only update date if it changed
                    if (currentState.currentDate != intent.date) {
                        dispatch(Msg.DateChanged(intent.date))
                        dispatch(Msg.ExerciseDataCleared)
                        dispatch(Msg.TemplateExerciseDataCleared)
                    }
                    
                    loadWorkoutSession(intent.date)
                }
                is MainScreenStore.Intent.ExerciseClicked -> {
                    publish(MainScreenStore.Label.NavigateToExerciseDetail(intent.workoutExerciseId))
                }
                is MainScreenStore.Intent.TemplateExerciseClicked -> {
                    publish(MainScreenStore.Label.NavigateToTemplateExercise(intent.exerciseId, intent.templateId))
                }
                is MainScreenStore.Intent.DeleteExercise -> {
                    deleteExercise(intent.workoutExerciseId)
                }
                is MainScreenStore.Intent.CompleteWorkout -> {
                    completeWorkout(getState)
                }
            }
        }

        private fun loadWorkoutSession(date: LocalDate) {
            dispatch(Msg.LoadingChanged(true))
                android.util.Log.d("MainScreenStore", "loadWorkoutSession: date=$date")
            currentLoadJob = scope.launch {
                try {
                    val weekCalendar = calendarUseCase.observeWeekCalendar(date)
                        .flowOn(Dispatchers.IO)
                        .firstOrNull()
                    
                    if (weekCalendar != null) {
                        val calendarDay = weekCalendar.days.find { it.date == date }
                        val templates = calendarDay?.applicableTemplates ?: emptyList()

                        var session: WorkoutSession? = null

                        // If we have templates, always call UseCase - it will check if session exists
                        if (templates.isNotEmpty()) {
                            val template = templates.first()
                            val userId = authRepository.getCurrentUser()?.id

                            if (userId != null) {
                                val result = createSessionFromTemplateUseCase(
                                    templateId = template.id,
                                    userId = userId,
                                    date = date
                                )

                                if (result.isSuccess) {
                                    session = result.getOrNull()
                                } else {
                                    android.util.Log.e("SessionDB", "Failed to create session: ${result.exceptionOrNull()?.message}")
                                }
                            }
                        } else if (calendarDay?.workoutSession != null) {
                            // No templates but have existing session (e.g., manually created)
                            session = calendarDay.workoutSession
                        }

                        dispatch(Msg.LoadingChanged(false))
                        dispatch(Msg.WorkoutSessionLoaded(session))
                        dispatch(Msg.TemplatesLoaded(templates))
                        dispatch(Msg.InitializingChanged(false))

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
                } catch (e: Exception) {
                    dispatch(Msg.LoadingChanged(false))
                }
            }
        }

        private fun loadExerciseData(workoutExercise: WorkoutExercise) {
            // Cancel any existing job for this exercise
            activeExerciseDataJobs[workoutExercise.exerciseId]?.cancel()

            activeExerciseDataJobs[workoutExercise.exerciseId] = scope.launch {
                // Получаем bestSet
                observeBestSetFromLastWorkoutUseCase(workoutExercise.exerciseId).flowOn(Dispatchers.IO)
                    .collect { bestSet ->
                        // Используем данные из WorkoutExercise напрямую, без зависимости от Exercise из библиотеки
                        val exercise = createExerciseFromWorkoutExercise(workoutExercise)
                        val data = MainScreenStore.ExerciseData(
                            exercise = exercise,
                            workoutExercise = workoutExercise,
                            bestSet = bestSet
                        )
                        dispatch(Msg.ExerciseDataLoaded(exerciseId = workoutExercise.exerciseId, data = data))
                    }
            }
        }

        private fun createExerciseFromWorkoutExercise(workoutExercise: WorkoutExercise): Exercise {
            return Exercise(
                id = workoutExercise.exerciseId,
                name = workoutExercise.exerciseName,
                muscleGroup = workoutExercise.muscleGroup,
                type = workoutExercise.exerciseType,
                iconRes = workoutExercise.iconRes,
                iconColor = null,
                backgroundRes = null,
                backgroundColor = null,
                isCustom = false
            )
        }

        private fun loadTemplateExerciseData(exerciseId: String) {
            // Cancel any existing job for this exercise
            activeExerciseDataJobs["template_$exerciseId"]?.cancel()

            activeExerciseDataJobs["template_$exerciseId"] = scope.launch {
                observeExerciseByIdUseCase(exerciseId).flowOn(Dispatchers.IO).collect { exercise ->
                    dispatch(Msg.TemplateExerciseDataLoaded(exerciseId, exercise))
                }
            }
        }

        private fun deleteExercise(workoutExerciseId: String) {
            scope.launch {
                // Get sessionId from workoutExercise
                val workoutExercise = observeWorkoutExerciseByIdUseCase(workoutExerciseId).firstOrNull()
                val sessionId = workoutExercise?.workoutSessionId

                val result = deleteWorkoutExerciseUseCase(workoutExerciseId)
                if (result.isSuccess) {
                    // Unlink from template since we deleted an exercise
                    if (sessionId != null) {
                        unlinkSessionFromTemplateUseCase(sessionId)
                    }

                    dispatch(Msg.ExerciseDeleted(workoutExerciseId))
                }
            }
        }

        private fun completeWorkout(getState: () -> MainScreenStore.State) {
            val currentState = getState()
            val sessionId = currentState.workoutSession?.id

            if (sessionId != null) {
                scope.launch {
                    val result = completeWorkoutSessionUseCase(sessionId)
                    if (result.isSuccess) {
                        dispatch(Msg.WorkoutCompleted(currentState.workoutSession))
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
                is Msg.InitializingChanged -> copy(isInitializing = message.isInitializing)
                is Msg.TemplatesLoaded -> copy(applicableTemplates = message.templates)
                is Msg.TemplateExerciseDataLoaded -> copy(
                    templateExerciseData = templateExerciseData + (message.exerciseId to message.exercise)
                )
                is Msg.ExerciseDeleted -> {
                    // Remove exercise from workout session
                    val updatedSession = workoutSession?.let { session ->
                        val updatedExercises = session.exercises.filterNot { it.id == message.workoutExerciseId }
                        session.copy(exercises = updatedExercises)
                    }

                    // Remove exercise data
                    val exerciseIdToRemove = exerciseData.entries
                        .firstOrNull { it.value.workoutExercise.id == message.workoutExerciseId }?.key

                    val updatedExerciseData = if (exerciseIdToRemove != null) {
                        exerciseData - exerciseIdToRemove
                    } else {
                        exerciseData
                    }

                    copy(
                        workoutSession = updatedSession,
                        exerciseData = updatedExerciseData
                    )
                }
                is Msg.WorkoutCompleted -> copy(workoutSession = message.session)
                is Msg.ExerciseDataCleared -> copy(exerciseData = emptyMap())
                is Msg.TemplateExerciseDataCleared -> copy(templateExerciseData = emptyMap())
            }
    }
}
