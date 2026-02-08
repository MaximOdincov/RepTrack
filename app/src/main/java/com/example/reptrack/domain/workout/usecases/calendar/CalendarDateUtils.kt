package com.example.reptrack.domain.workout.usecases.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

object CalendarDateUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeekNumberInMonth(date: LocalDate): Int {
        val firstDayOfMonth = LocalDate.of(date.year, date.month, 1)
        val dayOfWeek = firstDayOfMonth.dayOfWeek.value
        val dayOfMonth = date.dayOfMonth

        val daysBeforeFirstMonday = (dayOfWeek - 1)
        val daysAfterFirstDay = dayOfMonth - 1

        return ((daysAfterFirstDay + daysBeforeFirstMonday) / 7) + 1
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun isSecondWeekInMonth(date: LocalDate): Boolean {
        return getWeekNumberInMonth(date) > 1
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfWeekIndex(date: LocalDate): Int {
        return (date.dayOfWeek.value % 7)
    }

    fun getDayName(dayIndex: Int): String {
        return listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").getOrNull(dayIndex) ?: "MON"
    }
}
