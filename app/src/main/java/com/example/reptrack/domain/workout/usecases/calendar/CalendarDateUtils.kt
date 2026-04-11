package com.example.reptrack.domain.workout.usecases.calendar

import java.time.LocalDate

object CalendarDateUtils {

    fun getWeekNumberInMonth(date: LocalDate): Int {
        val firstDayOfMonth = LocalDate.of(date.year, date.month, 1)
        val dayOfWeek = firstDayOfMonth.dayOfWeek.value
        val dayOfMonth = date.dayOfMonth

        val daysBeforeFirstMonday = (dayOfWeek - 1)
        val daysAfterFirstDay = dayOfMonth - 1

        return ((daysAfterFirstDay + daysBeforeFirstMonday) / 7) + 1
    }


    fun isSecondWeekInMonth(date: LocalDate): Boolean {
        val weekNumber = getWeekNumberInMonth(date)
        // Чётные недели (2, 4, 6...) → true
        // Нечётные недели (1, 3, 5...) → false
        return weekNumber % 2 == 0
    }


    fun getDayOfWeekIndex(date: LocalDate): Int {
        // dayOfWeek.value: 1=Monday, 7=Sunday
        //我们需要: 0=Monday, 6=Sunday
        return date.dayOfWeek.value - 1
    }

    fun getDayName(dayIndex: Int): String {
        return listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").getOrNull(dayIndex) ?: "MON"
    }
}
