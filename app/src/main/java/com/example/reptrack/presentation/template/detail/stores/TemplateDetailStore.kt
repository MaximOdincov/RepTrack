package com.example.reptrack.presentation.template.detail.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.HandledError
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.onError
import com.example.reptrack.core.extensions.safeCollect
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.usecases.exercises.ObserveAllExercisesUseCase
import com.example.reptrack.domain.workout.usecases.templates.CreateWorkoutTemplateUseCase
import com.example.reptrack.domain.workout.usecases.templates.DeleteWorkoutTemplateUseCase
import com.example.reptrack.domain.workout.usecases.templates.ObserveWorkoutTemplateByIdUseCase
import com.example.reptrack.domain.workout.usecases.templates.UpdateWorkoutTemplateUseCase
import com.example.reptrack.presentation.exercise.detail.stores.CustomizationSheetMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import io.github.aakira.napier.Napier

interface TemplateDetailStore : Store<TemplateDetailStore.Intent, TemplateDetailStore.State, TemplateDetailStore.Label> {

    sealed interface Intent {
        data class Initialize(val templateId: String?, val mode: TemplateDetailMode) : Intent
        data class NameChanged(val name: String) : Intent
        data class DescriptionChanged(val description: String) : Intent
        data class IconSelected(val iconRes: Int) : Intent
        data class ColorSelected(val color: String) : Intent
        data class SheetModeChanged(val mode: CustomizationSheetMode) : Intent
        object OpenCustomizationSheet : Intent
        object CloseCustomizationSheet : Intent
        data class AddExerciseToTemplate(val exerciseId: String) : Intent
        data class RemoveExerciseFromTemplate(val exerciseId: String) : Intent
        data class MoveExerciseUp(val index: Int) : Intent
        data class MoveExerciseDown(val index: Int) : Intent
        data class ToggleScheduleDay(val weekNumber: Int, val day: Int) : Intent
        object SaveAndExit : Intent
        object ExitWithCheck : Intent
    }

    enum class TemplateDetailMode {
        CREATE_MODE,
        EDIT_MODE,
        VIEW_MODE
    }

    data class State(
        val mode: TemplateDetailMode = TemplateDetailMode.VIEW_MODE,
        val isLoading: Boolean = false,
        val templateId: String? = null,
        val name: String = "",
        val description: String = "",
        val exerciseIds: List<String> = emptyList(),
        val iconRes: Int? = null,
        val iconColor: String? = null,
        val isCustomizationSheetVisible: Boolean = false,
        val sheetMode: CustomizationSheetMode = CustomizationSheetMode.ICON,
        val draftIconRes: Int? = null,
        val draftIconColor: String? = null,
        val availableExercises: List<Exercise> = emptyList(),
        val isInitialized: Boolean = false,
        val schedule: com.example.reptrack.domain.workout.entities.TemplateSchedule? = null
    )

    sealed interface Label {
        object NavigateBack : Label
        object ShowSavedToast : Label
        data class ShowError(val message: String) : Label
    }
}

