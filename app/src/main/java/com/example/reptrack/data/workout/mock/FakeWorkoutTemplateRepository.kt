package com.example.reptrack.data.workout.mock

import com.example.reptrack.domain.workout.TemplateSchedule
import com.example.reptrack.domain.workout.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Mock repository for testing calendar functionality.
 * Contains pre-populated test data for workout templates.
 */
class FakeWorkoutTemplateRepository : WorkoutTemplateRepository {

    private val mockTemplates = createMockTemplates()

    override fun observeTemplateById(templateId: String): Flow<WorkoutTemplate?> {
        return flowOf(mockTemplates.find { it.id == templateId })
    }

    override fun observeAllTemplates(): Flow<List<WorkoutTemplate>> {
        return flowOf(mockTemplates)
    }

    override fun observeTemplatesByDayOfWeek(
        dayOfWeek: Int,
        isSecondWeek: Boolean
    ): Flow<List<WorkoutTemplate>> {
        val filtered = mockTemplates.filter { template ->
            template.schedule?.let { schedule ->
                val days = if (isSecondWeek) schedule.week2Days else schedule.week1Days
                dayOfWeek in days
            } ?: false
        }
        return flowOf(filtered)
    }

    override suspend fun createTemplate(template: WorkoutTemplate): Result<Unit> {
        // For mock, just return success
        return Result.success(Unit)
    }

    override suspend fun updateTemplate(template: WorkoutTemplate): Result<Unit> {
        // For mock, just return success
        return Result.success(Unit)
    }

    override suspend fun deleteTemplate(templateId: String): Result<Unit> {
        // For mock, just return success
        return Result.success(Unit)
    }

    /**
     * Creates mock workout templates for testing the calendar.
     * Templates are scheduled on specific days to test:
     * - PLANNED status (templates on future dates)
     * - SKIPPED status (templates on past dates without sessions)
     */
    private fun createMockTemplates(): List<WorkoutTemplate> {
        return listOf(
            // Monday workout (day 0)
            WorkoutTemplate(
                id = "template_monday",
                name = "Monday Push",
                iconId = "dumbbell",
                exerciseIds = listOf("bench_press", "overhead_press", "triceps"),
                schedule = TemplateSchedule(
                    week1Days = setOf(TemplateSchedule.MONDAY, TemplateSchedule.WEDNESDAY, TemplateSchedule.FRIDAY),
                    week2Days = setOf(TemplateSchedule.TUESDAY, TemplateSchedule.THURSDAY, TemplateSchedule.SATURDAY)
                )
            ),

            // Wednesday workout (day 2)
            WorkoutTemplate(
                id = "template_wednesday",
                name = "Wednesday Pull",
                iconId = "barbell",
                exerciseIds = listOf("pull_ups", "rows", "bicep_curls"),
                schedule = TemplateSchedule(
                    week1Days = setOf(TemplateSchedule.MONDAY, TemplateSchedule.WEDNESDAY, TemplateSchedule.FRIDAY),
                    week2Days = setOf(TemplateSchedule.TUESDAY, TemplateSchedule.THURSDAY, TemplateSchedule.SATURDAY)
                )
            ),

            // Friday workout (day 4)
            WorkoutTemplate(
                id = "template_friday",
                name = "Friday Legs",
                iconId = "kettlebell",
                exerciseIds = listOf("squats", "lunges", "leg_press"),
                schedule = TemplateSchedule(
                    week1Days = setOf(TemplateSchedule.MONDAY, TemplateSchedule.WEDNESDAY, TemplateSchedule.FRIDAY),
                    week2Days = setOf(TemplateSchedule.TUESDAY, TemplateSchedule.THURSDAY, TemplateSchedule.SATURDAY)
                )
            )
        )
    }
}
