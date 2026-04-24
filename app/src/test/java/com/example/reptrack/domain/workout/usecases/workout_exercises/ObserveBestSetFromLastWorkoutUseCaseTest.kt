package com.example.reptrack.domain.workout.usecases.workout_exercises

import com.example.reptrack.data.workout.mock.FakeWorkoutExerciseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ObserveBestSetFromLastWorkoutUseCaseTest {

    private val repository = FakeWorkoutExerciseRepository()

    @Before
    fun setup() {
        // Clear repository before each test to ensure test isolation
        repository.clear()
    }

    @Test
    fun `invoke returns best set from last workout`() = runTest {
        // Arrange
        val useCase = ObserveBestSetFromLastWorkoutUseCase(repository)

        val exerciseId = "bench_press"
        val exercise = repository.createMockExercise(
            id = "ex_1",
            exerciseId = exerciseId,
            weight = 20f,
            reps = 12,
            setsCount = 3
        )
        repository.addMockExercise("session_1", exercise)

        // Act
        val result = useCase(exerciseId).first()

        // Assert
        assertNotNull(result)
        // The mock creates sets with increasing weight (20, 22.5, 25)
        assertEquals(25f, result.weight)
        assertEquals(10, result.reps)
    }

    @Test
    fun `invoke returns null when no previous workout exists`() = runTest {
        // Arrange
        val useCase = ObserveBestSetFromLastWorkoutUseCase(repository)

        val exerciseId = "bench_press"

        // Act
        val result = useCase(exerciseId).first()

        // Assert
        assertNull(result)
    }

    @Test
    fun `invoke filters only completed sets`() = runTest {
        // Arrange
        val useCase = ObserveBestSetFromLastWorkoutUseCase(repository)

        val exerciseId = "bench_press"

        // Create exercise directly with custom sets to have full control
        val exercise = com.example.reptrack.domain.workout.entities.WorkoutExercise(
            id = "ex_1",
            workoutSessionId = "session_1",
            exerciseId = exerciseId,
            exerciseName = "Bench Press",
            muscleGroup = com.example.reptrack.domain.workout.entities.MuscleGroup.CHEST,
            exerciseType = com.example.reptrack.domain.workout.entities.ExerciseType.WEIGHT_REPS,
            iconRes = null,
            sets = listOf(
                com.example.reptrack.domain.workout.entities.WorkoutSet(
                    id = "set_1",
                    index = 1,
                    weight = 100f,
                    reps = 5,
                    isCompleted = false  // Not completed - heaviest but doesn't count
                ),
                com.example.reptrack.domain.workout.entities.WorkoutSet(
                    id = "set_2",
                    index = 2,
                    weight = 80f,
                    reps = 8,
                    isCompleted = true
                )
            ),
            restTimerSeconds = 90
        )
        repository.addMockExercise("session_1", exercise)

        // Act
        val result = useCase(exerciseId).first()

        // Assert
        assertNotNull(result)
        // Should return the completed set (80f), not the heavier incomplete one (100f)
        assertEquals(80f, result.weight)
        assertEquals(8, result.reps)
    }

    @Test
    fun `invoke returns set with maximum weight`() = runTest {
        // Arrange
        val useCase = ObserveBestSetFromLastWorkoutUseCase(repository)

        val exerciseId = "squat"
        val exercise = repository.createMockExercise(
            id = "ex_1",
            exerciseId = exerciseId,
            weight = 100f,
            reps = 10,
            setsCount = 5
        )
        repository.addMockExercise("session_1", exercise)

        // Act
        val result = useCase(exerciseId).first()

        // Assert
        assertNotNull(result)
        // With base weight 100f and 5 sets, weights are: 100, 102.5, 105, 107.5, 110
        assertEquals(110f, result.weight)
        assertEquals(6, result.reps) // 10 - 4 = 6
    }
}