internal class TemplateDetailStoreFactoryImpl(
    private val storeFactory: StoreFactory,
    private val createTemplateUseCase: CreateWorkoutTemplateUseCase,
    private val updateTemplateUseCase: UpdateWorkoutTemplateUseCase,
    private val deleteTemplateUseCase: DeleteWorkoutTemplateUseCase,
    private val observeTemplateByIdUseCase: ObserveWorkoutTemplateByIdUseCase,
    private val observeAllExercisesUseCase: ObserveAllExercisesUseCase,
    private val errorHandler: ErrorHandler
) {

    fun create(): TemplateDetailStore =
        object : TemplateDetailStore, Store<TemplateDetailStore.Intent, TemplateDetailStore.State, TemplateDetailStore.Label> by storeFactory.create(
            name = "TemplateDetailStore",
            initialState = TemplateDetailStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        object LoadAvailableExercises : Action
    }

    private sealed interface Msg {
        data class ModeInitialized(val mode: TemplateDetailStore.TemplateDetailMode) : Msg
        data class TemplateIdChanged(val templateId: String?) : Msg
        object LoadingStarted : Msg
        object LoadingFinished : Msg
        object InitializationCompleted : Msg
        data class TemplateLoaded(val template: WorkoutTemplate) : Msg
        data class NameChanged(val name: String) : Msg
        data class DescriptionChanged(val description: String) : Msg
        data class ExerciseIdsChanged(val exerciseIds: List<String>) : Msg
        data class IconLoaded(val iconRes: Int?, val iconColor: String?) : Msg
        object CustomizationSheetOpened : Msg
        object CustomizationSheetClosed : Msg
        data class SheetModeChanged(val mode: CustomizationSheetMode) : Msg
        data class DraftIconChanged(val iconRes: Int?, val iconColor: String?) : Msg
        data class IconApplied(val iconRes: Int?, val iconColor: String?) : Msg
        data class AvailableExercisesLoaded(val exercises: List<Exercise>) : Msg
        data class ExerciseAdded(val exerciseId: String) : Msg
        data class ExerciseRemoved(val exerciseId: String) : Msg
        data class ScheduleChanged(val schedule: com.example.reptrack.domain.workout.entities.TemplateSchedule?) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            Napier.i("Bootstrapper: invoked", tag = "TemplateDetailStore")
            dispatch(Action.LoadAvailableExercises)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<TemplateDetailStore.Intent, Action, TemplateDetailStore.State, Msg, TemplateDetailStore.Label>() {
        private var debounceJob: Job? = null

        override fun executeIntent(intent: TemplateDetailStore.Intent, getState: () -> TemplateDetailStore.State) {
            Napier.i("executeIntent: intent=$intent, state.templateId=${getState().templateId}, state.exerciseIds=${getState().exerciseIds}", tag = "TemplateDetailStore")
            when (intent) {
                is TemplateDetailStore.Intent.Initialize -> {
                    val state = getState()
                    Napier.i(
                        "Initialize: templateId=${intent.templateId}, mode=${intent.mode}, " +
                        "state.templateId=${state.templateId}, state.isInitialized=${state.isInitialized}, " +
                        "state.isLoading=${state.isLoading}, state.exerciseIds=${state.exerciseIds}",
                        tag = "TemplateDetailStore"
                    )

                    // Skip if already initialized with the same templateId
                    if (state.isInitialized && state.templateId == intent.templateId) {
                        Napier.i("Initialize: Already initialized with same templateId, skipping", tag = "TemplateDetailStore")
                        return
                    }

                    dispatch(Msg.ModeInitialized(intent.mode))
                    dispatch(Msg.TemplateIdChanged(intent.templateId))

                    // If creating new template, we're done loading
                    if (intent.mode == TemplateDetailStore.TemplateDetailMode.CREATE_MODE) {
                        Napier.i("Initialize: CREATE_MODE - skipping loading, marking as finished", tag = "TemplateDetailStore")
                        dispatch(Msg.LoadingFinished)
                        dispatch(Msg.InitializationCompleted)
                    } else {
                        // Check if we need to load template:
                        // 1. Not initialized yet OR
                        // 2. Different templateId
                        val shouldLoad = !state.isInitialized || state.templateId != intent.templateId

                        Napier.i("Initialize: shouldLoad=$shouldLoad", tag = "TemplateDetailStore")

                        if (shouldLoad && intent.templateId != null) {
                            loadTemplate(intent.templateId)
                        } else {
                            Napier.i("Initialize: No load needed, marking as finished", tag = "TemplateDetailStore")
                            dispatch(Msg.LoadingFinished)
                            if (!state.isInitialized) {
                                dispatch(Msg.InitializationCompleted)
                            }
                        }
                    }
                }
                is TemplateDetailStore.Intent.NameChanged -> {
                    dispatch(Msg.NameChanged(intent.name))
                    // Debounced autosave for name changes (only in EDIT_MODE)
                    val state = getState()
                    if (state.templateId != null) {
                        debouncedAutoSave(state)
                    }
                }
                is TemplateDetailStore.Intent.DescriptionChanged -> {
                    dispatch(Msg.DescriptionChanged(intent.description))
                    // Debounced autosave for description changes (only in EDIT_MODE)
                    val state = getState()
                    if (state.templateId != null) {
                        debouncedAutoSave(state)
                    }
                }
                is TemplateDetailStore.Intent.IconSelected -> {
                    dispatch(Msg.DraftIconChanged(intent.iconRes, getState().draftIconColor))
                }
                is TemplateDetailStore.Intent.ColorSelected -> {
                    dispatch(Msg.DraftIconChanged(getState().draftIconRes, intent.color))
                }
                is TemplateDetailStore.Intent.SheetModeChanged -> {
                    dispatch(Msg.SheetModeChanged(intent.mode))
                }
                TemplateDetailStore.Intent.OpenCustomizationSheet -> {
                    // Initialize draft values with current values
                    val state = getState()
                    dispatch(Msg.DraftIconChanged(state.iconRes, state.iconColor))
                    dispatch(Msg.CustomizationSheetOpened)
                }
                TemplateDetailStore.Intent.CloseCustomizationSheet -> {
                    // Apply draft values to actual icon and auto-save (only in EDIT_MODE)
                    val state = getState()
                    val newIconRes = state.draftIconRes
                    val newIconColor = state.draftIconColor
                    dispatch(Msg.IconApplied(newIconRes, newIconColor))
                    dispatch(Msg.CustomizationSheetClosed)
                    // Immediate autosave with new values (only if we have a templateId)
                    if (state.templateId != null) {
                        autoSaveWithIcon(newIconRes, newIconColor, state, showToast = false)
                    }
                }
                is TemplateDetailStore.Intent.AddExerciseToTemplate -> {
                    val state = getState()
                    val updatedExerciseIds = state.exerciseIds + intent.exerciseId

                    // Debug logging
                    io.github.aakira.napier.Napier.i(
                        "AddExerciseToTemplate: exerciseId=${intent.exerciseId}, " +
                        "current exerciseIds=${state.exerciseIds}, " +
                        "updated exerciseIds=$updatedExerciseIds, templateId=${state.templateId}",
                        tag = "TemplateDetailStore"
                    )

                    dispatch(Msg.ExerciseAdded(intent.exerciseId))
                    // Only auto-save if we have a templateId (EDIT_MODE)
                    if (state.templateId != null) {
                        autoSaveWithExerciseIds(updatedExerciseIds, state, showToast = false)
                    }
                }
                is TemplateDetailStore.Intent.RemoveExerciseFromTemplate -> {
                    val state = getState()
                    val updatedExerciseIds = state.exerciseIds - intent.exerciseId
                    dispatch(Msg.ExerciseRemoved(intent.exerciseId))
                    // Only auto-save if we have a templateId (EDIT_MODE)
                    if (state.templateId != null) {
                        autoSaveWithExerciseIds(updatedExerciseIds, state, showToast = false)
                    }
                }
                is TemplateDetailStore.Intent.MoveExerciseUp -> {
                    val state = getState()
                    val currentIndex = intent.index
                    if (currentIndex > 0 && currentIndex < state.exerciseIds.size) {
                        val updatedExerciseIds = state.exerciseIds.toMutableList()
                        // Swap with previous item
                        updatedExerciseIds[currentIndex] = updatedExerciseIds[currentIndex - 1]
                        updatedExerciseIds[currentIndex - 1] = state.exerciseIds[currentIndex]
                        dispatch(Msg.ExerciseIdsChanged(updatedExerciseIds))
                        // Auto-save
                        if (state.templateId != null) {
                            autoSaveWithExerciseIds(updatedExerciseIds, state, showToast = false)
                        }
                    }
                }
                is TemplateDetailStore.Intent.MoveExerciseDown -> {
                    val state = getState()
                    val currentIndex = intent.index
                    if (currentIndex >= 0 && currentIndex < state.exerciseIds.size - 1) {
                        val updatedExerciseIds = state.exerciseIds.toMutableList()
                        // Swap with next item
                        updatedExerciseIds[currentIndex] = updatedExerciseIds[currentIndex + 1]
                        updatedExerciseIds[currentIndex + 1] = state.exerciseIds[currentIndex]
                        dispatch(Msg.ExerciseIdsChanged(updatedExerciseIds))
                        // Auto-save
                        if (state.templateId != null) {
                            autoSaveWithExerciseIds(updatedExerciseIds, state, showToast = false)
                        }
                    }
                }
                is TemplateDetailStore.Intent.ToggleScheduleDay -> {
                    val state = getState()
                    val currentSchedule = state.schedule ?: com.example.reptrack.domain.workout.entities.TemplateSchedule(
                        week1Days = emptySet(),
                        week2Days = emptySet()
                    )

                    val updatedWeekDays = when (intent.weekNumber) {
                        1 -> currentSchedule.week1Days.toMutableSet()
                        2 -> currentSchedule.week2Days.toMutableSet()
                        else -> return@executeIntent
                    }

                    if (intent.day in updatedWeekDays) {
                        updatedWeekDays -= intent.day
                        Napier.i("ToggleScheduleDay: REMOVING week=${intent.weekNumber}, day=${intent.day}, newDays=$updatedWeekDays", tag = "TemplateDetailStore")
                    } else {
                        updatedWeekDays += intent.day
                        Napier.i("ToggleScheduleDay: ADDING week=${intent.weekNumber}, day=${intent.day}, newDays=$updatedWeekDays", tag = "TemplateDetailStore")
                    }

                    val newSchedule = when (intent.weekNumber) {
                        1 -> currentSchedule.copy(week1Days = updatedWeekDays)
                        2 -> currentSchedule.copy(week2Days = updatedWeekDays)
                        else -> currentSchedule
                    }

                    Napier.i("ToggleScheduleDay: Final schedule - week1=${newSchedule.week1Days}, week2=${newSchedule.week2Days}", tag = "TemplateDetailStore")
                    dispatch(Msg.ScheduleChanged(newSchedule))

                    // Auto-save schedule changes (only in EDIT_MODE)
                    if (state.templateId != null) {
                        debouncedAutoSave(state.copy(schedule = newSchedule))
                    }
                }
                TemplateDetailStore.Intent.SaveAndExit -> {
                    saveTemplate(getState(), showToast = true)
                }
                TemplateDetailStore.Intent.ExitWithCheck -> {
                    val state = getState()
                    // Check if template is empty (no name, no description, no exercises)
                    val isEmpty = state.name.isBlank() &&
                                  state.description.isBlank() &&
                                  state.exerciseIds.isEmpty()

                    if (isEmpty && state.templateId != null) {
                        // Delete empty template and exit
                        scope.launch {
                            deleteTemplateUseCase(state.templateId)
                            publish(TemplateDetailStore.Label.NavigateBack)
                        }
                    } else {
                        // Just exit without saving
                        publish(TemplateDetailStore.Label.NavigateBack)
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> TemplateDetailStore.State) {
            Napier.i("executeAction: action=$action", tag = "TemplateDetailStore")
            when (action) {
                is Action.LoadAvailableExercises -> {
                    loadAvailableExercises()
                }
            }
        }

        private fun debouncedAutoSave(state: TemplateDetailStore.State) {
            // Cancel previous job
            debounceJob?.cancel()
            // Start new debounced job
            debounceJob = scope.launch {
                delay(1000) // 1 second debounce
                autoSave(state.copy(), showToast = false)
            }
        }

        private fun autoSave(state: TemplateDetailStore.State, showToast: Boolean) {
            scope.launch {
                val muscleGroups = calculateMuscleGroups(state.exerciseIds, state.availableExercises)

                val template = WorkoutTemplate(
                    id = state.templateId ?: ("template_${System.currentTimeMillis()}"),
                    name = state.name,
                    description = state.description,
                    iconId = "custom", // Default iconId for custom templates
                    exerciseIds = state.exerciseIds,
                    iconRes = state.iconRes,
                    iconColor = state.iconColor,
                    muscleGroups = muscleGroups,
                    isCustom = true,
                    schedule = state.schedule
                )

                // If this is a new template, create it first
                val templateId = if (state.templateId == null) {
                    val result = createTemplateUseCase(template)
                    if (result.isSuccess) {
                        template.id
                    } else {
                        errorHandler.log(
                            result.exceptionOrNull()!!,
                            ErrorContext(
                                screen = "TemplateDetail",
                                action = "AutoSave",
                                entityId = template.id,
                                additionalInfo = mapOf("silent" to true)
                            )
                        )
                        return@launch
                    }
                } else {
                    state.templateId
                }

                // Then update it
                val updatedTemplate = template.copy(id = templateId)
                val result = updateTemplateUseCase(updatedTemplate)

                // Only update templateId in state if it was just created
                if (state.templateId == null && result.isSuccess) {
                    dispatch(Msg.TemplateIdChanged(templateId))
                } else if (result.isFailure) {
                    errorHandler.log(
                        result.exceptionOrNull()!!,
                        ErrorContext(
                            screen = "TemplateDetail",
                            action = "AutoSave",
                            entityId = templateId,
                            additionalInfo = mapOf("silent" to true)
                        )
                    )
                }
            }
        }

        private fun autoSaveWithExerciseIds(
            exerciseIds: List<String>,
            state: TemplateDetailStore.State,
            showToast: Boolean
        ) {
            scope.launch {
                val muscleGroups = calculateMuscleGroups(exerciseIds, state.availableExercises)

                val template = WorkoutTemplate(
                    id = state.templateId ?: ("template_${System.currentTimeMillis()}"),
                    name = state.name,
                    description = state.description,
                    iconId = "custom",
                    exerciseIds = exerciseIds,
                    iconRes = state.iconRes,
                    iconColor = state.iconColor,
                    muscleGroups = muscleGroups,
                    isCustom = true,
                    schedule = state.schedule
                )

                // If this is a new template, create it first
                val templateId = if (state.templateId == null) {
                    val result = createTemplateUseCase(template)
                    if (result.isSuccess) {
                        template.id
                    } else {
                        errorHandler.log(
                            result.exceptionOrNull()!!,
                            ErrorContext(
                                screen = "TemplateDetail",
                                action = "AutoSaveWithExerciseIds",
                                entityId = template.id,
                                additionalInfo = mapOf("silent" to true)
                            )
                        )
                        return@launch
                    }
                } else {
                    state.templateId
                }

                // Then update it
                val updatedTemplate = template.copy(id = templateId)
                val result = updateTemplateUseCase(updatedTemplate)

                // Only update templateId in state if it was just created
                if (state.templateId == null && result.isSuccess) {
                    dispatch(Msg.TemplateIdChanged(templateId))
                } else if (result.isFailure) {
                    errorHandler.log(
                        result.exceptionOrNull()!!,
                        ErrorContext(
                            screen = "TemplateDetail",
                            action = "AutoSaveWithExerciseIds",
                            entityId = templateId,
                            additionalInfo = mapOf("silent" to true)
                        )
                    )
                }
            }
        }

        private fun autoSaveWithIcon(
            iconRes: Int?,
            iconColor: String?,
            state: TemplateDetailStore.State,
            showToast: Boolean
        ) {
            scope.launch {
                val muscleGroups = calculateMuscleGroups(state.exerciseIds, state.availableExercises)

                val template = WorkoutTemplate(
                    id = state.templateId ?: ("template_${System.currentTimeMillis()}"),
                    name = state.name,
                    description = state.description,
                    iconId = "custom",
                    exerciseIds = state.exerciseIds,
                    iconRes = iconRes,
                    iconColor = iconColor,
                    muscleGroups = muscleGroups,
                    isCustom = true,
                    schedule = state.schedule
                )

                // If this is a new template, create it first
                val templateId = if (state.templateId == null) {
                    val result = createTemplateUseCase(template)
                    if (result.isSuccess) {
                        template.id
                    } else {
                        errorHandler.log(
                            result.exceptionOrNull()!!,
                            ErrorContext(
                                screen = "TemplateDetail",
                                action = "AutoSaveWithIcon",
                                entityId = template.id,
                                additionalInfo = mapOf("silent" to true)
                            )
                        )
                        return@launch
                    }
                } else {
                    state.templateId
                }

                // Then update it
                val updatedTemplate = template.copy(id = templateId)
                val result = updateTemplateUseCase(updatedTemplate)

                // Only update templateId in state if it was just created
                if (state.templateId == null && result.isSuccess) {
                    dispatch(Msg.TemplateIdChanged(templateId))
                } else if (result.isFailure) {
                    errorHandler.log(
                        result.exceptionOrNull()!!,
                        ErrorContext(
                            screen = "TemplateDetail",
                            action = "AutoSaveWithIcon",
                            entityId = templateId,
                            additionalInfo = mapOf("silent" to true)
                        )
                    )
                }
            }
        }

        private fun loadAvailableExercises() {
            scope.launch {
                observeAllExercisesUseCase()
                    .safeCollect(
                        errorHandler = errorHandler,
                        context = ErrorContext(screen = "TemplateDetail", action = "LoadExercises")
                    ) { exercisesByGroup ->
                        val allExercises = exercisesByGroup.values.flatten()
                        dispatch(Msg.AvailableExercisesLoaded(allExercises))
                    }
            }
        }

        private fun loadTemplate(templateId: String) {
            scope.launch {
                Napier.i("loadTemplate: STARTED - templateId=$templateId", tag = "TemplateDetailStore")
                dispatch(Msg.LoadingStarted)
                try {
                    val template = observeTemplateByIdUseCase(templateId).first()
                    if (template != null) {
                        Napier.i("loadTemplate: SUCCESS - template loaded, sending TemplateLoaded", tag = "TemplateDetailStore")
                        dispatch(Msg.TemplateLoaded(template))
                        dispatch(Msg.InitializationCompleted)
                        dispatch(Msg.LoadingFinished)
                    } else {
                        Napier.i("loadTemplate: NOT FOUND", tag = "TemplateDetailStore")
                        publish(TemplateDetailStore.Label.ShowError("Template not found"))
                        dispatch(Msg.LoadingFinished)
                    }
                } catch (e: Exception) {
                    Napier.i("loadTemplate: ERROR - ${e.message}", tag = "TemplateDetailStore")
                    errorHandler.handle(
                        e,
                        ErrorContext(screen = "TemplateDetail", action = "LoadTemplate", entityId = templateId)
                    )
                    publish(TemplateDetailStore.Label.ShowError(e.message ?: "Failed to load template"))
                    dispatch(Msg.LoadingFinished)
                }
            }
        }

        private fun calculateMuscleGroups(
            exerciseIds: List<String>,
            exercises: List<Exercise>
        ): List<MuscleGroup> {
            return exerciseIds
                .mapNotNull { exerciseId ->
                    exercises.find { it.id == exerciseId }?.muscleGroup
                }
                .distinct()
                .sortedBy { it.name }
        }

        private fun saveTemplate(state: TemplateDetailStore.State, showToast: Boolean = true) {
            scope.launch {
                val muscleGroups = calculateMuscleGroups(state.exerciseIds, state.availableExercises)

                val template = WorkoutTemplate(
                    id = state.templateId ?: ("template_${System.currentTimeMillis()}"),
                    name = state.name,
                    description = state.description,
                    iconId = "custom", // Default iconId for custom templates
                    exerciseIds = state.exerciseIds,
                    iconRes = state.iconRes,
                    iconColor = state.iconColor,
                    muscleGroups = muscleGroups,
                    isCustom = true,
                    schedule = state.schedule
                )

                Napier.i(
                    "saveTemplate: templateId=${template.id}, schedule=${template.schedule}, " +
                    "week1=${template.schedule?.week1Days}, week2=${template.schedule?.week2Days}",
                    tag = "TemplateDetailStore"
                )

                val result = if (state.mode == TemplateDetailStore.TemplateDetailMode.CREATE_MODE || state.templateId == null) {
                    createTemplateUseCase(template)
                } else {
                    updateTemplateUseCase(template)
                }

                result.onError(
                    errorHandler = errorHandler,
                    context = ErrorContext(
                        screen = "TemplateDetail",
                        action = "SaveTemplate",
                        entityId = template.id
                    )
                ) { handledError ->
                    if (showToast) {
                        publish(TemplateDetailStore.Label.ShowError(handledError.userMessage))
                    }
                }.onSuccess {
                    if (showToast) {
                        publish(TemplateDetailStore.Label.ShowSavedToast)
                    }
                    publish(TemplateDetailStore.Label.NavigateBack)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<TemplateDetailStore.State, Msg> {
        override fun TemplateDetailStore.State.reduce(msg: Msg): TemplateDetailStore.State {
            val result = when (msg) {
                is Msg.LoadingStarted -> {
                    Napier.i("Reducer: LoadingStarted - setting isLoading=true", tag = "TemplateDetailStore")
                    copy(isLoading = true)
                }
                is Msg.LoadingFinished -> {
                    Napier.i("Reducer: LoadingFinished - setting isLoading=false", tag = "TemplateDetailStore")
                    copy(isLoading = false)
                }
                else -> null
            }

            if (result != null) return result

            Napier.i("Reducer: msg=$msg, current exerciseIds=$exerciseIds", tag = "TemplateDetailStore")
            return when (msg) {
                is Msg.ModeInitialized -> copy(mode = msg.mode)
                is Msg.TemplateIdChanged -> copy(templateId = msg.templateId)
                is Msg.InitializationCompleted -> copy(isInitialized = true)
                is Msg.TemplateLoaded -> {
                    Napier.i(
                        "TemplateLoaded: template.id=${msg.template.id}, " +
                        "template.exerciseIds=${msg.template.exerciseIds}, " +
                        "current exerciseIds=$exerciseIds",
                        tag = "TemplateDetailStore"
                    )
                    copy(
                        name = msg.template.name,
                        description = msg.template.description,
                        exerciseIds = msg.template.exerciseIds,
                        iconRes = msg.template.iconRes,
                        iconColor = msg.template.iconColor,
                        templateId = msg.template.id,
                        schedule = msg.template.schedule
                    )
                }
                is Msg.NameChanged -> copy(name = msg.name)
                is Msg.DescriptionChanged -> copy(description = msg.description)
                is Msg.ExerciseIdsChanged -> copy(exerciseIds = msg.exerciseIds)
                is Msg.IconLoaded -> copy(iconRes = msg.iconRes, iconColor = msg.iconColor)
                is Msg.CustomizationSheetOpened -> copy(isCustomizationSheetVisible = true)
                is Msg.ExerciseAdded -> {
                    Napier.i(
                        "Reducer: ExerciseAdded, old exerciseIds=$exerciseIds, new exerciseIds=${exerciseIds + msg.exerciseId}",
                        tag = "TemplateDetailStore"
                    )
                    copy(exerciseIds = exerciseIds + msg.exerciseId)
                }
                is Msg.CustomizationSheetClosed -> copy(isCustomizationSheetVisible = false)
                is Msg.SheetModeChanged -> copy(sheetMode = msg.mode)
                is Msg.DraftIconChanged -> copy(
                    draftIconRes = msg.iconRes,
                    draftIconColor = msg.iconColor
                )
                is Msg.IconApplied -> copy(
                    iconRes = msg.iconRes,
                    iconColor = msg.iconColor
                )
                is Msg.AvailableExercisesLoaded -> copy(availableExercises = msg.exercises)
                is Msg.ExerciseRemoved -> copy(exerciseIds = exerciseIds - msg.exerciseId)
                is Msg.ScheduleChanged -> {
                    Napier.i("Reducer: ScheduleChanged - schedule=${msg.schedule}, week1=${msg.schedule?.week1Days}, week2=${msg.schedule?.week2Days}", tag = "TemplateDetailStore")
                    copy(schedule = msg.schedule)
                }

                Msg.LoadingFinished -> TODO()
                Msg.LoadingStarted -> TODO()
            }
        }
    }
}
