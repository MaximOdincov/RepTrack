package com.example.reptrack.presentation.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.domain.workout.CalendarDay
import com.example.reptrack.domain.workout.CalendarWeek
import com.example.reptrack.domain.workout.DayWorkoutStatus
import java.time.LocalDate

/**
 * Saver for LocalDate to enable rememberSaveable
 */
private val LocalDateSaver = Saver<LocalDate, String>(
    save = { it.toString() },
    restore = { LocalDate.parse(it) }
)

/**
 * Calendar component with week view using HorizontalPager
 */
@Composable
fun Calendar(
    initialDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    loadWeekCalendar: suspend (LocalDate) -> CalendarWeek?
) {
    var currentDisplayDate by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(initialDate)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Text(
            text = getMonthDisplayName(currentDisplayDate),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeekView(
            initialDate = initialDate,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            loadWeekCalendar = loadWeekCalendar,
            onDisplayDateChanged = { newDate -> currentDisplayDate = newDate }
        )
    }
}

/**
 * Week view with HorizontalPager for smooth swipe animations
 * Day names are static, only numbers and dots swipe
 */
@Composable
private fun WeekView(
    initialDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    loadWeekCalendar: suspend (LocalDate) -> CalendarWeek?,
    onDisplayDateChanged: (LocalDate) -> Unit
) {
    val baseDate = rememberSaveable { mutableStateOf(initialDate) }

    val middlePage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = middlePage,
        pageCount = { Int.MAX_VALUE }
    )

    LaunchedEffect(pagerState.currentPage) {
        val weeksDiff = pagerState.currentPage - middlePage
        val newDate = baseDate.value.plusWeeks(weeksDiff.toLong())
        onDisplayDateChanged(newDate)
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val pageDate = baseDate.value.plusWeeks((page - middlePage).toLong())
        var weekCalendar by remember { mutableStateOf<CalendarWeek?>(null) }

        LaunchedEffect(pageDate) {
            weekCalendar = loadWeekCalendar(pageDate)
        }

        val calendar = weekCalendar
        if (calendar != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    calendar.days.forEach { calendarDay ->
                        DayNameHeader(
                            dayName = calendarDay.date.dayOfWeek.name.take(3),
                            modifier = Modifier.width(52.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    calendar.days.forEach { calendarDay ->
                        NumberCell(
                            calendarDay = calendarDay,
                            isSelected = calendarDay.date == selectedDate,
                            onDateSelected = onDateSelected
                        )
                    }
                }
            }
        }
    }
}

/**
 * Static day name header (for week view)
 */
@Composable
private fun DayNameHeader(
    dayName: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = dayName,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally),
        textAlign = TextAlign.Center
    )
}

/**
 * Number cell with dot and number only (for week view)
 */
@Composable
private fun NumberCell(
    calendarDay: CalendarDay,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "background_color"
    )

    Column(
        modifier = Modifier
            .width(52.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onDateSelected(calendarDay.date)
            }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val dotScale by animateFloatAsState(
            targetValue = if (calendarDay.status != null) 1f else 0f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
            label = "dot_scale"
        )

        Box(
            modifier = Modifier
                .size(7.dp)
                .scale(dotScale)
                .clip(CircleShape)
                .background(
                    when (calendarDay.status) {
                        DayWorkoutStatus.SKIPPED -> MaterialTheme.colorScheme.secondaryContainer
                        DayWorkoutStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                        DayWorkoutStatus.PLANNED -> MaterialTheme.colorScheme.primaryContainer
                        null -> Color.Transparent
                    }
                )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = calendarDay.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

/**
 * Get month display name from date
 */
private fun getMonthDisplayName(date: LocalDate): String {
    val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$monthName ${date.year}"
}
