package com.example.reptrack.data.workout.repositories

import com.example.reptrack.data.local.aggregates.WorkoutExerciseWithSets
import com.example.reptrack.data.local.aggregates.WorkoutSessionWithExercises
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.data.local.models.WorkoutSetDb
import com.example.reptrack.domain.workout.WorkoutExercise
import com.example.reptrack.domain.workout.WorkoutSession
import com.example.reptrack.domain.workout.WorkoutSet
import com.example.reptrack.domain.workout.WorkoutStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkoutSessionRepositoryImplTest {

    private lateinit var repository: WorkoutSessionRepositoryImpl
    private val workoutDao: WorkoutDao = mockk()

    @Before
    fun setUp() {
        repository = WorkoutSessionRepositoryImpl(workoutDao)
    }

    // ============ observeSessionById Tests ============

    @Test
    fun `observeSessionById returns session when exists`() = runTest {
        // Arrange
        val sessionId = "session123"
        val sessionDb = createMockSessionDb(sessionId)
        val exerciseWithSets = WorkoutExerciseWithSets(
            exercise = createMockExerciseDb("ex1"),
            sets = emptyList()
        )
        val sessionWithExercises = WorkoutSessionWithExercises(
            session = sessionDb,
            exercises = listOf(exerciseWithSets)
        )

        coEvery { workoutDao.observeSessionById(sessionId) } returns flowOf(sessionWithExercises)

        // Act
        val result = repository.observeSessionById(sessionId)

        // Assert
        // Collect first emission
        val session = result.first()
        assertNotNull(session)
        assertEquals(sessionId, session.id)
        assertEquals("Test Session", session.name)
    }

    @Test
    fun `observeSessionById returns null when not found`() = runTest {
        // Arrange
        val sessionId = "nonexistent"
        coEvery { workoutDao.observeSessionById(sessionId) } returns flowOf(null)

        // Act
        val result = repository.observeSessionById(sessionId)

        // Assert
        val session = result.first()
        assertNull(session)
    }

    // ============ createSession Tests ============

    @Test
    fun `createSession successfully creates session with exercises and sets`() = runTest {
        // Arrange
        val sessionId = "session123"
        val exerciseId = "ex1"
        val setId = "set1"

        val session = WorkoutSession(
            id = sessionId,
            userId = "user1",
            date = LocalDateTime.now(),
            status = WorkoutStatus.IN_PROGRESS,
            name = "Leg Day",
            durationSeconds = 0,
            exercises = listOf(
                WorkoutExercise(
                    id = exerciseId,
                    exerciseId = "squat",
                    sets = listOf(
                        WorkoutSet(
                            id = setId,
                            index = 1,
                            weight = 100f,
                            reps = 10,
                            isCompleted = true
                        )
                    ),
                    restTimerSeconds = 60
                )
            ),
            comment = null
        )

        coEvery { workoutDao.insertFullWorkout(any(), any(), any()) } returns Unit

        // Act
        val result = repository.createSession(session)

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            workoutDao.insertFullWorkout(
                session = any(),
                exercises = any(),
                sets = any()
            )
        }
    }

    @Test
    fun `createSession with empty exercises list`() = runTest {
        // Arrange
        val session = WorkoutSession(
            id = "session123",
            userId = "user1",
            date = LocalDateTime.now(),
            status = WorkoutStatus.IN_PROGRESS,
            name = "Rest Day",
            durationSeconds = 0,
            exercises = emptyList(),
            comment = null
        )

        coEvery { workoutDao.insertFullWorkout(any(), any(), any()) } returns Unit

        // Act
        val result = repository.createSession(session)

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            workoutDao.insertFullWorkout(
                session = any(),
                exercises = emptyList(),
                sets = emptyList()
            )
        }
    }

    @Test
    fun `createSession propagates exception on failure`() = runTest {
        // Arrange
        val session = createMockWorkoutSession("session123")
        val exception = RuntimeException("Database error")
        coEvery { workoutDao.insertFullWorkout(any(), any(), any()) } throws exception

        // Act
        val result = repository.createSession(session)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // ============ updateSession Tests ============

    @Test
    fun `updateSession successfully updates session`() = runTest {
        // Arrange
        val session = createMockWorkoutSession("session123").copy(
            name = "Updated Name",
            durationSeconds = 3600
        )

        coEvery { workoutDao.insertFullWorkout(any(), any(), any()) } returns Unit

        // Act
        val result = repository.updateSession(session)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { workoutDao.insertFullWorkout(any(), any(), any()) }
    }

    @Test
    fun `updateSession propagates exception on failure`() = runTest {
        // Arrange
        val session = createMockWorkoutSession("session123")
        val exception = RuntimeException("Update failed")
        coEvery { workoutDao.insertFullWorkout(any(), any(), any()) } throws exception

        // Act
        val result = repository.updateSession(session)

        // Assert
        assertTrue(result.isFailure)
    }

    // ============ deleteSession Tests ============

    @Test
    fun `deleteSession successfully soft deletes session with exercises and sets`() = runTest {
        // Arrange
        val sessionId = "session123"
        coEvery { workoutDao.deleteSession(any(), any(), any()) } returns Unit
        coEvery { workoutDao.deleteExercisesBySession(any(), any(), any()) } returns Unit
        coEvery { workoutDao.deleteSetsBySession(any(), any(), any()) } returns Unit

        // Act
        val result = repository.deleteSession(sessionId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { workoutDao.deleteSession(sessionId, any(), any()) }
        coVerify { workoutDao.deleteExercisesBySession(sessionId, any(), any()) }
        coVerify { workoutDao.deleteSetsBySession(sessionId, any(), any()) }
    }

    @Test
    fun `deleteSession propagates exception on failure`() = runTest {
        // Arrange
        val sessionId = "session123"
        val exception = RuntimeException("Delete failed")
        coEvery { workoutDao.deleteSession(any(), any(), any()) } throws exception

        // Act
        val result = repository.deleteSession(sessionId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // ============ Helper Methods ============

    private fun createMockSessionDb(id: String) = com.example.reptrack.data.local.models.WorkoutSessionDb(
        id = id,
        userId = "user1",
        date = LocalDateTime.now(),
        status = WorkoutStatus.IN_PROGRESS,
        name = "Test Session",
        durationSeconds = 0,
        comment = null,
        updatedAt = LocalDateTime.now(),
        deletedAt = null
    )

    private fun createMockExerciseDb(id: String) = WorkoutExerciseDb(
        id = id,
        workoutSessionId = "session123",
        exerciseId = "squat",
        restTimerSeconds = 60,
        updatedAt = LocalDateTime.now(),
        deletedAt = null
    )

    private fun createMockWorkoutSession(id: String) = WorkoutSession(
        id = id,
        userId = "user1",
        date = LocalDateTime.now(),
        status = WorkoutStatus.IN_PROGRESS,
        name = "Test Session",
        durationSeconds = 0,
        exercises = emptyList(),
        comment = null
    )
}
