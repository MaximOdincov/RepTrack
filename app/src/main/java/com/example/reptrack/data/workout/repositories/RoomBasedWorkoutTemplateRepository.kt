package com.example.reptrack.data.workout.repositories

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.exceptions.DataException
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndHandle
import com.example.reptrack.core.extensions.toAppException
import com.example.reptrack.data.local.AppDatabase
import com.example.reptrack.data.local.aggregates.WorkoutTemplateWithExercises
import com.example.reptrack.data.local.dao.WorkoutTemplateDao
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.sql.SQLException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

/**
 * Room-based implementation of WorkoutTemplateRepository
 * Simplified to work with existing database structure
 */
class RoomBasedWorkoutTemplateRepository(
    private val database: AppDatabase,
    private val errorHandler: ErrorHandler
) : WorkoutTemplateRepository {

    private val dao: WorkoutTemplateDao = database.templateDao()

    override fun observeTemplateById(templateId: String): Flow<WorkoutTemplate?> {
        val templateFlow = dao.observeTemplateById(templateId)
        val orderedIdsFlow = dao.observeOrderedExerciseIds(templateId)

        return combine(templateFlow, orderedIdsFlow) { templateWithExercises, orderedIds ->
            if (templateWithExercises == null) return@combine null

            // Map exercises to maintain order
            val exerciseMap = templateWithExercises.exercises.associateBy { it.id }
            val orderedExercises = orderedIds.mapNotNull { exerciseMap[it] }

            templateWithExercises.copy(exercises = orderedExercises).toDomain()
        }.catchAndHandle(
            errorHandler = errorHandler,
            context = ErrorContext(
                action = "observeTemplateById",
                entityId = templateId
            )
        )
    }

    override fun observeAllTemplates(): Flow<List<WorkoutTemplate>> {
        return dao.observeTemplates()
            .map { list ->
                list.map { templateWithExercises ->
                    // For each template, we need to load ordered exercise IDs
                    // Since we're in a Flow, we can't easily do this per-template
                    // For now, return templates with exerciseIds from the relation
                    // Note: This won't preserve order! Need to fix this too.
                    templateWithExercises.toDomain()
                }
            }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(action = "observeAllTemplates")
            )
    }

    override fun observeTemplatesByDayOfWeek(
        dayOfWeek: Int,
        isSecondWeek: Boolean
    ): Flow<List<WorkoutTemplate>> {
        android.util.Log.d("TemplateRepository", "observeTemplatesByDayOfWeek: dayOfWeek=$dayOfWeek, isSecondWeek=$isSecondWeek")

        return observeAllTemplates()
            .map { templates ->
                android.util.Log.d("TemplateRepository", "Total templates: ${templates.size}")

                val filtered = templates.filter { template ->
                    val schedule = template.schedule
                    android.util.Log.d("TemplateRepository", "Checking template: ${template.name}, schedule: $schedule")

                    if (schedule == null) {
                        android.util.Log.d("TemplateRepository", "  -> REJECTED: schedule is null")
                        false
                    } else {
                        // Выбираем нужную неделю в цикле
                        val targetDays = if (isSecondWeek) schedule.week2Days else schedule.week1Days
                        // Проверяем, что день недели есть в расписании
                        val matches = targetDays.contains(dayOfWeek)
                        android.util.Log.d("TemplateRepository", "  -> targetDays=$targetDays, contains($dayOfWeek)=$matches")
                        matches
                    }
                }

                android.util.Log.d("TemplateRepository", "Filtered templates: ${filtered.size}, names: ${filtered.map { it.name }}")
                filtered
            }
            .catchAndHandle(
                errorHandler = errorHandler,
                context = ErrorContext(
                    action = "observeTemplatesByDayOfWeek",
                    additionalInfo = mapOf("dayOfWeek" to dayOfWeek, "isSecondWeek" to isSecondWeek)
                )
            )
    }

    override suspend fun createTemplate(template: WorkoutTemplate): Result<Unit> {
        return try {
            dao.insertFullTemplate(
                template.toDb(),
                template.exerciseIds
            )
            Result.success(Unit)
        } catch (e: Exception) {
            val appException = when (e) {
                is SQLException -> DataException.DatabaseError(
                    operation = "insertFullTemplate",
                    cause = e
                )
                else -> e.toAppException()
            }
            errorHandler.log(
                appException,
                ErrorContext(
                    action = "createTemplate",
                    entityId = template.id
                )
            )
            Result.failure(appException)
        }
    }

    override suspend fun updateTemplate(template: WorkoutTemplate): Result<Unit> {
        return try {
            // Delete old exercises
            dao.deleteTemplateExercises(template.id)
            // Insert updated template
            dao.insertFullTemplate(
                template.toDb(),
                template.exerciseIds
            )
            Result.success(Unit)
        } catch (e: Exception) {
            val appException = when (e) {
                is SQLException -> DataException.DatabaseError(
                    operation = "updateTemplate",
                    cause = e
                )
                else -> e.toAppException()
            }
            errorHandler.log(
                appException,
                ErrorContext(
                    action = "updateTemplate",
                    entityId = template.id
                )
            )
            Result.failure(appException)
        }
    }

    override suspend fun deleteTemplate(templateId: String): Result<Unit> {
        return try {
            dao.deleteTemplate(
                templateId = templateId,
                deletedAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            val appException = when (e) {
                is SQLException -> DataException.DatabaseError(
                    operation = "deleteTemplate",
                    cause = e
                )
                else -> e.toAppException()
            }
            errorHandler.log(
                appException,
                ErrorContext(
                    action = "deleteTemplate",
                    entityId = templateId
                )
            )
            Result.failure(appException)
        }
    }
}

