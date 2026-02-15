package com.example.reptrack.domain.workout.repositories

import com.example.reptrack.domain.workout.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

interface WorkoutTemplateRepository {
    fun observeTemplateById(templateId: String): Flow<WorkoutTemplate?>

    fun observeAllTemplates(): Flow<List<WorkoutTemplate>>

    fun observeTemplatesByDayOfWeek(
        dayOfWeek: Int,
        isSecondWeek: Boolean
    ): Flow<List<WorkoutTemplate>>

    suspend fun createTemplate(template: WorkoutTemplate): Result<Unit>

    suspend fun updateTemplate(template: WorkoutTemplate): Result<Unit>

    suspend fun deleteTemplate(templateId: String): Result<Unit>
}
