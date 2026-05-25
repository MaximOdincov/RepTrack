package com.example.reptrack.presentation.timer.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.presentation.theme.LightAccentOrange
import kotlinx.coroutines.launch

@Composable
fun TimePickerDialog(
    currentDurationSeconds: Int,
    onDismiss: () -> Unit,
    onConfirm: (seconds: Int) -> Unit
) {
    var hours by remember { mutableIntStateOf(currentDurationSeconds / 3600) }
    var minutes by remember { mutableIntStateOf((currentDurationSeconds % 3600) / 60) }
    var seconds by remember { mutableIntStateOf(currentDurationSeconds % 60) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Timer Duration",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                // Preset buttons
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                val presets = listOf(
                    Pair("30s", 30),
                    Pair("1m", 60),
                    Pair("2m", 120),
                    Pair("5m", 300),
                    Pair("10m", 600),
                    Pair("15m", 900)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    presets.forEach { (label, duration) ->
                        PresetButton(
                            label = label,
                            isSelected = hours * 3600 + minutes * 60 + seconds == duration,
                            onClick = {
                                hours = duration / 3600
                                minutes = (duration % 3600) / 60
                                seconds = duration % 60
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TimeWheel(
                        label = "Hours",
                        range = 0..23,
                        selectedValue = hours,
                        onValueChange = { hours = it },
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    TimeWheel(
                        label = "Minutes",
                        range = 0..59,
                        selectedValue = minutes,
                        onValueChange = { minutes = it },
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    TimeWheel(
                        label = "Seconds",
                        range = 0..59,
                        selectedValue = seconds,
                        onValueChange = { seconds = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(hours * 3600 + minutes * 60 + seconds) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightAccentOrange
                )
            ) {
                Text("Start Timer")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PresetButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(60.dp)
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) LightAccentOrange else MaterialTheme.colorScheme.surfaceVariant,
        border = if (!isSelected) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        } else null
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TimeWheel(
    label: String,
    range: IntRange,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val values = range.toList()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedValue) {
        coroutineScope.launch {
            lazyListState.animateScrollToItem(selectedValue)
        }
    }

    Column(
        modifier = modifier.height(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            // Center selection indicator
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        LightAccentOrange.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
            )

            LazyColumn(
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.height(120.dp)
            ) {
                itemsIndexed(values) { index, value ->
                    val isSelected = value == selectedValue
                    val scale = if (isSelected) 1.2f else 0.9f
                    val alpha = if (isSelected) 1f else 0.5f

                    Text(
                        text = value.toString().padStart(2, '0'),
                        modifier = Modifier
                            .height(40.dp)
                            .clickable {
                                onValueChange(value)
                            }
                            .scale(scale),
                        fontSize = 20.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) LightAccentOrange else MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}