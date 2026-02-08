package com.example.reptrack.presentation.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.domain.workout.CalendarDay
import com.example.reptrack.domain.workout.CalendarMonth
import com.example.reptrack.domain.workout.CalendarWeek
import com.example.reptrack.domain.workout.DayWorkoutStatus
import com.example.reptrack.presentation.theme.LightAccentGreen
import com.example.reptrack.presentation.theme.LightAccentOrange
import com.example.reptrack.presentation.theme.LightAccentRed
import com.example.reptrack.presentation.theme.DarkAccentGreen
import com.example.reptrack.presentation.theme.DarkAccentOrange
import com.example.reptrack.presentation.theme.DarkAccentRed
import java.time.LocalDate
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * Calendar component with week and month views
 */
@Composable
fun Calendar(
    currentDate: LocalDate,
    displayDate: LocalDate,
    weekCalendar: CalendarWeek?,
    monthCalendar: CalendarMonth?,
    isCalendarExpanded: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onNavigateWeek: (offset: Int) -> Unit,
    onNavigateMonth: (offset: Int) -> Unit,
    onExpandCalendar: () -> Unit,
    onCollapseCalendar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header with month name
        Text(
            text = getMonthDisplayName(displayDate),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        if (isCalendarExpanded && monthCalendar != null) {
            MonthView(
                monthCalendar = monthCalendar,
                currentDate = currentDate,
                onDateSelected = onDateSelected,
                onNavigateMonth = onNavigateMonth,
                onCollapse = onCollapseCalendar
            )
        } else if (weekCalendar != null) {
            WeekView(
                weekCalendar = weekCalendar,
                currentDate = currentDate,
                onDateSelected = onDateSelected,
                onNavigateWeek = onNavigateWeek,
                onExpand = onExpandCalendar
            )
        }

        // Drop shadow
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.1f)
                )
        )
    }
}

/**
 * Week view with smooth carousel animation
 * Shows hint of previous/next week, snaps to week on swipe
 */
