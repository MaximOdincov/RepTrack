package com.example.reptrack.data.workout.mock

import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Fake repository for exercises with mock data.
 * Uses StateFlow to automatically notify subscribers when data changes.
 *
 * Used for UI development and testing.
 */
class FakeExerciseRepository : ExerciseRepository {
    private val availableIcons = listOf(
        R.drawable.exercise_bench_press,
        R.drawable.exercise_default_icon,
        R.drawable.exercise_icon_3,
        R.drawable.exercise_icon_4,
        R.drawable.exercis_icon_2,
        R.drawable.bench_press,
        R.drawable.barbell_energy,
        R.drawable.dumbell,
        R.drawable.bic_dumbell,
        R.drawable.dumbel_with_a_hand,
        R.drawable.weights,
        R.drawable.weight_lifting,

        R.drawable.muscle_icon_chest,
        R.drawable.muscle_icon_back,
        R.drawable.muscle_icon_legs,
        R.drawable.muscle_icon_arms,
        R.drawable.muscle_icon_abs,
        R.drawable.muscle_icon_cardio,
        R.drawable.back_muscles,
        R.drawable.muscles,
        R.drawable.abs,

        R.drawable.fitness,
        R.drawable.fitness_women,
        R.drawable.stationary_bike,
        R.drawable.treadmill,
        R.drawable.rowing,
        R.drawable.bicycle,
        R.drawable.leg_push,
        R.drawable.calories,
        R.drawable.heart_dumbell,
        R.drawable.fins,

        R.drawable.foot,
        R.drawable.leg,

        R.drawable.bear,
        R.drawable.bear_big,
        R.drawable.wolf,
        R.drawable.moose,
        R.drawable.hedgehog,
        R.drawable.elephant,
        R.drawable.deer,
        R.drawable.duck,
        R.drawable.walrus,
        R.drawable.teddy_bear,

        R.drawable.chess_sword,
        R.drawable.sword,
        R.drawable.tank,
        R.drawable.plane,
        R.drawable.robot,
        R.drawable.car,
        R.drawable.castle,

        R.drawable.trophy,
        R.drawable.star,
        R.drawable.goal,
        R.drawable.like,
        R.drawable.best_choice,
        R.drawable.rocket,
        R.drawable.idea,

        R.drawable.skull,
        R.drawable.thunder,
        R.drawable.fire,
        R.drawable.eye,
        R.drawable.heart,
        R.drawable.dna,
        R.drawable.focus,
        R.drawable.speedometr,
        R.drawable.chronometr,
        R.drawable.sand_clock,

        R.drawable.main_screen_icon,
        R.drawable.library_icon,
        R.drawable.timer_icon,
        R.drawable.profile_icon,
        R.drawable.arrow_up_icon
    )

    private var iconIndex = 0

    private val _exercises = MutableStateFlow(createMockExercises())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private fun getNextIcon(): Int {
        val icon = availableIcons[iconIndex % availableIcons.size]
        iconIndex++
        return icon
    }

    override suspend fun observeExerciseById(exerciseId: String): Flow<Exercise> {
        return _exercises.map { exercises ->
            exercises.find { it.id == exerciseId }
                ?: throw NoSuchElementException("Exercise with id $exerciseId not found")
        }
    }

    override suspend fun observeAllExercises(): Flow<List<Exercise>> {
        return _exercises
    }

    override suspend fun createExercise(exercise: Exercise): Result<Unit> {
        _exercises.update { current ->
            current + exercise
        }
        return Result.success(Unit)
    }

    override suspend fun updateExercise(exercise: Exercise): Result<Unit> {
        _exercises.update { current ->
            current.map {
                if (it.id == exercise.id) exercise
                else it
            }
        }
        return Result.success(Unit)
    }

    override suspend fun deleteExercise(exerciseId: String): Result<Unit> {
        _exercises.update { current ->
            current.filterNot { it.id == exerciseId }
        }
        return Result.success(Unit)
    }

    override suspend fun observeWorkoutExerciseById(exerciseId: String): Flow<WorkoutExercise> {
        return kotlinx.coroutines.flow.flowOf(
            WorkoutExercise(
                id = "workout_exercise_$exerciseId",
                exerciseId = exerciseId,
                sets = createMockSets(),
                restTimerSeconds = 90
            )
        )
    }

