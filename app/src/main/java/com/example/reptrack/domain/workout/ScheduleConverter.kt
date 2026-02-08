package com.example.reptrack.domain.workout

/**
 * Конвертор для работы с расписанием шаблонов
 */
object ScheduleConverter {

    /**
     * Парсить расписание из JSON строки (используется при маппинге)
     */
    fun parseScheduleFromJson(json: String?): Set<Int>? {
        return if (json != null && json.isNotEmpty()) {
            try {
                json.removeSurrounding("[", "]")
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toSet()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Преобразовать расписание в JSON строку
     */
    fun scheduleToJson(schedule: Set<Int>?): String? {
        return if (schedule != null && schedule.isNotEmpty()) {
            "[${schedule.sorted().joinToString(",")}]"
        } else {
            null
        }
    }

    /**
     * Проверить применимо ли расписание на определённый день
     */
    fun isApplicableOnDay(schedule: TemplateSchedule?, dayOfWeek: Int, isSecondWeek: Boolean): Boolean {
        return if (schedule != null) {
            if (isSecondWeek) {
                schedule.week2Days.contains(dayOfWeek)
            } else {
                schedule.week1Days.contains(dayOfWeek)
            }
        } else {
            false
        }
    }
}