@Composable
private fun WeekView(
    weekCalendar: CalendarWeek,
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onNavigateWeek: (offset: Int) -> Unit,
    onExpand: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Animation state for swipe gesture
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Page width - we want to show full week plus hints of neighbors
    val pageWidth = 400f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                val velocityTracker = VelocityTracker()
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().x
                        val threshold = pageWidth * 0.3f // 30% of page width
                        val velocityThreshold = 800f

                        coroutineScope.launch {
                            when {
                                dragOffset < -threshold || velocity < -velocityThreshold -> {
                                    // Swipe left - navigate next
                                    dragOffset = -pageWidth
                                    onNavigateWeek(1)
                                    dragOffset = pageWidth // Start from right for incoming
                                    dragOffset = 0f // Snap to center
                                }
                                dragOffset > threshold || velocity > velocityThreshold -> {
                                    // Swipe right - navigate previous
                                    dragOffset = pageWidth
                                    onNavigateWeek(-1)
                                    dragOffset = -pageWidth // Start from left for incoming
                                    dragOffset = 0f // Snap to center
                                }
                                else -> {
                                    // Return to center - not enough swipe
                                    dragOffset = 0f
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        dragOffset += dragAmount
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount > 50) {
                        onExpand()
                    }
                    change.consume()
                }
            }
    ) {
        // Animated container for smooth transitions
        val animatedOffset by animateFloatAsState(
            targetValue = dragOffset,
            animationSpec = spring(
                dampingRatio = 0.85f,
                stiffness = 380f,
                visibilityThreshold = 0.5f
            ),
            label = "week_offset"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = animatedOffset.toInt(), y = 0) }
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center
        ) {
            weekCalendar.days.forEach { calendarDay ->
                DayCell(
                    calendarDay = calendarDay,
                    isSelected = calendarDay.date == currentDate,
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

/**
 * Month view with smooth carousel animation
 */
@Composable
private fun MonthView(
    monthCalendar: CalendarMonth,
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onNavigateMonth: (offset: Int) -> Unit,
    onCollapse: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val pageWidth = 400f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                val velocityTracker = VelocityTracker()
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().x
                        val threshold = pageWidth * 0.3f
                        val velocityThreshold = 800f

                        coroutineScope.launch {
                            when {
                                dragOffset < -threshold || velocity < -velocityThreshold -> {
                                    dragOffset = -pageWidth
                                    onNavigateMonth(1)
                                    dragOffset = pageWidth
                                    dragOffset = 0f
                                }
                                dragOffset > threshold || velocity > velocityThreshold -> {
                                    dragOffset = pageWidth
                                    onNavigateMonth(-1)
                                    dragOffset = -pageWidth
                                    dragOffset = 0f
                                }
                                else -> {
                                    dragOffset = 0f
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        dragOffset += dragAmount
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount < -50) {
                        onCollapse()
                    }
                    change.consume()
                }
            }
    ) {
        val animatedOffset by animateFloatAsState(
            targetValue = dragOffset,
            animationSpec = spring(
                dampingRatio = 0.85f,
                stiffness = 380f,
                visibilityThreshold = 0.5f
            ),
            label = "month_offset"
        )
        // Day names header - centered
        val dayNames = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dayNames.forEach { dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .width(52.dp)
                        .wrapContentWidth(align = Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Month weeks
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = animatedOffset.toInt(), y = 0) }
                .align(Alignment.CenterHorizontally)
        ) {
            Column {
                monthCalendar.weeks.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        week.days.forEach { calendarDay ->
                            CompactDayCell(
                                calendarDay = calendarDay,
                                isSelected = calendarDay.date == currentDate,
                                onDateSelected = onDateSelected
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

/**
 * Full day cell with day name, dot and number (for week view)
 */
@Composable
private fun DayCell(
    calendarDay: CalendarDay,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance < 0.5f

    // Animate selection
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkTheme) DarkAccentOrange else LightAccentOrange
        } else {
            Color.Transparent
        },
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "background_color"
    )

    Column(
        modifier = Modifier
            .width(52.dp)
            .clickable { onDateSelected(calendarDay.date) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day of week
        val dayOfWeek = calendarDay.date.dayOfWeek.name.take(3)
        Text(
            text = dayOfWeek,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Status dot with scale animation
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
                        DayWorkoutStatus.SKIPPED -> if (isDarkTheme) DarkAccentRed else LightAccentRed
                        DayWorkoutStatus.COMPLETED -> if (isDarkTheme) DarkAccentGreen else LightAccentGreen
                        DayWorkoutStatus.PLANNED -> if (isDarkTheme) DarkAccentOrange else LightAccentOrange
                        null -> Color.Transparent
                    }
                )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Day number with selection circle
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
 * Compact day cell without day name (for month view)
 */
@Composable
private fun CompactDayCell(
    calendarDay: CalendarDay,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance < 0.5f

    // Animate selection
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkTheme) DarkAccentOrange else LightAccentOrange
        } else {
            Color.Transparent
        },
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "background_color"
    )

    Column(
        modifier = Modifier
            .width(52.dp)
            .height(52.dp)
            .clickable { onDateSelected(calendarDay.date) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Status dot with scale animation
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
                        DayWorkoutStatus.SKIPPED -> if (isDarkTheme) DarkAccentRed else LightAccentRed
                        DayWorkoutStatus.COMPLETED -> if (isDarkTheme) DarkAccentGreen else LightAccentGreen
                        DayWorkoutStatus.PLANNED -> if (isDarkTheme) DarkAccentOrange else LightAccentOrange
                        null -> Color.Transparent
                    }
                )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Day number with selection circle
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = calendarDay.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
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

/**
 * Helper to check if color is dark (for theme detection)
 */
private val Color.luminance: Float
    get() {
        val r = red * 255
        val g = green * 255
        val b = blue * 255
        return ((0.299 * r + 0.587 * g + 0.114 * b) / 255).toFloat()
    }
