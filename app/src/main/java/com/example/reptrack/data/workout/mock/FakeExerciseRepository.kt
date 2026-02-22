package com.example.reptrack.data.workout.mock

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake repository for exercises with mock data.
 * Used for UI development and testing.
 */
class FakeExerciseRepository : ExerciseRepository {

    private val mockExercises = createMockExercises()

    override suspend fun observeExerciseById(exerciseId: String): Flow<Exercise> {
        return flowOf(
            mockExercises.find { it.id == exerciseId }
                ?: throw NoSuchElementException("Exercise with id $exerciseId not found")
        )
    }

    override suspend fun observeAllExercises(): Flow<List<Exercise>> {
        return flowOf(mockExercises)
    }

    override suspend fun createExercise(exercise: Exercise): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateExercise(exercise: Exercise): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteExercise(exerciseId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun observeWorkoutExerciseById(exerciseId: String): Flow<WorkoutExercise> {
        return flowOf(
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
        return flowOf(createMockSets())
    }

    private fun createMockExercises(): List<Exercise> = listOf(
        // CHEST exercises
        Exercise(
            id = "bench_press",
            name = "Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#FF6B6B",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),
        Exercise(
            id = "incline_bench_press",
            name = "Incline Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#FF6B6B",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),
        Exercise(
            id = "dumbbell_fly",
            name = "Dumbbell Fly",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#FF6B6B",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),
        Exercise(
            id = "cable_fly",
            name = "Cable Fly",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#FF6B6B",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),
        Exercise(
            id = "push_ups",
            name = "Push Ups",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#FF6B6B",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),

        // BACK exercises
        Exercise(
            id = "deadlift",
            name = "Deadlift",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#4ECDC4",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F7",
            isCustom = false
        ),
        Exercise(
            id = "pull_ups",
            name = "Pull Ups",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#4ECDC4",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F7",
            isCustom = false
        ),
        Exercise(
            id = "barbell_row",
            name = "Barbell Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#4ECDC4",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F7",
            isCustom = false
        ),
        Exercise(
            id = "lat_pulldown",
            name = "Lat Pulldown",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#4ECDC4",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F7",
            isCustom = false
        ),
        Exercise(
            id = "seated_cable_row",
            name = "Seated Cable Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#4ECDC4",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F7",
            isCustom = false
        ),

        // LEGS exercises
        Exercise(
            id = "squat",
            name = "Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#95E1D3",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F4",
            isCustom = false
        ),
        Exercise(
            id = "leg_press",
            name = "Leg Press",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#95E1D3",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F4",
            isCustom = false
        ),
        Exercise(
            id = "lunges",
            name = "Lunges",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#95E1D3",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F4",
            isCustom = false
        ),
        Exercise(
            id = "leg_curl",
            name = "Leg Curl",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#95E1D3",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F4",
            isCustom = false
        ),
        Exercise(
            id = "calf_raises",
            name = "Calf Raises",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#95E1D3",
            backgroundImageUrl = null,
            backgroundColor = "#E5F9F4",
            isCustom = false
        ),

        // ARMS exercises
        Exercise(
            id = "bicep_curls",
            name = "Bicep Curls",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#F38181",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),
        Exercise(
            id = "tricep_pushdown",
            name = "Tricep Pushdown",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#F38181",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),
        Exercise(
            id = "hammer_curls",
            name = "Hammer Curls",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#F38181",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),
        Exercise(
            id = "overhead_press",
            name = "Overhead Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#F38181",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),
        Exercise(
            id = "skull_crushers",
            name = "Skull Crushers",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#F38181",
            backgroundImageUrl = null,
            backgroundColor = "#FFE5E5",
            isCustom = false
        ),

        // ABS exercises
        Exercise(
            id = "crunches",
            name = "Crunches",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#AA96DA",
            backgroundImageUrl = null,
            backgroundColor = "#F0EDFF",
            isCustom = false
        ),
        Exercise(
            id = "plank",
            name = "Plank",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#AA96DA",
            backgroundImageUrl = null,
            backgroundColor = "#F0EDFF",
            isCustom = false
        ),
        Exercise(
            id = "leg_raises",
            name = "Leg Raises",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#AA96DA",
            backgroundImageUrl = null,
            backgroundColor = "#F0EDFF",
            isCustom = false
        ),
        Exercise(
            id = "russian_twist",
            name = "Russian Twist",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#AA96DA",
            backgroundImageUrl = null,
            backgroundColor = "#F0EDFF",
            isCustom = false
        ),
        Exercise(
            id = "cable_crunch",
            name = "Cable Crunch",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconUrl = null,
            iconColor = "#AA96DA",
            backgroundImageUrl = null,
            backgroundColor = "#F0EDFF",
            isCustom = false
        ),

        // CARDIO exercises
        Exercise(
            id = "treadmill",
            name = "Treadmill",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconUrl = null,
            iconColor = "#FCBAD3",
            backgroundImageUrl = null,
            backgroundColor = "#FFF0F5",
            isCustom = false
        ),
        Exercise(
            id = "cycling",
            name = "Cycling",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconUrl = null,
            iconColor = "#FCBAD3",
            backgroundImageUrl = null,
            backgroundColor = "#FFF0F5",
            isCustom = false
        ),
        Exercise(
            id = "elliptical",
            name = "Elliptical",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconUrl = null,
            iconColor = "#FCBAD3",
            backgroundImageUrl = null,
            backgroundColor = "#FFF0F5",
            isCustom = false
        ),
        Exercise(
            id = "rowing_machine",
            name = "Rowing Machine",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconUrl = null,
            iconColor = "#FCBAD3",
            backgroundImageUrl = null,
            backgroundColor = "#FFF0F5",
            isCustom = false
        ),
        Exercise(
            id = "jump_rope",
            name = "Jump Rope",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconUrl = null,
            iconColor = "#FCBAD3",
            backgroundImageUrl = null,
            backgroundColor = "#FFF0F5",
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
