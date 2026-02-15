package com.example.reptrack.data.workout.mock

import com.example.reptrack.domain.workout.WorkoutExercise
import com.example.reptrack.domain.workout.WorkoutSet
import com.example.reptrack.domain.workout.WorkoutSession
import com.example.reptrack.domain.workout.WorkoutStatus
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

/**
 * Mock repository for testing calendar functionality.
 * Contains pre-populated test data for various workout statuses.
 */
class FakeWorkoutSessionRepository : WorkoutSessionRepository {

    private val mockSessions = createMockSessions()

    override fun observeSessionById(sessionId: String): Flow<WorkoutSession?> {
        return flowOf(mockSessions.find { it.id == sessionId })
    }

    override fun observeSessionsInRange(fromDate: Long, toDate: Long): Flow<List<WorkoutSession>> {
        val filtered = mockSessions.filter { session ->
            val timestamp = session.date.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
            timestamp in fromDate..toDate
        }
        return flowOf(filtered)
    }

    override fun observeSessionByDate(date: LocalDate): Flow<WorkoutSession?> {
        val session = mockSessions.find { session ->
            session.date.toLocalDate() == date
        }
        return flowOf(session)
    }

    override suspend fun createSession(session: WorkoutSession): Result<Unit> {
        // For mock, just return success
        return Result.success(Unit)
    }

    override suspend fun updateSession(session: WorkoutSession): Result<Unit> {
        // For mock, just return success
        return Result.success(Unit)
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        // For mock, just return success
        return Result.success(Unit)
    }

    /**
     * Creates mock workout sessions for testing the calendar.
     * Includes sessions with different statuses:
     * - COMPLETED: Past workouts (GREEN dot)
     * - PLANNED: Future scheduled workouts (ORANGE dot)
     * - IN_PROGRESS: Today's workout (ORANGE dot)
     */
    private fun createMockSessions(): List<WorkoutSession> {
        val today = LocalDate.now()
        val now = java.time.LocalDateTime.now()

        return listOf(
            // Completed workout - 3 days ago (GREEN dot)
            WorkoutSession(
                id = "session_1",
                userId = "user_1",
                date = today.minusDays(3).atTime(10, 0),
                status = WorkoutStatus.COMPLETED,
                name = "Push Day",
                durationSeconds = 3600,
                exercises = createMockExercises("Bench Press", "Overhead Press", "Triceps"),
                comment = "Great workout!"
            ),

            // Completed workout - 2 days ago (GREEN dot)
            WorkoutSession(
                id = "session_2",
                userId = "user_1",
                date = today.minusDays(2).atTime(9, 30),
                status = WorkoutStatus.COMPLETED,
                name = "Pull Day",
                durationSeconds = 3300,
                exercises = createMockExercises("Pull Ups", "Rows", "Bicep Curls"),
                comment = null
            ),

            // Completed workout - 7 days ago (GREEN dot)
            WorkoutSession(
                id = "session_6",
                userId = "user_1",
                date = today.minusDays(7).atTime(11, 0),
                status = WorkoutStatus.COMPLETED,
                name = "Cardio",
                durationSeconds = 2400,
                exercises = createMockExercises("Treadmill", "Jump Rope"),
                comment = "Good cardio session"
            ),

            // Today's workout - in progress (ORANGE dot)
            WorkoutSession(
                id = "session_3",
                userId = "user_1",
                date = now,
                status = WorkoutStatus.IN_PROGRESS,
                name = "Leg Day",
                durationSeconds = 2700,
                exercises = createMockExercises("Squats", "Lunges", "Leg Press"),
                comment = "Feeling strong"
            ),

            // Planned workout - tomorrow (ORANGE dot)
            WorkoutSession(
                id = "session_4",
                userId = "user_1",
                date = today.plusDays(1).atTime(18, 0),
                status = WorkoutStatus.PLANNED,
                name = "Upper Body",
                durationSeconds = 3000,
                exercises = createMockExercises("Bench", "Rows", "Shoulders"),
                comment = null
            ),

            // Planned workout - 4 days from now (ORANGE dot)
            WorkoutSession(
                id = "session_5",
                userId = "user_1",
                date = today.plusDays(4).atTime(10, 30),
                status = WorkoutStatus.PLANNED,
                name = "Full Body",
                durationSeconds = 3600,
                exercises = createMockExercises("Squats", "Push Ups", "Plank"),
                comment = null
            )
        )
    }

    private fun createMockExercises(vararg names: String): List<WorkoutExercise> {
        return names.mapIndexed { index, name ->
            WorkoutExercise(
                id = "exercise_$index",
                exerciseId = name.lowercase().replace(" ", "_"),
                sets = listOf(
                    WorkoutSet(
                        id = "set_${index}_1",
                        index = 1,
                        weight = 20f,
                        reps = 12,
                        isCompleted = true
                    ),
                    WorkoutSet(
                        id = "set_${index}_2",
                        index = 2,
                        weight = 22.5f,
                        reps = 10,
                        isCompleted = true
                    ),
                    WorkoutSet(
                        id = "set_${index}_3",
                        index = 3,
                        weight = 25f,
                        reps = 8,
                        isCompleted = true
                    )
                ),
                restTimerSeconds = 90
            )
        }
    }
}
