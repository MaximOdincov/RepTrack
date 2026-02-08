package com.example.reptrack.domain.workout.usecases.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.domain.workout.CalendarDay
import com.example.reptrack.domain.workout.CalendarMonth
import com.example.reptrack.domain.workout.CalendarWeek
import com.example.reptrack.domain.workout.DayWorkoutStatus
import com.example.reptrack.domain.workout.repositories.TrainingSessionRepository
import com.example.reptrack.domain.workout.repositories.TrainingTemplateRepository
import com.example.reptrack.domain.workout.usecases.calendar.CalendarDateUtils.getDayOfWeekIndex
import com.example.reptrack.domain.workout.usecases.calendar.CalendarDateUtils.isSecondWeekInMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


class CalendarUseCase(
    private val sessionRepository: TrainingSessionRepository,
    private val templateRepository: TrainingTemplateRepository
) {

    /**
     * Получить неделю календаря для указанной даты
     * @param date Дата для получения недели (используется для определения текущей недели)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeekCalendar(date: LocalDate): Result<CalendarWeek> = try {
        val mondayOfWeek = date.with(DayOfWeek.MONDAY)
        val daysInWeek = (0L..6L).map { mondayOfWeek.plusDays(it) }

        val calendarDays = daysInWeek.map { dayDate ->
            getCalendarDay(dayDate)
        }

        Result.success(
            CalendarWeek(
                days = calendarDays,
                weekStartDate = mondayOfWeek
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Получить месячный вид календаря
     * @param date Дата для определения месяца
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMonthCalendar(date: LocalDate): Result<CalendarMonth> = try {
        val yearMonth = YearMonth.of(date.year, date.month)
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        val startDate = firstDay.with(DayOfWeek.MONDAY)
        val endDate = lastDay.with(DayOfWeek.SUNDAY)

        val weeks = mutableListOf<CalendarWeek>()
        var currentDate = startDate

        while (currentDate <= endDate) {
            val weekDays = (0L..6L).map { currentDate.plusDays(it) }
            val calendarDays = weekDays.map { dayDate ->
                getCalendarDay(dayDate)
            }

            weeks.add(
                CalendarWeek(
                    days = calendarDays,
                    weekStartDate = currentDate
                )
            )
            currentDate = currentDate.plusDays(7)
        }

        Result.success(
            CalendarMonth(
                weeks = weeks,
                monthIndex = yearMonth.monthValue - 1,
                year = yearMonth.year,
                displayName = yearMonth.month.toString().replaceFirstChar { it.uppercase() } + " ${yearMonth.year}"
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Получить информацию о тренировке для конкретного дня
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getCalendarDay(date: LocalDate): CalendarDay {
        val sessionResult = sessionRepository.getSessionByDate(date)
        val session = sessionResult.getOrNull()

        val dayOfWeekValue = getDayOfWeekIndex(date)
        val isSecondWeek = isSecondWeekInMonth(date)

        val templatesResult = templateRepository.getTemplatesByDayOfWeek(
            dayOfWeekValue,
            isSecondWeek
        )
        val templates = templatesResult.getOrNull() ?: emptyList()

        val hasWorkout = session != null || templates.isNotEmpty()
        val status = session?.status?.let { workoutStatus ->
            when (workoutStatus.toString()) {
                "COMPLETED" -> DayWorkoutStatus.COMPLETED
                "SKIPPED", "CANCELLED" -> DayWorkoutStatus.SKIPPED
                else -> DayWorkoutStatus.PLANNED
            }
        } ?: if (templates.isNotEmpty()) DayWorkoutStatus.PLANNED else null

        return CalendarDay(
            date = date,
            hasWorkout = hasWorkout,
            status = status,
            workoutSession = session,
            applicableTemplates = templates
        )
    }
}