// Extension function to convert from DB model to domain model
private fun WorkoutTemplateWithExercises.toDomain(): WorkoutTemplate {
    val json = Json { ignoreUnknownKeys = true }

    android.util.Log.i("Mapper", "toDomain: templateId=${template.id}, week1Days=${template.week1Days}, week2Days=${template.week2Days}")

    // Parse schedule from JSON strings
    val schedule = if (template.week1Days != null || template.week2Days != null) {
        val week1Days = template.week1Days?.let {
            try {
                json.decodeFromString<Set<Int>>(it)
            } catch (e: Exception) {
                android.util.Log.e("Mapper", "Failed to parse week1Days: $it", e)
                emptySet()
            }
        } ?: emptySet()
        val week2Days = template.week2Days?.let {
            try {
                json.decodeFromString<Set<Int>>(it)
            } catch (e: Exception) {
                android.util.Log.e("Mapper", "Failed to parse week2Days: $it", e)
                emptySet()
            }
        } ?: emptySet()
        com.example.reptrack.domain.workout.entities.TemplateSchedule(
            week1Days = week1Days,
            week2Days = week2Days
        )
    } else {
        android.util.Log.i("Mapper", "Schedule is null in DB")
        null
    }

    android.util.Log.i("Mapper", "toDomain: parsed schedule - week1=${schedule?.week1Days}, week2=${schedule?.week2Days}")

    return WorkoutTemplate(
        id = template.id,
        name = template.name,
        description = template.description ?: "",
        exerciseIds = exercises.map { it.id },
        iconRes = template.iconRes,
        iconColor = template.iconColor,
        iconId = template.iconId,
        muscleGroups = emptyList(),  // Can calculate from exercises if needed
        isCustom = true,  // Assume all templates are custom
        schedule = schedule
    )
}

// Extension function to convert from domain model to DB model
private fun WorkoutTemplate.toDb(): com.example.reptrack.data.local.models.WorkoutTemplateDb {
    val week1Json = schedule?.week1Days?.let { Json.encodeToString(it) }
    val week2Json = schedule?.week2Days?.let { Json.encodeToString(it) }

    android.util.Log.i("Mapper", "toDb: templateId=$id, schedule=$schedule, week1Json=$week1Json, week2Json=$week2Json")

    return com.example.reptrack.data.local.models.WorkoutTemplateDb(
        id = id,
        name = name,
        description = description.ifBlank { null },
        iconId = iconId,
        iconRes = iconRes,
        iconColor = iconColor,
        week1Days = week1Json,
        week2Days = week2Json
    )
}
