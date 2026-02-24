package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.WorkoutSetDb
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutSet
import com.example.reptrack.domain.workout.entities.Exercise
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExerciseMapperTest {

    @Test
    fun `ExerciseDb toDomain maps all fields correctly`() {
        // Arrange
        val exerciseDb = ExerciseDb(
            id = "bench_press",
            name = "Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = 123,
            iconColor = "#FF6B6B",
            backgroundRes = 456,
            backgroundColor = "#FFE5E5",
            isCustom = false,
            updatedAt = java.time.LocalDateTime.now(),
            deletedAt = null
        )

        // Act
        val exercise = exerciseDb.toDomain()

        // Assert
        assertEquals("bench_press", exercise.id)
        assertEquals("Bench Press", exercise.name)
        assertEquals(MuscleGroup.CHEST, exercise.muscleGroup)
        assertEquals(ExerciseType.WEIGHT_REPS, exercise.type)
        assertEquals(123, exercise.iconRes)
        assertEquals("#FF6B6B", exercise.iconColor)
        assertEquals(456, exercise.backgroundRes)
        assertEquals("#FFE5E5", exercise.backgroundColor)
        assertEquals(false, exercise.isCustom)
    }

    @Test
    fun `Exercise toDb maps all fields correctly`() {
        // Arrange
        val exercise = Exercise(
            id = "squat",
            name = "Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = "#E5F9F4",
            isCustom = false
        )

        // Act
        val exerciseDb = exercise.toDb()

        // Assert
        assertEquals("squat", exerciseDb.id)
        assertEquals("Squat", exerciseDb.name)
        assertEquals(MuscleGroup.LEGS, exerciseDb.muscleGroup)
        assertEquals(ExerciseType.WEIGHT_REPS, exerciseDb.type)
        assertNull(exerciseDb.iconRes)
        assertEquals("#95E1D3", exerciseDb.iconColor)
        assertNull(exerciseDb.backgroundRes)
        assertEquals("#E5F9F4", exerciseDb.backgroundColor)
        assertEquals(false, exerciseDb.isCustom)
        assertNull(exerciseDb.deletedAt)
    }

    @Test
    fun `WorkoutSetDb toDomain maps all fields correctly`() {
        // Arrange
        val setDb = WorkoutSetDb(
            id = "set_1",
            workoutExerciseId = "workout_ex_1",
            setOrder = 1,
            weight = 20.5f,
            reps = 12,
            isCompleted = true,
            updatedAt = java.time.LocalDateTime.now(),
            deletedAt = null
        )

        // Act
        val workoutSet = setDb.toDomain()

        // Assert
        assertEquals("set_1", workoutSet.id)
        assertEquals(1, workoutSet.index)
        assertEquals(20.5f, workoutSet.weight)
        assertEquals(12, workoutSet.reps)
        assertTrue(workoutSet.isCompleted)
    }

    @Test
    fun `WorkoutSet toDb maps all fields correctly`() {
        // Arrange
        val workoutSet = WorkoutSet(
            id = "set_2",
            index = 2,
            weight = 22.5f,
            reps = 10,
            isCompleted = false
        )

        // Act
        val setDb = workoutSet.toDb("workout_ex_1")

        // Assert
        assertEquals("set_2", setDb.id)
        assertEquals("workout_ex_1", setDb.workoutExerciseId)
        assertEquals(2, setDb.setOrder)
        assertEquals(22.5f, setDb.weight)
        assertEquals(10, setDb.reps)
        assertEquals(false, setDb.isCompleted)
        assertNull(setDb.deletedAt)
    }

    @Test
    fun `WorkoutSet with null weight maps correctly`() {
        // Arrange
        val setDb = WorkoutSetDb(
            id = "set_3",
            workoutExerciseId = "workout_ex_1",
            setOrder = 3,
            weight = null,
            reps = null,
            isCompleted = false,
            updatedAt = java.time.LocalDateTime.now(),
            deletedAt = null
        )

        // Act
        val workoutSet = setDb.toDomain()

        // Assert
        assertNull(workoutSet.weight)
        assertNull(workoutSet.reps)
        assertEquals(3, workoutSet.index)
    }
}
