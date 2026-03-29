package com.example.reptrack.data.workout.mock

import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.TemplateSchedule
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Mock repository for testing calendar functionality.
 * Contains pre-populated test data for workout templates.
 */
class FakeWorkoutTemplateRepository : WorkoutTemplateRepository {

    private val templates = MutableStateFlow(createMockTemplates())

    override suspend fun observeTemplateById(templateId: String): Flow<WorkoutTemplate?> {
        return templates.map { templateList ->
            templateList.firstOrNull { it.id == templateId }
        }
    }

    override suspend fun observeAllTemplates(): Flow<List<WorkoutTemplate>> {
        return templates
    }

    override fun observeTemplatesByDayOfWeek(
        dayOfWeek: Int,
        isSecondWeek: Boolean
    ): Flow<List<WorkoutTemplate>> {
        return templates.map { templateList ->
            templateList.filter { template ->
                template.schedule?.let { schedule ->
                    val days = if (isSecondWeek) schedule.week2Days else schedule.week1Days
                    dayOfWeek in days
                } ?: false
            }
        }
    }

    override suspend fun createTemplate(template: WorkoutTemplate): Result<Unit> {
        return try {
            val currentList = templates.value.toMutableList()
            currentList.add(template)
            templates.value = currentList
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTemplate(template: WorkoutTemplate): Result<Unit> {
        return try {
            val currentList = templates.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == template.id }
            if (index != -1) {
                currentList[index] = template
                templates.value = currentList
                Result.success(Unit)
            } else {
                Result.failure(Exception("Template not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTemplate(templateId: String): Result<Unit> {
        return try {
            val currentList = templates.value.toMutableList()
            val removed = currentList.removeIf { it.id == templateId }
            if (removed) {
                templates.value = currentList
                Result.success(Unit)
            } else {
                Result.failure(Exception("Template not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates mock workout templates for testing.
     */
    private fun createMockTemplates(): List<WorkoutTemplate> {
        return listOf(
            WorkoutTemplate(
                id = "template_1",
                name = "Тренировка груди",
                description = "Полноценная тренировка грудных мышц для развития объема и силы",
                iconId = "dumbbell",
                exerciseIds = listOf("exercise_1", "exercise_2", "exercise_3"),
                iconRes = R.drawable.exercise_default_icon,
                iconColor = "#FF5722",
                muscleGroups = listOf(MuscleGroup.CHEST),
                isCustom = true,
                schedule = TemplateSchedule(
                    week1Days = setOf(TemplateSchedule.MONDAY, TemplateSchedule.WEDNESDAY, TemplateSchedule.FRIDAY),
                    week2Days = setOf(TemplateSchedule.TUESDAY, TemplateSchedule.THURSDAY, TemplateSchedule.SATURDAY)
                )
            ),

            WorkoutTemplate(
                id = "template_2",
                name = "Тренировка спины",
                description = "Комплекс упражнений для развития мышц спины",
                iconId = "barbell",
                exerciseIds = listOf("exercise_4", "exercise_5"),
                iconRes = R.drawable.exercise_default_icon,
                iconColor = "#4CAF50",
                muscleGroups = listOf(MuscleGroup.BACK),
                isCustom = true,
                schedule = TemplateSchedule(
                    week1Days = setOf(TemplateSchedule.MONDAY, TemplateSchedule.WEDNESDAY, TemplateSchedule.FRIDAY),
                    week2Days = setOf(TemplateSchedule.TUESDAY, TemplateSchedule.THURSDAY, TemplateSchedule.SATURDAY)
                )
            ),

            WorkoutTemplate(
                id = "template_3",
                name = "Ноги и плечи",
                description = "Силовая тренировка нижней части тела и плечевого пояса",
                iconId = "kettlebell",
                exerciseIds = listOf("exercise_6", "exercise_7", "exercise_8", "exercise_9"),
                iconRes = R.drawable.exercise_default_icon,
                iconColor = "#2196F3",
                muscleGroups = listOf(MuscleGroup.LEGS, MuscleGroup.ARMS),
                isCustom = true,
                schedule = TemplateSchedule(
                    week1Days = setOf(TemplateSchedule.MONDAY, TemplateSchedule.WEDNESDAY, TemplateSchedule.FRIDAY),
                    week2Days = setOf(TemplateSchedule.TUESDAY, TemplateSchedule.THURSDAY, TemplateSchedule.SATURDAY)
                )
            ),

            WorkoutTemplate(
                id = "template_4",
                name = "Full Body",
                description = "Полноценная тренировка всего тела за одну сессию",
                iconId = "fullbody",
                exerciseIds = listOf("exercise_1", "exercise_4", "exercise_6"),
                iconRes = R.drawable.exercise_default_icon,
                iconColor = "#9C27B0",
                muscleGroups = listOf(
                    MuscleGroup.CHEST,
                    MuscleGroup.BACK,
                    MuscleGroup.LEGS,
                    MuscleGroup.ARMS
                ),
                isCustom = false,
                schedule = null
            ),

            WorkoutTemplate(
                id = "template_5",
                name = "Кардио тренировка",
                description = "Интенсивное кардио для сжигания калорий и развития выносливости",
                iconId = "cardio",
                exerciseIds = listOf("exercise_10"),
                iconRes = R.drawable.exercise_default_icon,
                iconColor = "#FF9800",
                muscleGroups = listOf(MuscleGroup.CARDIO),
                isCustom = true,
                schedule = null
            ),

            WorkoutTemplate(
                id = "template_6",
                name = "Пресс и кора",
                description = "Укрепление мышц кора и пресса",
                iconId = "abs",
                exerciseIds = listOf("exercise_11", "exercise_12"),
                iconRes = R.drawable.exercise_default_icon,
                iconColor = "#00BCD4",
                muscleGroups = listOf(MuscleGroup.ABS),
                isCustom = true,
                schedule = null
            )
        )
    }
}
