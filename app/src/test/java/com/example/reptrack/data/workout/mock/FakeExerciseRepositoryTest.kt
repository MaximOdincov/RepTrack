package com.example.reptrack.data.workout.mock

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
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
}
