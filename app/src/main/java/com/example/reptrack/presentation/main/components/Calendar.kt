package com.example.reptrack.presentation.main.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.domain.workout.CalendarDay
import com.example.reptrack.domain.workout.CalendarMonth
import com.example.reptrack.domain.workout.CalendarWeek
import com.example.reptrack.domain.workout.DayWorkoutStatus
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

/**
 * Календарь с поддержкой недельного и месячного вида с плавными анимациями
 */
@Composable
fun Calendar(
    currentDate: LocalDate = LocalDate.now(),
    weekCalendar: CalendarWeek? = null,
    monthCalendar: CalendarMonth? = null,
    onDateSelected: (LocalDate) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMedium))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (isExpanded && monthCalendar != null) {
            ExpandedCalendarView(
                monthCalendar = monthCalendar,
                currentDate = currentDate,
                onDateSelected = onDateSelected,
                onCollapse = { isExpanded = false }
            )
        } else if (weekCalendar != null) {
            WeekCalendarView(
                weekCalendar = weekCalendar,
                currentDate = currentDate,
                onDateSelected = onDateSelected,
                onExpand = { isExpanded = true }
            )
        }
    }
}

/**
 * Недельный вид календаря с жестами свайпа
 */
@Composable
private fun WeekCalendarView(
    weekCalendar: CalendarWeek,
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onExpand: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount > 50) {
                        onExpand()
                    }
                    change.consume()
                }
            }
    ) {
        // Заголовок с месяцем
        val yearMonth = YearMonth.from(weekCalendar.weekStartDate)
        Text(
            text = yearMonth.month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            ).replaceFirstChar { it.uppercase() } + " ${yearMonth.year}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Дни недели
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val dayNames = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            dayNames.forEachIndexed { index, dayName ->
                WeekDayCell(
                    dayName = dayName,
                    calendarDay = weekCalendar.days.getOrNull(index),
                    isSelected = weekCalendar.days.getOrNull(index)?.date == currentDate,
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

/**
 * Ячейка дня недели
 */
@Composable
private fun RowScope.WeekDayCell(
    dayName: String,
    calendarDay: CalendarDay?,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(enabled = calendarDay != null) {
                calendarDay?.let { onDateSelected(it.date) }
            }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Статус индикатор
        if (calendarDay?.status != null) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(
                        when (calendarDay.status) {
                            DayWorkoutStatus.SKIPPED -> Color.Red
                            DayWorkoutStatus.COMPLETED -> Color.Black
                            DayWorkoutStatus.PLANNED -> Color(0xFFFFA500)
                        }
                    )
            )
        } else {
            Spacer(modifier = Modifier.size(6.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = dayName,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0xFFFFA500) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = calendarDay?.date?.dayOfMonth?.toString() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.Unspecified,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Расширенный (месячный) вид календаря
 */
@Composable
private fun ExpandedCalendarView(
    monthCalendar: CalendarMonth,
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onCollapse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMedium))
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount < -50) {
                        onCollapse()
                    }
                    change.consume()
                }
            }
    ) {
        // Заголовок
        Text(
            text = monthCalendar.displayName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Сетка календаря
        monthCalendar.weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val dayNames = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                week.days.forEachIndexed { index, day ->
                    ExpandedDayCell(
                        dayName = dayNames[index],
                        calendarDay = day,
                        isSelected = day.date == currentDate,
                        onDateSelected = onDateSelected
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Ячейка дня в месячном виде
 */
@Composable
private fun RowScope.ExpandedDayCell(
    dayName: String,
    calendarDay: CalendarDay,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { onDateSelected(calendarDay.date) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0xFFFFA500) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (calendarDay.status != null) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                when (calendarDay.status) {
                                    DayWorkoutStatus.SKIPPED -> Color.Red
                                    DayWorkoutStatus.COMPLETED -> Color.Black
                                    DayWorkoutStatus.PLANNED -> Color(0xFFFFA500)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                }

                Text(
                    text = calendarDay.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color.Unspecified,
                    fontSize = 11.sp
                )
            }
        }
    }
}

