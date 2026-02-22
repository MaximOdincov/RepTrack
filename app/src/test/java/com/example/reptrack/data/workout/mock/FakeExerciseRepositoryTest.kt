package com.example.reptrack.data.workout.mock

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FakeExerciseRepositoryTest {

    private val repository = FakeExerciseRepository()

    @Test
    fun `observeAllExercises returns all exercises`() = runTest {
        // Act
        val exercises = repository.observeAllExercises().first()

        // Assert
        assertEquals(30, exercises.size) // 5 exercises per muscle group * 6 groups
    }

    @Test
    fun `observeAllExercises contains all muscle groups`() = runTest {
        // Act
        val exercises = repository.observeAllExercises().first()

        // Assert
        val muscleGroups = exercises.map { it.muscleGroup }.distinct()
        assertEquals(6, muscleGroups.size)
        assertTrue(muscleGroups.contains(MuscleGroup.CHEST))
        assertTrue(muscleGroups.contains(MuscleGroup.BACK))
        assertTrue(muscleGroups.contains(MuscleGroup.LEGS))
        assertTrue(muscleGroups.contains(MuscleGroup.ARMS))
        assertTrue(muscleGroups.contains(MuscleGroup.ABS))
        assertTrue(muscleGroups.contains(MuscleGroup.CARDIO))
    }

    @Test
    fun `observeExerciseById returns correct exercise`() = runTest {
        // Act
        val exercise = repository.observeExerciseById("bench_press").first()

        // Assert
        assertEquals("bench_press", exercise.id)
        assertEquals("Bench Press", exercise.name)
        assertEquals(MuscleGroup.CHEST, exercise.muscleGroup)
        assertEquals(ExerciseType.WEIGHT_REPS, exercise.type)
    }

    @Test
    fun `observeExerciseById throws exception for non-existent exercise`() = runTest {
        // Act & Assert
        var exceptionThrown = false
        try {
            repository.observeExerciseById("non_existent").first()
        } catch (e: NoSuchElementException) {
            exceptionThrown = true
        }
        assertTrue(exceptionThrown)
    }

    @Test
    fun `createExercise returns success`() = runTest {
        // Arrange
        val exercise = Exercise(
            id = "custom_exercise",
            name = "Custom Exercise",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFFFFF",
            backgroundRes = null,
            backgroundColor = "#000000",
            isCustom = true
        )

        // Act
        val result = repository.createExercise(exercise)

        // Assert
        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `updateExercise returns success`() = runTest {
        // Arrange
        val exercise = Exercise(
            id = "bench_press",
            name = "Updated Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF0000",
            backgroundRes = null,
            backgroundColor = "#000000",
            isCustom = false
        )

        // Act
        val result = repository.updateExercise(exercise)

        // Assert
        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `deleteExercise returns success`() = runTest {
        // Act
        val result = repository.deleteExercise("bench_press")

        // Assert
        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `observeWorkoutExerciseById returns exercise with sets`() = runTest {
        // Act
        val workoutExercise = repository.observeWorkoutExerciseById("bench_press").first()

        // Assert
        assertNotNull(workoutExercise)
        assertEquals("workout_exercise_bench_press", workoutExercise.id)
        assertEquals("bench_press", workoutExercise.exerciseId)
        assertEquals(3, workoutExercise.sets.size)
        assertEquals(90, workoutExercise.restTimerSeconds)
    }

    @Test
    fun `getLastExerciseProgress returns correct sets`() = runTest {
        // Act
        val sets = repository.getLastExerciseProgress("bench_press").first()

        // Assert
        assertEquals(3, sets.size)
        assertEquals(1, sets[0].index)
        assertEquals(20f, sets[0].weight)
        assertEquals(12, sets[0].reps)
        assertTrue(sets[0].isCompleted)

        assertEquals(2, sets[1].index)
        assertEquals(22.5f, sets[1].weight)
        assertEquals(10, sets[1].reps)
        assertTrue(sets[1].isCompleted)

        assertEquals(3, sets[2].index)
        assertEquals(25f, sets[2].weight)
        assertEquals(8, sets[2].reps)
        assertEquals(false, sets[2].isCompleted)
    }

    @Test
    fun `createWorkoutExercise returns success`() = runTest {
        // Arrange
        val workoutExercise = WorkoutExercise(
            id = "workout_ex_1",
            exerciseId = "bench_press",
            sets = emptyList(),
            restTimerSeconds = 60
        )

        // Act
        val result = repository.createWorkoutExercise(workoutExercise, "session_1")

        // Assert
        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `updateWorkoutExercise returns success`() = runTest {
        // Arrange
        val workoutExercise = WorkoutExercise(
            id = "workout_ex_1",
            exerciseId = "bench_press",
            sets = listOf(
                WorkoutSet(
                    id = "set_1",
                    index = 1,
                    weight = 30f,
                    reps = 10,
                    isCompleted = true
                )
            ),
            restTimerSeconds = 120
        )

        // Act
        val result = repository.updateWorkoutExercise(workoutExercise)

        // Assert
        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `deleteWorkoutExercise returns success`() = runTest {
        // Act
        val result = repository.deleteWorkoutExercise("workout_ex_1")

        // Assert
        assertEquals(Result.success(Unit), result)
    }
}
