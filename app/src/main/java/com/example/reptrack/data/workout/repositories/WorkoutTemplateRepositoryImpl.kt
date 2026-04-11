package com.example.reptrack.data.workout.repositories

import com.example.reptrack.data.local.dao.WorkoutTemplateDao
import com.example.reptrack.data.local.mappers.parseSchedule
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.domain.workout.entities.TemplateSchedule
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class WorkoutTemplateRepositoryImpl(
    private val templateDao: WorkoutTemplateDao
) : WorkoutTemplateRepository {

    override fun observeTemplateById(templateId: String): Flow<WorkoutTemplate?> {
        return templateDao.observeTemplateById(templateId)
            .map { aggregate ->
                aggregate?.let {
                    it.template.toDomain(
                        exerciseIds = it.exercises.map { exercise -> exercise.id }
                    )
                }
            }
            .catch { e -> throw e }
    }

    override fun observeAllTemplates(): Flow<List<WorkoutTemplate>> {
        return templateDao.observeTemplates()
            .map { aggregates ->
                aggregates.map { aggregate ->
                    aggregate.template.toDomain(
                        exerciseIds = aggregate.exercises.map { it.id }
                    )
                }
            }
            .catch { e -> throw e }
    }

    override fun observeTemplatesByDayOfWeek(
        dayOfWeek: Int,
        isSecondWeek: Boolean
    ): Flow<List<WorkoutTemplate>> {
        return templateDao.observeTemplates()
            .map { aggregates ->
                aggregates.filter { aggregate ->
                    matchesDayOfWeek(aggregate.template, dayOfWeek, isSecondWeek)
                }.map { aggregate ->
                    aggregate.template.toDomain(
                        exerciseIds = aggregate.exercises.map { it.id }
                    )
                }
            }
            .catch { e -> throw e }
    }

    override suspend fun createTemplate(template: WorkoutTemplate): Result<Unit> {
        return try {
            android.util.Log.i("TemplateRepository", "createTemplate: id=${template.id}, schedule=${template.schedule}")
            val templateDb = template.toDb()
            android.util.Log.i("TemplateRepository", "createTemplate: week1Days=${templateDb.week1Days}, week2Days=${templateDb.week2Days}")
            templateDao.insertFullTemplate(
                template = templateDb,
                exerciseIds = template.exerciseIds
            )
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("TemplateRepository", "createTemplate error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateTemplate(template: WorkoutTemplate): Result<Unit> {
        return try {
            android.util.Log.i("TemplateRepository", "updateTemplate: id=${template.id}, schedule=${template.schedule}")
            templateDao.deleteTemplateExercises(template.id)

            val templateDb = template.toDb()
            android.util.Log.i("TemplateRepository", "updateTemplate: week1Days=${templateDb.week1Days}, week2Days=${templateDb.week2Days}")
            templateDao.insertFullTemplate(
                template = templateDb,
                exerciseIds = template.exerciseIds
            )
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("TemplateRepository", "updateTemplate error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteTemplate(templateId: String): Result<Unit> {
        return try {
            val now = java.time.LocalDateTime.now()
            templateDao.deleteTemplate(templateId, now, now)
            templateDao.deleteTemplateExercises(templateId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Проверяет, подходит ли шаблон для указанного дня недели
     */
    private fun matchesDayOfWeek(
        templateDb: com.example.reptrack.data.local.models.WorkoutTemplateDb,
        dayOfWeek: Int,
        isSecondWeek: Boolean
    ): Boolean {
        val schedule = parseSchedule(templateDb.week1Days, templateDb.week2Days)
            ?: return false

        val targetDays = if (isSecondWeek) schedule.week2Days else schedule.week1Days
        return targetDays.contains(dayOfWeek)
    }
}
