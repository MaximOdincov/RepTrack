package com.example.reptrack.domain.workout.repositories

import com.example.reptrack.domain.workout.WorkoutTemplate
import java.time.DayOfWeek

interface TrainingTemplateRepository {
    suspend fun getTemplateById(templateId: String): Result<WorkoutTemplate>

    suspend fun getAllTemplates(): Result<List<WorkoutTemplate>>

    /**
     * Получить шаблоны, применимые на конкретный день недели
     * @param dayOfWeek День недели (1 = Понедельник, 7 = Воскресенье)
     * @param isSecondWeek Вторая ли неделя (для ротирующихся расписаний)
     */
    suspend fun getTemplatesByDayOfWeek(
        dayOfWeek: Int,
        isSecondWeek: Boolean
    ): Result<List<WorkoutTemplate>>

    suspend fun createTemplate(template: WorkoutTemplate): Result<Unit>

    suspend fun updateTemplate(template: WorkoutTemplate): Result<Unit>

    suspend fun deleteTemplate(templateId: String): Result<Unit>
}