    override suspend fun createWorkoutExercise(
        exercise: WorkoutExercise,
        workoutSessionId: String
    ): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateWorkoutExercise(exercise: WorkoutExercise): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteWorkoutExercise(exerciseId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getLastExerciseProgress(exerciseId: String): Flow<List<WorkoutSet>> {
        return kotlinx.coroutines.flow.flowOf(createMockSets())
    }

    private fun createMockExercises(): List<Exercise> = listOf(
        // CHEST exercises
        Exercise(
            id = "bench_press",
            name = "Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#FF6B6B",
            backgroundColor = "#FFEBEE",
            backgroundRes = null,
            isCustom = false
        ),
        Exercise(
            id = "incline_bench_press",
            name = "Incline Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#E53935",
            backgroundRes = null,
            backgroundColor = "#FFEBEE",
            isCustom = false
        ),
        Exercise(
            id = "dumbbell_fly",
            name = "Dumbbell Fly",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#FA34D3",
            backgroundRes = null,
            backgroundColor = "#FFEBEE",
            isCustom = false
        ),
        Exercise(
            id = "cable_fly",
            name = "Cable Fly",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#D81B60",
            backgroundRes = null,
            backgroundColor = "#FFEBEE",
            isCustom = false
        ),
        Exercise(
            id = "push_ups",
            name = "Push Ups",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#C62828",
            backgroundRes = null,
            backgroundColor = "#FFEBEE",
            isCustom = false
        ),

        // BACK exercises
        Exercise(
            id = "deadlift",
            name = "Deadlift",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#009688",
            backgroundRes = null,
            backgroundColor = "#E0F7FA",
            isCustom = false
        ),
        Exercise(
            id = "pull_ups",
            name = "Pull Ups",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#4DB6AC",
            backgroundRes = null,
            backgroundColor = "#E0F7FA",
            isCustom = false
        ),
        Exercise(
            id = "barbell_row",
            name = "Barbell Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#26A69A",
            backgroundRes = null,
            backgroundColor = "#E0F7FA",
            isCustom = false
        ),
        Exercise(
            id = "lat_pulldown",
            name = "Lat Pulldown",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#00897B",
            backgroundRes = null,
            backgroundColor = "#E0F7FA",
            isCustom = false
        ),
        Exercise(
            id = "seated_cable_row",
            name = "Seated Cable Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#4ECDC4",
            backgroundRes = null,
            backgroundColor = "#E0F7FA",
            isCustom = false
        ),

        // LEGS exercises
        Exercise(
            id = "squat",
            name = "Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#43A047",
            backgroundRes = null,
            backgroundColor = "#E8F5E9",
            isCustom = false
        ),
        Exercise(
            id = "leg_press",
            name = "Leg Press",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#388E3C",
            backgroundRes = null,
            backgroundColor = "#E8F5E9",
            isCustom = false
        ),
        Exercise(
            id = "lunges",
            name = "Lunges",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#2E7D32",
            backgroundRes = null,
            backgroundColor = "#E8F5E9",
            isCustom = false
        ),
        Exercise(
            id = "leg_curl",
            name = "Leg Curl",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#1B5E20",
            backgroundRes = null,
            backgroundColor = "#E8F5E9",
            isCustom = false
        ),
        Exercise(
            id = "calf_raises",
            name = "Calf Raises",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#66BB6A",
            backgroundRes = null,
            backgroundColor = "#E8F5E9",
            isCustom = false
        ),

        // ARMS exercises
        Exercise(
            id = "bicep_curls",
            name = "Bicep Curls",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#E64A19",
            backgroundRes = null,
            backgroundColor = "#FFF3E0",
            isCustom = false
        ),
        Exercise(
            id = "tricep_pushdown",
            name = "Tricep Pushdown",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#F57C00",
            backgroundRes = null,
            backgroundColor = "#FFF3E0",
            isCustom = false
        ),
        Exercise(
            id = "hammer_curls",
            name = "Hammer Curls",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#FF9800",
            backgroundRes = null,
            backgroundColor = "#FFF3E0",
            isCustom = false
        ),
        Exercise(
            id = "overhead_press",
            name = "Overhead Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#FB8C00",
            backgroundRes = null,
            backgroundColor = "#FFF3E0",
            isCustom = false
        ),
        Exercise(
            id = "skull_crushers",
            name = "Skull Crushers",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#EF6C00",
            backgroundRes = null,
            backgroundColor = "#FFF3E0",
            isCustom = false
        ),

        // ABS exercises
        Exercise(
            id = "crunches",
            name = "Crunches",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#7E57C2",
            backgroundRes = null,
            backgroundColor = "#F3E5F5",
            isCustom = false
        ),
        Exercise(
            id = "plank",
            name = "Plank",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#9575CD",
            backgroundRes = null,
            backgroundColor = "#F3E5F5",
            isCustom = false
        ),
        Exercise(
            id = "leg_raises",
            name = "Leg Raises",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#673AB7",
            backgroundRes = null,
            backgroundColor = "#F3E5F5",
            isCustom = false
        ),
        Exercise(
            id = "russian_twist",
            name = "Russian Twist",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#5E35B1",
            backgroundRes = null,
            backgroundColor = "#F3E5F5",
            isCustom = false
        ),
        Exercise(
            id = "cable_crunch",
            name = "Cable Crunch",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = getNextIcon(),
            iconColor = "#512DA8",
            backgroundRes = null,
            backgroundColor = "#F3E5F5",
            isCustom = false
        ),

        // CARDIO exercises
        Exercise(
            id = "treadmill",
            name = "Treadmill",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = getNextIcon(),
            iconColor = "#EC407A",
            backgroundRes = null,
            backgroundColor = "#FCE4EC",
            isCustom = false
        ),
        Exercise(
            id = "cycling",
            name = "Cycling",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = getNextIcon(),
            iconColor = "#D81B60",
            backgroundRes = null,
            backgroundColor = "#FCE4EC",
            isCustom = false
        ),
        Exercise(
            id = "elliptical",
            name = "Elliptical",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = getNextIcon(),
            iconColor = "#C2185B",
            backgroundRes = null,
            backgroundColor = "#FCE4EC",
            isCustom = false
        ),
        Exercise(
            id = "rowing_machine",
            name = "Rowing Machine",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = getNextIcon(),
            iconColor = "#AD1457",
            backgroundRes = null,
            backgroundColor = "#FCE4EC",
            isCustom = false
        ),
        Exercise(
            id = "jump_rope",
            name = "Jump Rope",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = getNextIcon(),
            iconColor = "#880E4F",
            backgroundRes = null,
            backgroundColor = "#FCE4EC",
            isCustom = false
        )
    )

    private fun createMockSets(): List<WorkoutSet> = listOf(
        WorkoutSet(
            id = "set_1",
            index = 1,
            weight = 20f,
            reps = 12,
            isCompleted = true
        ),
        WorkoutSet(
            id = "set_2",
            index = 2,
            weight = 22.5f,
            reps = 10,
            isCompleted = true
        ),
        WorkoutSet(
            id = "set_3",
            index = 3,
            weight = 25f,
            reps = 8,
            isCompleted = false
        )
    )
}
