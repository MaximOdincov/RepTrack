package com.example.reptrack.domain.workout.repositories

import com.example.reptrack.domain.workout.WorkoutSession
import java.time.LocalDate

interface TrainingSessionRepository {
    suspend fun getSessionById(sessionId: String): Result<WorkoutSession>

    suspend fun getSessionsInRange(fromDate: Long, toDate: Long): Result<List<WorkoutSession>>

    /**
     * Получить тренировку на конкретную дату
     */
    suspend fun getSessionByDate(date: LocalDate): Result<WorkoutSession?>

    suspend fun createSession(session: WorkoutSession): Result<Unit>

    suspend fun updateSession(session: WorkoutSession): Result<Unit>

    suspend fun deleteSession(sessionId: String): Result<Unit>
}