package com.example.reptrack.presentation.workout_exercise.detail.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.usecases.exercises.ObserveExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.ObserveWorkoutExerciseByIdUseCase
import com.example.reptrack.domain.workout.usecases.workout_exercises.UpdateWorkoutExerciseUseCase
import com.example.reptrack.domain.workout.usecases.sessions.UpdateSessionStatusOnFirstSetUseCase
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.mappers.toDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

internal class WorkoutExerciseDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeWorkoutExerciseByIdUseCase: ObserveWorkoutExerciseByIdUseCase,
    private val observeExerciseByIdUseCase: ObserveExerciseByIdUseCase,
    private val updateWorkoutExerciseUseCase: UpdateWorkoutExerciseUseCase,
    private val updateSessionStatusOnFirstSetUseCase: UpdateSessionStatusOnFirstSetUseCase,
    private val workoutDao: WorkoutDao
) {

    fun create(): WorkoutExerciseDetailStore =
        object : WorkoutExerciseDetailStore, Store<WorkoutExerciseDetailStore.Intent, WorkoutExerciseDetailStore.State, WorkoutExerciseDetailStore.Label> by storeFactory.create(
            name = "WorkoutExerciseDetailStore",
            initialState = WorkoutExerciseDetailStore.State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data class Initialized(val workoutExerciseId: String) : Msg
        data class WorkoutExerciseLoaded(val workoutExercise: WorkoutExercise) : Msg
        data class ExerciseLoaded(val exercise: Exercise) : Msg
        data class SetUpdated(val setId: String, val set: WorkoutSet) : Msg
        data class SetsListChanged(val sets: List<WorkoutSet>) : Msg
        data class LoadingChanged(val isLoading: Boolean) : Msg
        data class SavingChanged(val isSaving: Boolean) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<WorkoutExerciseDetailStore.Intent, Nothing, WorkoutExerciseDetailStore.State, Msg, WorkoutExerciseDetailStore.Label>() {

        // Local cache for workout exercise to avoid unnecessary updates
        private var cachedWorkoutExercise: WorkoutExercise? = null
        // Separate cache for sets that's updated during runtime
        private var cachedSets: List<WorkoutSet> = emptyList()
        // Mutex to prevent race conditions during save
        private val saveMutex = Mutex()

        override fun executeIntent(intent: WorkoutExerciseDetailStore.Intent, getState: () -> WorkoutExerciseDetailStore.State) {
            when (intent) {
                is WorkoutExerciseDetailStore.Intent.Initialize -> {
                    dispatch(Msg.Initialized(intent.workoutExerciseId))
                    loadWorkoutExercise(intent.workoutExerciseId)
                }
                is WorkoutExerciseDetailStore.Intent.WeightChanged -> {
                    updateSetWeight(intent.setId, intent.weight)
                }
                is WorkoutExerciseDetailStore.Intent.RepsChanged -> {
                    updateSetReps(intent.setId, intent.reps)
                }
                is WorkoutExerciseDetailStore.Intent.AddSet -> {
                    addSet(intent.weight, intent.reps)
                }
                is WorkoutExerciseDetailStore.Intent.RemoveSet -> {
                    removeSet(intent.setId)
                }
                is WorkoutExerciseDetailStore.Intent.SaveAndExit -> {
                    saveAndExit(getState())
                }
            }
        }

        private fun loadWorkoutExercise(workoutExerciseId: String) {
            dispatch(Msg.LoadingChanged(true))
            scope.launch {
                try {
                    android.util.Log.d("WorkoutExercise", "Loading exercise: $workoutExerciseId")
                    android.util.Log.d("task1", "=== LOAD WORKOUT EXERCISE ===")
                    android.util.Log.d("task1", "workoutExerciseId=$workoutExerciseId")
                    // Use firstOrNull() to handle case when workoutExercise doesn't exist
                    val workoutExercise = observeWorkoutExerciseByIdUseCase(workoutExerciseId)
                        .flowOn(Dispatchers.IO)
                        .firstOrNull()

                    if (workoutExercise == null) {
                        android.util.Log.e("WorkoutExercise", "Exercise not found: $workoutExerciseId")
                        dispatch(Msg.LoadingChanged(false))
                        publish(WorkoutExerciseDetailStore.Label.ShowError("Exercise not found"))
                        return@launch
                    }

                    android.util.Log.d("WorkoutExercise", "Exercise loaded: ${workoutExercise.id}, sets: ${workoutExercise.sets.size}, set data: ${workoutExercise.sets.map { "${it.weight}x${it.reps}" }}")
                    android.util.Log.d("task1", "Exercise loaded: ${workoutExercise.id}, sets: ${workoutExercise.sets.size}")
                    android.util.Log.d("task1", "Set data: ${workoutExercise.sets.map { "id=${it.id}, index=${it.index}, weight=${it.weight}, reps=${it.reps}" }}")
                    cachedWorkoutExercise = workoutExercise
                    cachedSets = workoutExercise.sets
                    dispatch(Msg.WorkoutExerciseLoaded(workoutExercise))
                    dispatch(Msg.SetsListChanged(workoutExercise.sets))
                    dispatch(Msg.LoadingChanged(false))

                    loadExercise(workoutExercise.exerciseId)
                } catch (e: Exception) {
                    android.util.Log.e("WorkoutExercise", "Error loading exercise: ${e.message}")
                    android.util.Log.e("task1", "Error loading exercise: ${e.message}")
                    dispatch(Msg.LoadingChanged(false))
                    publish(WorkoutExerciseDetailStore.Label.ShowError(e.message ?: "Failed to load exercise"))
                }
            }
        }

        private fun loadExercise(exerciseId: String) {
            scope.launch {
                try {
                    // Пробуем получить Exercise из библиотеки
                    val exercise = observeExerciseByIdUseCase(exerciseId)
                        .flowOn(Dispatchers.IO)
                        .firstOrNull()

                    if (exercise != null) {
                        // Exercise найден в библиотеке
                        dispatch(Msg.ExerciseLoaded(exercise))
                    } else {
                        // Exercise удалён из библиотеки - создаём на основе данных из WorkoutExercise
                        val workoutExercise = cachedWorkoutExercise
                        if (workoutExercise != null) {
                            val fallbackExercise = createExerciseFromWorkoutExercise(workoutExercise)
                            dispatch(Msg.ExerciseLoaded(fallbackExercise))
                        }
                    }
                } catch (e: Exception) {
                    // При ошибке тоже пробуем создать из WorkoutExercise
                    val workoutExercise = cachedWorkoutExercise
                    if (workoutExercise != null) {
                        val fallbackExercise = createExerciseFromWorkoutExercise(workoutExercise)
                        dispatch(Msg.ExerciseLoaded(fallbackExercise))
                    }
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

        private fun updateSetWeight(setId: String, weight: Float) {
            val targetSet = cachedSets.find { it.id == setId } ?: return

            android.util.Log.d("task1", "=== UPDATE WEIGHT ===")
            android.util.Log.d("task1", "setId=$setId, oldWeight=${targetSet.weight}, newWeight=$weight")
            android.util.Log.d("task1", "cachedSets size BEFORE update: ${cachedSets.size}")

            if (targetSet.weight == weight || (targetSet.weight == null && weight <= 0)) {
                android.util.Log.d("task1", "Weight unchanged, skipping update")
                android.util.Log.d("task1", "=====================")
                return
            }

            val updatedSet = targetSet.copy(weight = if (weight > 0) weight else null)
            cachedSets = cachedSets.map { if (it.id == setId) updatedSet else it }

            android.util.Log.d("task1", "cachedSets size AFTER update: ${cachedSets.size}")
            android.util.Log.d("task1", "Updated set: id=${updatedSet.id}, index=${updatedSet.index}, weight=${updatedSet.weight}, reps=${updatedSet.reps}")
            android.util.Log.d("task1", "All cachedSets: ${cachedSets.map { "id=${it.id}, index=${it.index}, weight=${it.weight}, reps=${it.reps}" }}")
            android.util.Log.d("task1", "=====================")

            android.util.Log.d("WorkoutExercise", "Weight updated: setId=$setId, newWeight=$weight, cachedSets size: ${cachedSets.size}")
            dispatch(Msg.SetUpdated(setId, updatedSet))
            // Сохраняем только сеты, не всё упражнение
            saveSetsToDatabase(cachedSets)
        }

        private fun updateSetReps(setId: String, reps: Int) {
            val targetSet = cachedSets.find { it.id == setId } ?: return

            android.util.Log.d("task1", "=== UPDATE REPS ===")
            android.util.Log.d("task1", "setId=$setId, oldReps=${targetSet.reps}, newReps=$reps")
            android.util.Log.d("task1", "cachedSets size BEFORE update: ${cachedSets.size}")

            if (targetSet.reps == reps || (targetSet.reps == null && reps <= 0)) {
                android.util.Log.d("task1", "Reps unchanged, skipping update")
                android.util.Log.d("task1", "===================")
                return
            }

            val updatedSet = targetSet.copy(reps = if (reps > 0) reps else null)
            cachedSets = cachedSets.map { if (it.id == setId) updatedSet else it }

            android.util.Log.d("task1", "cachedSets size AFTER update: ${cachedSets.size}")
            android.util.Log.d("task1", "Updated set: id=${updatedSet.id}, index=${updatedSet.index}, weight=${updatedSet.weight}, reps=${updatedSet.reps}")
            android.util.Log.d("task1", "All cachedSets: ${cachedSets.map { "id=${it.id}, index=${it.index}, weight=${it.weight}, reps=${it.reps}" }}")
            android.util.Log.d("task1", "===================")

            android.util.Log.d("WorkoutExercise", "Reps updated: setId=$setId, newReps=$reps, cachedSets size: ${cachedSets.size}")
            dispatch(Msg.SetUpdated(setId, updatedSet))
            // Сохраняем только сеты, не всё упражнение
            saveSetsToDatabase(cachedSets)
        }

        private fun addSet(weight: Float?, reps: Int?) {
            val newIndex = cachedSets.maxOfOrNull { it.index }?.plus(1) ?: 1
            val newSet = WorkoutSet(
                id = UUID.randomUUID().toString(),
                index = newIndex,
                weight = weight,
                reps = reps,
                isCompleted = true
            )
            cachedSets = cachedSets + newSet

            android.util.Log.d("task1", "=== ADD SET ===")
            android.util.Log.d("task1", "New set: id=${newSet.id}, index=$newIndex, weight=$weight, reps=$reps, isCompleted=${newSet.isCompleted}")
            android.util.Log.d("task1", "cachedSets size BEFORE add: ${cachedSets.size - 1}")
            android.util.Log.d("task1", "cachedSets size AFTER add: ${cachedSets.size}")
            android.util.Log.d("task1", "All cachedSets: ${cachedSets.map { "id=${it.id}, index=${it.index}, weight=${it.weight}, reps=${it.reps}" }}")
            android.util.Log.d("task1", "================")

            android.util.Log.d("WorkoutExercise", "Set added: id=${newSet.id}, index=$newIndex, ${weight}x$reps, total sets: ${cachedSets.size}")
            dispatch(Msg.SetsListChanged(cachedSets))
            // Сохраняем только сеты, не всё упражнение
            saveSetsToDatabase(cachedSets)
        }

        private fun removeSet(setId: String) {
            android.util.Log.d("task1", "=== REMOVE SET START ===")
            android.util.Log.d("task1", "setId to remove: $setId")
            android.util.Log.d("task1", "cachedSets size BEFORE remove: ${cachedSets.size}")
            android.util.Log.d("task1", "All cachedSets: ${cachedSets.map { "id=${it.id}, index=${it.index}, weight=${it.weight}, reps=${it.reps}" }}")

            val updatedSets = cachedSets.filterNot { it.id == setId }
            val reindexedSets = updatedSets.mapIndexed { index, set ->
                set.copy(index = index + 1)
            }
            cachedSets = reindexedSets

            android.util.Log.d("task1", "cachedSets size AFTER remove: ${cachedSets.size}")
            android.util.Log.d("task1", "Reindexed sets: ${cachedSets.map { "id=${it.id}, index=${it.index}, weight=${it.weight}, reps=${it.reps}" }}")
            android.util.Log.d("task1", "======================")
            dispatch(Msg.SetsListChanged(reindexedSets))
            // Обновляем только сеты, не всё упражнение
            saveSetsToDatabase(reindexedSets)
        }

        private fun saveSetsToDatabase(sets: List<WorkoutSet>) {
            val workoutExercise = cachedWorkoutExercise ?: return

            android.util.Log.d("task1", "=== SAVE SETS ONLY ===")
            android.util.Log.d("task1", "workoutExerciseId=${workoutExercise.id}")
            android.util.Log.d("task1", "Sets to save: ${sets.size}, IDs: ${sets.map { it.id }}")
            android.util.Log.d("task1", "=================================")

            scope.launch {
                saveSetsOnlySynchronous(workoutExercise, sets).onSuccess {
                    android.util.Log.d("task1", "=== SAVE SUCCESS ===")
                    android.util.Log.d("task1", "Successfully saved ${sets.size} sets")
                    android.util.Log.d("task1", "====================")
                }.onFailure { error ->
                    android.util.Log.e("task1", "=== SAVE FAILED ===")
                    android.util.Log.e("task1", "Error: ${error.message}")
                    android.util.Log.e("task1", "==================")
                }
            }
        }

        private suspend fun saveSetsOnlySynchronous(workoutExercise: WorkoutExercise, sets: List<WorkoutSet>): Result<Unit> {
            android.util.Log.d("task1", "=== SAVE SETS ONLY START ===")
            android.util.Log.d("task1", "workoutExerciseId=${workoutExercise.id}")
            android.util.Log.d("task1", "Sets to save: ${sets.size}")
            android.util.Log.d("task1", "Stack trace:", Exception())

            return saveMutex.withLock {
                // Простой подход: удаляем ВСЕ сеты упражнения и вставляем новые
                android.util.Log.d("task1", "About to delete ALL sets for exercise: ${workoutExercise.id}")
                workoutDao.deleteSetsByExercise(workoutExercise.id)
                android.util.Log.d("task1", "DELETE completed, now inserting ${sets.size} sets")

                // Вставляем новые сеты
                val setsDb = sets.map { it.toDb(workoutExercise.id) }
                android.util.Log.d("task1", "Inserting ${setsDb.size} sets: ${setsDb.map { it.id }}")
                workoutDao.insertSets(setsDb)

                android.util.Log.d("task1", "SAVE SETS SUCCESS: ${setsDb.size} sets saved")
                Result.success(Unit)
            }
        }

        private suspend fun saveSynchronous(workoutExercise: WorkoutExercise, sets: List<WorkoutSet>): Result<Unit> {
            android.util.Log.d("task1", "=== SAVE SYNCHRONOUS START ===")
            android.util.Log.d("task1", "workoutExerciseId=${workoutExercise.id}")
            android.util.Log.d("task1", "Sets to save: ${sets.size}")
            android.util.Log.d("task1", "Stack trace:", Exception())

            return saveMutex.withLock {
                // Удаляем все сеты упражнения и вставляем новые (как в saveSetsOnlySynchronous)
                android.util.Log.d("task1", "DELETE then INSERT - deleting ALL sets for exercise: ${workoutExercise.id}")
                workoutDao.deleteSetsByExercise(workoutExercise.id)
                android.util.Log.d("task1", "DELETE completed, now inserting ${sets.size} sets")

                val setsDb = sets.map { it.toDb(workoutExercise.id) }
                android.util.Log.d("task1", "Inserting ${setsDb.size} sets")
                workoutDao.insertSets(setsDb)

                android.util.Log.d("task1", "SAVE SYNCHRONOUS SUCCESS: ${setsDb.size} sets saved")
                Result.success(Unit)
            }
        }

        private fun saveToDatabase() {
            val workoutExercise = cachedWorkoutExercise ?: return

            android.util.Log.d("task1", "=== SAVE TO DATABASE (ASYNC) ===")
            android.util.Log.d("task1", "workoutExerciseId=${workoutExercise.id}")
            android.util.Log.d("task1", "cachedSets.size: ${cachedSets.size}")
            android.util.Log.d("task1", "cachedSets data: ${cachedSets.map { "id=${it.id}, index=${it.index}, weight=${it.weight}, reps=${it.reps}" }}")
            android.util.Log.d("task1", "===================================")

            scope.launch {
                saveSynchronous(workoutExercise, cachedSets).onSuccess {
                    android.util.Log.d("task1", "=== SAVE SUCCESS ===")
                    android.util.Log.d("task1", "Successfully saved ${cachedSets.size} sets")
                    android.util.Log.d("task1", "====================")
                    android.util.Log.d("WorkoutExercise", "Auto-save SUCCESS: ${cachedSets.size} sets saved to DB")
                }.onFailure { error ->
                    android.util.Log.e("task1", "=== SAVE FAILED ===")
                    android.util.Log.e("task1", "Error: ${error.message}")
                    android.util.Log.e("task1", "Stack: ${error.stackTraceToString()}")
                    android.util.Log.e("task1", "==================")
                    android.util.Log.e("WorkoutExercise", "Auto-save FAILED: ${error.message}")
                }
            }
        }

        private fun saveAndExit(state: WorkoutExerciseDetailStore.State) {
            android.util.Log.d("task1", "=== SAVE AND EXIT START ===")
            android.util.Log.d("task1", "cachedSets size: ${cachedSets.size}")
            android.util.Log.d("task1", "cachedSets data: ${cachedSets.map { "id=${it.id}, index=${it.index}" }}")
            android.util.Log.d("task1", "============================")

            val workoutExercise = cachedWorkoutExercise ?: return

            // Update session status if this is the first completed set
            val previousSets = workoutExercise.sets
            val hadCompletedSetsBefore = previousSets.any { it.isCompleted }

            if (!hadCompletedSetsBefore && cachedSets.any { it.weight != null && it.weight!! > 0 || it.reps != null && it.reps!! > 0 }) {
                // This is the first completed set - update session status to IN_PROGRESS
                scope.launch {
                    updateSessionStatusOnFirstSetUseCase(workoutExercise.workoutSessionId)
                        .onFailure { e ->
                            android.util.Log.e("WorkoutExercise", "Failed to update session status: ${e.message}")
                        }
                }
            }

            dispatch(Msg.SavingChanged(true))
            scope.launch {
                android.util.Log.d("task1", "About to call saveSynchronous with cachedSets.size: ${cachedSets.size}")
                val result = saveSynchronous(workoutExercise, cachedSets)
                dispatch(Msg.SavingChanged(false))
                result.onSuccess {
                    android.util.Log.d("task1", "Save and exit SUCCESS: ${cachedSets.size} sets saved to DB")
                    android.util.Log.d("task1", "===============================")
                    publish(WorkoutExerciseDetailStore.Label.NavigateBack)
                }.onFailure { error ->
                    android.util.Log.e("task1", "Save and exit FAILED: ${error.message}")
                    publish(WorkoutExerciseDetailStore.Label.ShowError(error.message ?: "Failed to save"))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<WorkoutExerciseDetailStore.State, Msg> {
        override fun WorkoutExerciseDetailStore.State.reduce(message: Msg): WorkoutExerciseDetailStore.State {
            return when (message) {
                is Msg.Initialized -> copy(workoutExerciseId = message.workoutExerciseId)
                is Msg.WorkoutExerciseLoaded -> {
                    copy(workoutExercise = message.workoutExercise, sets = message.workoutExercise.sets)
                }
                is Msg.ExerciseLoaded -> copy(exercise = message.exercise)
                is Msg.SetUpdated -> {
                    copy(
                        sets = sets.map { set ->
                            if (set.id == message.setId) message.set else set
                        }
                    )
                }
                is Msg.SetsListChanged -> {
                    copy(sets = message.sets)
                }
                is Msg.LoadingChanged -> copy(isLoading = message.isLoading)
                is Msg.SavingChanged -> copy(isSaving = message.isSaving)
            }
        }
    }
}
