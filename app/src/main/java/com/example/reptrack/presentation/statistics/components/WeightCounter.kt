package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WeightCounter(
    currentWeight: Float?,
    onWeightChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactNumberInput(
                value = currentWeight,
                onValueChanged = { if (it > 0) onWeightChanged(it) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "kg",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompactNumberInput(
    value: Float?,
    onValueChanged: (Float) -> Unit
) {
    var displayValue by remember { mutableFloatStateOf(value ?: 70f) }

    LaunchedEffect(displayValue) {
        delay(500)
        if (displayValue != (value ?: 0f)) {
            onValueChanged(displayValue)
        }
    }

    LaunchedEffect(value) {
        if (value != null && value != displayValue) {
            displayValue = value
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IncrementButton(
            value = displayValue,
            onValueChange = { displayValue = it },
            isIncrement = false
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatWeightValue(displayValue),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IncrementButton(
            value = displayValue,
            onValueChange = { displayValue = it },
            isIncrement = true
        )
    }
}

@Composable
private fun IncrementButton(
    value: Float,
    onValueChange: (Float) -> Unit,
    isIncrement: Boolean
) {
    val currentValue by rememberUpdatedState(value)
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }
    var pressStartTime by remember { mutableStateOf(0L) }

    val singleStep = 0.5f
    val acceleratedStep = 2.5f

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isPressed = true
                    isLongPressing = false
                    pressStartTime = System.currentTimeMillis()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                is PressInteraction.Release -> {
                    val pressDuration = System.currentTimeMillis() - pressStartTime
                    if (!isLongPressing && pressDuration < 300) {
                        val newValue = if (isIncrement) {
                            currentValue + singleStep
                        } else {
                            maxOf(0f, currentValue - singleStep)
                        }
                        onValueChange(newValue)
                    }
                    isPressed = false
                    isLongPressing = false
                }
                is PressInteraction.Cancel -> {
                    isPressed = false
                    isLongPressing = false
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed && !isLongPressing) {
            val pressStart = pressStartTime
            delay(300)

            if (isPressed && (System.currentTimeMillis() - pressStart) >= 300) {
                isLongPressing = true
                var delayTime = 150L
                var step = singleStep
                val longPressStartTime = System.currentTimeMillis()

                while (isPressed) {
                    val elapsed = System.currentTimeMillis() - longPressStartTime
                    if (elapsed > 3000) {
                        step = acceleratedStep
                    }

                    val newValue = if (isIncrement) {
                        currentValue + step
                    } else {
                        maxOf(0f, currentValue - step)
                    }
                    onValueChange(newValue)

                    delay(delayTime)
                    delayTime = (delayTime * 0.85).toLong().coerceAtLeast(30)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (isPressed)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else
                    Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isPressed)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {},
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isIncrement) "+" else "−",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPressed)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatWeightValue(value: Float): String {
    val isWholeNumber = value == value.toInt().toFloat()
    return if (isWholeNumber) {
        value.toInt().toString()
    } else {
        String.format("%.1f", value)
    }
}