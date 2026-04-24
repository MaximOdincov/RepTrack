package com.example.reptrack.presentation.workout_exercise.detail.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.domain.workout.entities.WorkoutSet
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun SetCard(
    modifier: Modifier = Modifier,
    set: WorkoutSet,
    isExpanded: Boolean = true,
    onToggleExpanded: () -> Unit = {},
    onWeightChanged: (Float) -> Unit,
    onRepsChanged: (Int) -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var offsetX by remember { mutableStateOf(0f) }
    val deleteThreshold = -400f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .offset(x = offsetX.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .pointerInput(Unit) {
                    var hasVibrated = false
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < deleteThreshold) {
                                onDelete()
                                offsetX = 0f
                            } else {
                                offsetX = 0f
                            }
                            hasVibrated = false
                        },
                        onDragCancel = {
                            offsetX = 0f
                            hasVibrated = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            if (newOffset <= 0f) {
                                offsetX = newOffset
                                if (!hasVibrated && newOffset < deleteThreshold) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    hasVibrated = true
                                }
                            }
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (offsetX == 0f) {
                            onToggleExpanded()
                        }
                    }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "▼" else "▶",
                        fontSize = 16.sp,
                        modifier = Modifier.width(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Set ${set.index}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val hasValues = set.weight != null && set.weight!! > 0 && set.reps != null && set.reps!! > 0
                if (hasValues) {
                    Text(
                        text = "${formatValue(set.weight)} × ${set.reps}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Empty",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Weight",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Reps",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompactNumberInput(
                            value = set.weight,
                            onValueChanged = { if (it > 0) onWeightChanged(it) },
                            isWeight = true
                        )

                        Text(
                            text = "×",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        CompactNumberInput(
                            value = set.reps?.toFloat(),
                            onValueChanged = { if (it > 0) onRepsChanged(it.toInt()) },
                            isWeight = false
                        )
                    }
                }
            }
        }
    }
}

private val BoxMinSize = 72.dp
private val BoxWithContentSize = 230.dp

@Composable
private fun CompactNumberInput(
    value: Float?,
    onValueChanged: (Float) -> Unit,
    isWeight: Boolean = true
) {
    var displayValue by remember { mutableFloatStateOf(value ?: 0f) }

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
            isIncrement = false,
            isWeight = isWeight
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .width(52.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatValue(displayValue, isWeight),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IncrementButton(
            value = displayValue,
            onValueChange = { displayValue = it },
            isIncrement = true,
            isWeight = isWeight
        )
    }
}

@Composable
private fun IncrementButton(
    value: Float,
    onValueChange: (Float) -> Unit,
    isIncrement: Boolean,
    isWeight: Boolean
) {
    val currentValue by rememberUpdatedState(value)
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }
    var pressStartTime by remember { mutableStateOf(0L) }

    val singleStep = if (isWeight) 0.5f else 1f
    val acceleratedStep = if (isWeight) 2.5f else 2f

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
            .size(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                color = if (isPressed)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else
                    Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                color = if (isPressed)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {},
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isIncrement) "+" else "−",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPressed)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatValue(value: Float?, isWeight: Boolean = true): String {
    return if (value == null || value == 0f) {
        "0"
    } else {
        if (isWeight) {
            val isWholeNumber = value == value.toInt().toFloat()
            if (isWholeNumber) {
                value.toInt().toString()
            } else {
                String.format("%.1f", value)
            }
        } else {
            value.toInt().toString()
        }
    }
}
