package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.models.WorkoutTemplateDb
import com.example.reptrack.domain.workout.entities.TemplateSchedule
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

internal fun parseSchedule(week1Str: String?, week2Str: String?): TemplateSchedule? {
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
