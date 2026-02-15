package com.example.reptrack.domain.workout.usecases.calendar

import com.example.reptrack.domain.workout.CalendarDay
import com.example.reptrack.domain.workout.CalendarWeek
import com.example.reptrack.domain.workout.DayWorkoutStatus
import com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import com.example.reptrack.domain.workout.usecases.calendar.CalendarDateUtils.getDayOfWeekIndex
import com.example.reptrack.domain.workout.usecases.calendar.CalendarDateUtils.isSecondWeekInMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Use case для работы с календарём тренировок.
 * Все методы возвращают Flow для реактивного обновления UI.
 */
class CalendarUseCase(
    private val sessionRepository: WorkoutSessionRepository,
    private val templateRepository: WorkoutTemplateRepository
) {

    /**
     * Наблюдает за неделей календаря для указанной даты
     * @param date Дата для получения недели (используется для определения текущей недели)
     */
    fun observeWeekCalendar(date: LocalDate): Flow<CalendarWeek> {
        val mondayOfWeek = date.with(DayOfWeek.MONDAY)
        val daysInWeek = (0L..6L).map { mondayOfWeek.plusDays(it) }

        // Создаём Flow для каждого дня недели и комбинируем их
        val dayFlows = daysInWeek.map { dayDate ->
            observeCalendarDay(dayDate)
        }

        // Комбинируем все Flow дней в один Flow недели
        return combine(dayFlows) { calendarDays ->
            CalendarWeek(
                days = calendarDays.toList(),
                weekStartDate = mondayOfWeek
            )
        }
    }

    /**
     * Наблюдает за информацией о тренировке для конкретного дня
     */
    private fun observeCalendarDay(date: LocalDate): Flow<CalendarDay> {
        val dayOfWeekValue = getDayOfWeekIndex(date)
        val isSecondWeek = isSecondWeekInMonth(date)

        // Подписываемся на сессию и шаблоны параллельно
        return combine(
            sessionRepository.observeSessionByDate(date),
            templateRepository.observeTemplatesByDayOfWeek(dayOfWeekValue, isSecondWeek)
        ) { session, templates ->
            createCalendarDay(date, session, templates)
        }
    }

    /**
     * Создаёт CalendarDay на основе данных сессии и шаблонов
     */
    private fun createCalendarDay(
        date: LocalDate,
        session: com.example.reptrack.domain.workout.WorkoutSession?,
        templates: List<com.example.reptrack.domain.workout.WorkoutTemplate>
    ): CalendarDay {
        val hasWorkout = session != null || templates.isNotEmpty()
        val now = LocalDate.now()

        val status = session?.status?.let { workoutStatus ->
            when (workoutStatus.toString()) {
                "COMPLETED" -> DayWorkoutStatus.COMPLETED
                "SKIPPED", "CANCELLED" -> DayWorkoutStatus.SKIPPED
                else -> DayWorkoutStatus.PLANNED
            }
        } ?: when {
            // If date is in the past and has templates but no session -> SKIPPED
            templates.isNotEmpty() && date.isBefore(now) -> DayWorkoutStatus.SKIPPED
            // If date is today/future and has templates -> PLANNED
            templates.isNotEmpty() -> DayWorkoutStatus.PLANNED
            // No workout at all
            else -> null
        }

        return CalendarDay(
            date = date,
            hasWorkout = hasWorkout,
            status = status,
            workoutSession = session,
            applicableTemplates = templates
        )
    }
}
