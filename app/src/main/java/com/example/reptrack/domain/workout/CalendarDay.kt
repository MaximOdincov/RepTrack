package com.example.reptrack.domain.workout

import java.time.LocalDate

/**
 * Статус тренировки на день
 */
enum class DayWorkoutStatus {
    SKIPPED,      // Тренировка пропущена (красная точка)
    COMPLETED,    // Тренировка завершена (чёрная точка)
    PLANNED       // Тренировка планируется (оранжевая точка)
}

/**
 * День календаря с информацией о тренировке
 * @param date Дата дня
 * @param hasWorkout Есть ли тренировка в этот день
 * @param status Статус тренировки (если hasWorkout == true)
 * @param workoutSession Данные о тренировке на этот день (если есть)
 * @param applicableTemplates Шаблоны, применимые в этот день
 */
data class CalendarDay(
    val date: LocalDate,
    val hasWorkout: Boolean,
    val status: DayWorkoutStatus? = null,
    val workoutSession: WorkoutSession? = null,
    val applicableTemplates: List<WorkoutTemplate> = emptyList()
)

/**
 * Неделя календаря
 */
data class CalendarWeek(
    val days: List<CalendarDay>,
    val weekStartDate: LocalDate
) {
    fun getCurrentDayIndex(today: LocalDate): Int {
        return days.indexOfFirst { it.date == today }
    }
}

/**
 * Расширенный вид календаря (месячный вид)
 */
data class CalendarMonth(
    val weeks: List<CalendarWeek>,
    val monthIndex: Int,
    val year: Int,
    val displayName: String // Например "January 2026"
)
