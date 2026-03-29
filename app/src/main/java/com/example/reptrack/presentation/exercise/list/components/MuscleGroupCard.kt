package com.example.reptrack.presentation.exercise.list.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.key
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors
import com.example.reptrack.presentation.main.components.ExerciseCard

/**
 * Muscle group card with accordion functionality
 *
 * @param muscleGroup The muscle group to display
 * @param exercises List of exercises in this group
 * @param onExerciseClick Callback when exercise is clicked
 * @param onDeleteExercise Callback when exercise is deleted
 * @param expansionState Global expansion state holder
 * @param modifier Modifier for the card
 */
@Composable
fun MuscleGroupCard(
    muscleGroup: MuscleGroup,
    exercises: List<Exercise>,
    onExerciseClick: (Exercise) -> Unit,
    onDeleteExercise: (String) -> Unit,
    expansionState: MuscleGroupExpansionState,
    modifier: Modifier = Modifier
) {
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }
    val groupName = muscleGroup.name
    var isExpanded by remember(groupName) {
        mutableStateOf(expansionState.isExpanded(groupName))
    }

    // Sync state with expansionState
    val currentExpanded = expansionState.isExpanded(groupName)
    if (currentExpanded != isExpanded) {
        isExpanded = currentExpanded
    }

    // Animate arrow rotation with smoother spring animation
    val rotationAngle by animateDpAsState(
        targetValue = if (isExpanded) 180.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "arrow_rotation"
    )

    val groupIcon = getMuscleGroupIcon(muscleGroup)
    val groupDisplayName = getMuscleGroupName(muscleGroup)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clickable(
                    onClick = {
                        isExpanded = !isExpanded
                        expansionState.toggle(groupName)
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = groupIcon),
                contentDescription = groupDisplayName,
                tint = Color.Black,
                modifier = Modifier.size(58.dp).border(BorderStroke(2.dp, Color.Black), CircleShape).padding(6.dp)
            )

            // Group name
            Text(
                text = groupDisplayName,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                painter = painterResource(R.drawable.arrow_up_icon),
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotationAngle.value)
            )
        }

        if (isExpanded) {
            exercises.forEach { exercise ->
                key(exercise.id) {
                    SwipeToDeleteExerciseCard(
                        exercise = exercise,
                        muscleGroup = muscleGroup,
                        onExerciseClick = onExerciseClick,
                        onDeleteExercise = { exerciseToDelete = exercise }
                    )
                }
            }
        }

        // Delete confirmation dialog
        exerciseToDelete?.let { exercise ->
            AlertDialog(
                onDismissRequest = { exerciseToDelete = null },
                title = {
                    Text(text = "Удалить упражнение?")
                },
                text = {
                    Text(text = "Вы уверены, что хотите удалить упражнение \"${exercise.name}\"?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteExercise(exercise.id)
                            exerciseToDelete = null
                        }
                    ) {
                        Text("Удалить", color = Color(0xFFEF5350))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { exerciseToDelete = null }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

/**
 * Swipe to delete exercise card with modern animation
 */
@Composable
private fun SwipeToDeleteExerciseCard(
    exercise: Exercise,
    muscleGroup: MuscleGroup,
    onExerciseClick: (Exercise) -> Unit,
    onDeleteExercise: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val deleteThreshold = -300f
    val haptic = LocalHapticFeedback.current

    // Calculate icon scale and opacity based on swipe progress
    val swipeProgress = (kotlin.math.abs(offsetX) / kotlin.math.abs(deleteThreshold)).coerceIn(0f, 1f)
    val iconScale by animateFloatAsState(
        targetValue = if (swipeProgress > 0.01f) 0.3f + (swipeProgress * 0.7f) else 0f, // Scale from 0.3 to 1.0
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "icon_scale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = swipeProgress,
        animationSpec = tween(durationMillis = 100),
        label = "icon_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Delete icon (appears when swiping)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .alpha(iconAlpha),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFEF5350).copy(alpha = iconAlpha),
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(32.dp)
                    .scale(iconScale)
            )
        }

        // Exercise card (swipeable)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (offsetX == 0f) {
                            onExerciseClick(exercise)
                        }
                    }
                )
                .pointerInput(Unit) {
                    var hasVibrated = false
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < deleteThreshold) {
                                // Trigger delete
                                onDeleteExercise()
                                offsetX = 0f
                            } else {
                                // Animate back
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
                            // Only allow swipe to the left (negative values)
                            if (newOffset <= 0f) {
                                offsetX = newOffset
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
            ExerciseCard(
                exercise = exercise,
                muscleGroupColor = MuscleGroupColors.getPrimaryColor(muscleGroup)
            )
        }
    }
}
