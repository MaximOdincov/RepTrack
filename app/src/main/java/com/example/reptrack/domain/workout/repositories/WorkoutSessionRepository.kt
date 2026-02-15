package com.example.reptrack.domain.workout.repositories

import com.example.reptrack.domain.workout.WorkoutSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WorkoutSessionRepository {
    fun observeSessionById(sessionId: String): Flow<WorkoutSession?>

    fun observeSessionsInRange(fromDate: Long, toDate: Long): Flow<List<WorkoutSession>>

    fun observeSessionByDate(date: LocalDate): Flow<WorkoutSession?>


    suspend fun createSession(session: WorkoutSession): Result<Unit>


    suspend fun updateSession(session: WorkoutSession): Result<Unit>


    suspend fun deleteSession(sessionId: String): Result<Unit>
}
