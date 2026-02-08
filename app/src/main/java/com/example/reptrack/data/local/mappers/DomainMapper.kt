package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.WorkoutSetDb
import com.example.reptrack.data.local.models.WorkoutTemplateDb
import com.example.reptrack.domain.workout.Exercise
import com.example.reptrack.domain.workout.ExerciseType
import com.example.reptrack.domain.workout.MuscleGroup
import com.example.reptrack.domain.workout.TemplateSchedule
import com.example.reptrack.domain.workout.WorkoutSet
import com.example.reptrack.domain.workout.WorkoutTemplate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Маппер для преобразования DB моделей в Domain моели
 */
object DomainMapper {

    fun ExerciseDb.toDomain(): Exercise = Exercise(
        id = id,
        name = name,
        muscleGroup = this.muscleGroup,
        type = this.type,
        iconUrl = iconUrl,
        iconColor = iconColor,
        backgroundImageUrl = backgroundImageUrl,
        backgroundColor = backgroundColor,
        isCustom = isCustom
    )

    fun Exercise.toDb(): ExerciseDb = ExerciseDb(
        id = id,
        name = name,
        muscleGroup = muscleGroup,
        type = type,
        iconUrl = iconUrl,
        iconColor = iconColor,
        backgroundImageUrl = backgroundImageUrl,
        backgroundColor = backgroundColor,
        isCustom = isCustom
    )

    fun WorkoutTemplateDb.toDomain(exerciseIds: List<String>): WorkoutTemplate = WorkoutTemplate(
        id = id,
        name = name,
        iconId = iconId,
        exerciseIds = exerciseIds,
        schedule = parseSchedule(week1Days, week2Days)
    )

    fun WorkoutTemplate.toDb(): WorkoutTemplateDb = WorkoutTemplateDb(
        id = id,
        name = name,
        iconId = iconId,
        week1Days = serializeSchedule(schedule?.week1Days),
        week2Days = serializeSchedule(schedule?.week2Days),
        updatedAt = java.time.LocalDateTime.now()
    )

    fun WorkoutSetDb.toDomain(): WorkoutSet = WorkoutSet(
        id = id,
        index = setOrder,
        weight = weight,
        reps = reps,
        isCompleted = isCompleted
    )

    fun WorkoutSet.toDb(workoutExerciseId: String): WorkoutSetDb = WorkoutSetDb(
        id = id,
        workoutExerciseId = workoutExerciseId,
        setOrder = index,
        weight = weight,
        reps = reps,
        isCompleted = isCompleted,
        updatedAt = java.time.LocalDateTime.now(),
        deletedAt = null
    )

    /**
     * Парсит JSON строку расписания в Set<Int>
     */
    private fun parseSchedule(week1Str: String?, week2Str: String?): TemplateSchedule? {
        return if (week1Str != null && week2Str != null) {
            try {
                val json = Json { ignoreUnknownKeys = true }
                val week1 = json.decodeFromString<Set<Int>>(week1Str)
                val week2 = json.decodeFromString<Set<Int>>(week2Str)
                TemplateSchedule(week1Days = week1, week2Days = week2)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Сериализует Set<Int> в JSON строку
     */
    private fun serializeSchedule(schedule: Set<Int>?): String? {
        return if (schedule != null) {
            try {
                val json = Json { prettyPrint = false }
                json.encodeToString(schedule)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
}
