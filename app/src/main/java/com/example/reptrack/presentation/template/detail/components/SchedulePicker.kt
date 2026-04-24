package com.example.reptrack.presentation.template.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.domain.workout.entities.TemplateSchedule

private val DAYS = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

/**
 * Schedule picker component for selecting workout days by week
 */
@Composable
fun SchedulePicker(
    schedule: TemplateSchedule?,
    onDayToggle: (weekNumber: Int, day: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "График тренировок",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Column(modifier = Modifier.padding(top = 12.dp)) {
            ScheduleWeekRow(
                weekNumber = "I",
                selectedDays = schedule?.week1Days ?: emptySet(),
                onDayToggle = { day -> onDayToggle(1, day) }
            )

            ScheduleWeekRow(
                weekNumber = "II",
                selectedDays = schedule?.week2Days ?: emptySet(),
                onDayToggle = { day -> onDayToggle(2, day) }
            )
        }
    }
}

@Composable
private fun ScheduleWeekRow(
    weekNumber: String,
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Week number in Roman numerals
        Text(
            text = weekNumber,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(end = 8.dp)
        )

        // Days
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DAYS.forEachIndexed { index, day ->
                DayPicker(
                    day = day,
                    dayNumber = index + 1, // Display 1-7 instead of 0-6
                    isSelected = index in selectedDays,
                    onClick = { onDayToggle(index) }
                )
            }
        }
    }
}

@Composable
private fun DayPicker(
    day: String,
    dayNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = com.example.reptrack.presentation.theme.LightAccentOrange

    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day oval
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) primaryColor else Color.White
                )
                .border(
                    width = 1.dp,
                    color = if (isSelected) primaryColor else Color.LightGray,
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayNumber.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else primaryColor,
                textAlign = TextAlign.Center
            )
        }

        // Day label
        Text(
            text = day,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
