package com.example.reptrack.presentation.template.detail.screens

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Draggable exercise item with vertical drag-to-reorder and horizontal swipe-to-delete
 */
@Composable
fun DraggableExerciseItem(
    exercise: com.example.reptrack.domain.workout.entities.Exercise,
    index: Int,
    totalItems: Int,
    exerciseIds: List<String>,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    onReorder: (fromIndex: Int, toIndex: Int) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var verticalOffset by remember { mutableFloatStateOf(0f) }
    var horizontalOffset by remember { mutableFloatStateOf(0f) }

    val haptic = LocalHapticFeedback.current
    val deleteThreshold = -300f

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            exerciseName = exercise.name,
            onConfirm = {
                showDeleteDialog = false
                onRemove()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    LaunchedEffect(horizontalOffset) {
        if (horizontalOffset < deleteThreshold) {
            showDeleteDialog = true
            horizontalOffset = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Exercise card (movable layer with gestures)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = verticalOffset
                    translationX = horizontalOffset
                    alpha = if (isDragging) 0.9f else 1f
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            verticalOffset = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            verticalOffset = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newY = verticalOffset + dragAmount.y
                            val itemHeight = 90f

                            when {
                                // Dragging up significantly
                                newY < -itemHeight / 2 && index > 0 -> {
                                    onMoveUp()
                                    verticalOffset = 0f
                                }
                                // Dragging down significantly
                                newY > itemHeight / 2 && index < totalItems - 1 -> {
                                    onMoveDown()
                                    verticalOffset = 0f
                                }
                                else -> {
                                    verticalOffset = newY
                                }
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    var hasVibrated = false
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (horizontalOffset < deleteThreshold) {
                                // Delete is handled by LaunchedEffect
                                horizontalOffset = 0f
                            } else {
                                // Animate back
                                horizontalOffset = 0f
                            }
                            hasVibrated = false
                        },
                        onDragCancel = {
                            horizontalOffset = 0f
                            hasVibrated = false
                        },
                        onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                            val newOffset = horizontalOffset + dragAmount
                            // Only allow swipe to the left (negative values)
                            if (newOffset <= 0f) {
                                horizontalOffset = newOffset
                                // Vibrate when reaching delete threshold
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    com.example.reptrack.presentation.main.components.ExerciseCard(
                        exercise = exercise,
                        muscleGroupColor = com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors.getPrimaryColor(
                            exercise.muscleGroup
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    exerciseName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Удалить упражнение?")
        },
        text = {
            Text("Вы уверены, что хотите удалить «$exerciseName» из шаблона?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Удалить", color = Color(0xFFEF5350))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
