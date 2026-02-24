package com.example.reptrack.domain.workout.usecases.exercises

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.repositories.ExerciseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveAllExercisesUseCaseTest {

    @Test
    fun `invoke groups exercises by muscle group`() = runTest {
        // Arrange
        val mockRepository = object : ExerciseRepository {
            override suspend fun observeExerciseById(exerciseId: String) = throw NotImplementedError()
            override suspend fun observeAllExercises() = flowOf(
                listOf(
                    Exercise(
                        id = "bench_press",
                        name = "Bench Press",
                        muscleGroup = MuscleGroup.CHEST,
                        type = ExerciseType.WEIGHT_REPS,
                        iconRes = null,
                        iconColor = "#FF6B6B",
                        backgroundRes = null,
                        backgroundColor = "#FFE5E5",
                        isCustom = false
                    ),
                    Exercise(
                        id = "squat",
                        name = "Squat",
                        muscleGroup = MuscleGroup.LEGS,
                        type = ExerciseType.WEIGHT_REPS,
                        iconRes = null,
                        iconColor = "#95E1D3",
                        backgroundRes = null,
                        backgroundColor = "#E5F9F4",
                        isCustom = false
                    ),
                    Exercise(
                        id = "incline_bench",
                        name = "Incline Bench",
                        muscleGroup = MuscleGroup.CHEST,
                        type = ExerciseType.WEIGHT_REPS,
                        iconRes = null,
                        iconColor = "#FF6B6B",
                        backgroundRes = null,
                        backgroundColor = "#FFE5E5",
                        isCustom = false
                    )
                )
            )
            override suspend fun createExercise(exercise: Exercise) = throw NotImplementedError()
            override suspend fun updateExercise(exercise: Exercise) = throw NotImplementedError()
            override suspend fun deleteExercise(exerciseId: String) = throw NotImplementedError()
            override suspend fun observeWorkoutExerciseById(exerciseId: String) = throw NotImplementedError()
            override suspend fun createWorkoutExercise(exercise: WorkoutExercise, workoutSessionId: String) = throw NotImplementedError()
            override suspend fun updateWorkoutExercise(exercise: WorkoutExercise) = throw NotImplementedError()
            override suspend fun deleteWorkoutExercise(exerciseId: String) = throw NotImplementedError()
            override suspend fun getLastExerciseProgress(exerciseId: String) = throw NotImplementedError()
        }

        val useCase = ObserveAllExercisesUseCase(mockRepository)

        // Act
        val result = useCase()

        // Assert
        val groupedExercises = result.first()
        assertEquals(2, groupedExercises.size)
        assertTrue(groupedExercises.containsKey(MuscleGroup.CHEST))
        assertTrue(groupedExercises.containsKey(MuscleGroup.LEGS))
        assertEquals(2, groupedExercises[MuscleGroup.CHEST]?.size)
        assertEquals(1, groupedExercises[MuscleGroup.LEGS]?.size)
    }

    @Test
    fun `invoke sorts exercises by name within each muscle group`() = runTest {
        // Arrange
        val mockRepository = object : ExerciseRepository {
            override suspend fun observeExerciseById(exerciseId: String) = throw NotImplementedError()
            override suspend fun observeAllExercises() = flowOf(
                listOf(
                    Exercise(
                        id = "incline_bench",
                        name = "Incline Bench",
                        muscleGroup = MuscleGroup.CHEST,
                        type = ExerciseType.WEIGHT_REPS,
                        iconRes = null,
                        iconColor = "#FF6B6B",
                        backgroundRes = null,
                        backgroundColor = "#FFE5E5",
                        isCustom = false
                    ),
                    Exercise(
                        id = "bench_press",
                        name = "Bench Press",
                        muscleGroup = MuscleGroup.CHEST,
                        type = ExerciseType.WEIGHT_REPS,
                        iconRes = null,
                        iconColor = "#FF6B6B",
                        backgroundRes = null,
                        backgroundColor = "#FFE5E5",
                        isCustom = false
                    )
                )
            )
            override suspend fun createExercise(exercise: Exercise) = throw NotImplementedError()
            override suspend fun updateExercise(exercise: Exercise) = throw NotImplementedError()
            override suspend fun deleteExercise(exerciseId: String) = throw NotImplementedError()
            override suspend fun observeWorkoutExerciseById(exerciseId: String) = throw NotImplementedError()
            override suspend fun createWorkoutExercise(exercise: WorkoutExercise, workoutSessionId: String) = throw NotImplementedError()
            override suspend fun updateWorkoutExercise(exercise: WorkoutExercise) = throw NotImplementedError()
            override suspend fun deleteWorkoutExercise(exerciseId: String) = throw NotImplementedError()
            override suspend fun getLastExerciseProgress(exerciseId: String) = throw NotImplementedError()
        }

        val useCase = ObserveAllExercisesUseCase(mockRepository)

        // Act
        val result = useCase()
        val chestExercises = result.first()[MuscleGroup.CHEST]!!

        // Assert
        assertEquals("Bench Press", chestExercises[0].name)
        assertEquals("Incline Bench", chestExercises[1].name)
    }

    @Test
    fun `invoke sorts muscle groups by ordinal`() = runTest {
        // Arrange
        val mockRepository = object : ExerciseRepository {
            override suspend fun observeExerciseById(exerciseId: String) = throw NotImplementedError()
            override suspend fun observeAllExercises() = flowOf(
                listOf(
                    Exercise(
                        id = "cardio_1",
                        name = "Treadmill",
                        muscleGroup = MuscleGroup.CARDIO,
                        type = ExerciseType.TIME_DISTANCE,
                        iconRes = null,
                        iconColor = "#FCBAD3",
                        backgroundRes = null,
                        backgroundColor = "#FFF0F5",
                        isCustom = false
                    ),
                    Exercise(
                        id = "chest_1",
                        name = "Bench Press",
                        muscleGroup = MuscleGroup.CHEST,
                        type = ExerciseType.WEIGHT_REPS,
                        iconRes = null,
                        iconColor = "#FF6B6B",
                        backgroundRes = null,
                        backgroundColor = "#FFE5E5",
                        isCustom = false
                    )
                )
            )
            override suspend fun createExercise(exercise: Exercise) = throw NotImplementedError()
            override suspend fun updateExercise(exercise: Exercise) = throw NotImplementedError()
            override suspend fun deleteExercise(exerciseId: String) = throw NotImplementedError()
            override suspend fun observeWorkoutExerciseById(exerciseId: String) = throw NotImplementedError()
            override suspend fun createWorkoutExercise(exercise: WorkoutExercise, workoutSessionId: String) = throw NotImplementedError()
            override suspend fun updateWorkoutExercise(exercise: WorkoutExercise) = throw NotImplementedError()
            override suspend fun deleteWorkoutExercise(exerciseId: String) = throw NotImplementedError()
            override suspend fun getLastExerciseProgress(exerciseId: String) = throw NotImplementedError()
        }

        val useCase = ObserveAllExercisesUseCase(mockRepository)

        // Act
        val result = useCase()
        val keys = result.first().keys.toList()

        // Assert
        assertEquals(MuscleGroup.CHEST, keys[0])
        assertEquals(MuscleGroup.CARDIO, keys[1])
    }

    @Test
    fun `invoke returns empty map when no exercises exist`() = runTest {
        // Arrange
        val mockRepository = object : ExerciseRepository {
            override suspend fun observeExerciseById(exerciseId: String) = throw NotImplementedError()
            override suspend fun observeAllExercises() = flowOf(emptyList<Exercise>())
            override suspend fun createExercise(exercise: Exercise) = throw NotImplementedError()
            override suspend fun updateExercise(exercise: Exercise) = throw NotImplementedError()
            override suspend fun deleteExercise(exerciseId: String) = throw NotImplementedError()
            override suspend fun observeWorkoutExerciseById(exerciseId: String) = throw NotImplementedError()
            override suspend fun createWorkoutExercise(exercise: WorkoutExercise, workoutSessionId: String) = throw NotImplementedError()
            override suspend fun updateWorkoutExercise(exercise: WorkoutExercise) = throw NotImplementedError()
            override suspend fun deleteWorkoutExercise(exerciseId: String) = throw NotImplementedError()
            override suspend fun getLastExerciseProgress(exerciseId: String) = throw NotImplementedError()
        }

        val useCase = ObserveAllExercisesUseCase(mockRepository)

        // Act
        val result = useCase()

        // Assert
        val exercises = result.first()
        assertTrue(exercises.isEmpty())
    }
}
