package com.example.reptrack.presentation.template.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import com.example.reptrack.presentation.utils.painterResourceSafe
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import androidx.core.graphics.toColorInt

/**
 * Карточка шаблона тренировки
 * Отличается от ExerciseCard более крупным размером и отображением дополнительной информации
 *
 * @param template Шаблон тренировки для отображения
 * @param modifier Modifier для карточки
 * @param onClick Callback при клике на карточку
 */
@Composable
fun TemplateCard(
    template: WorkoutTemplate,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val iconTint = try {
        template.iconColor?.let { Color(it.toColorInt()) }
            ?: Color(0xFF2196F3)
    } catch (e: Exception) {
        Color(0xFF2196F3)
    }

    // Основной цвет фона - более темный и насыщенный
    val backgroundColor = iconTint.copy(alpha = 0.12f)

    // Просто используем iconRes с fallback на дефолтную
    // Проверяем и на null, и на 0 (0 - валидное значение int, но не валидный resource ID)
    val iconResId = if (template.iconRes != null && template.iconRes != 0) {
        template.iconRes
    } else {
        R.drawable.exercise_default_icon
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Большой иконка с эффектом размытия
            Box(
                modifier = Modifier.size(64.dp)
            ) {
                // Фоновый размытый круг
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = iconTint.copy(alpha = 0.20f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .blur(radius = 8.dp)
                )

                // Резкая иконка сверху
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .matchParentSize(),
                    painter = painterResourceSafe(id = iconResId),
                    contentDescription = null,
                    tint = iconTint
                )
            }

            // Информация о шаблоне
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                // Название шаблона
                Text(
                    text = template.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Описание (если есть)
                if (template.description.isNotBlank()) {
                    Text(
                        text = template.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Информация о количестве упражнений и группах мышц
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Количество упражнений
                    Text(
                        text = "${template.exerciseIds.size} упр.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = iconTint
                    )

                    // Разделитель
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(1.dp)
                            .height(14.dp)
                            .background(Color.Gray.copy(alpha = 0.5f))
                    )

                    // Группы мышц
                    Text(
                        text = template.muscleGroups
                            .take(3)
                            .joinToString(", ") { it.getDisplayName() }
                            .let { if (template.muscleGroups.size > 3) "$it..." else it },
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Вспомогательная функция для получения отображаемого имени группы мышц
 */
private fun com.example.reptrack.domain.workout.entities.MuscleGroup.getDisplayName(): String {
    return when (this) {
        com.example.reptrack.domain.workout.entities.MuscleGroup.CHEST -> "Грудь"
        com.example.reptrack.domain.workout.entities.MuscleGroup.BACK -> "Спина"
        com.example.reptrack.domain.workout.entities.MuscleGroup.LEGS -> "Ноги"
        com.example.reptrack.domain.workout.entities.MuscleGroup.ARMS -> "Руки"
        com.example.reptrack.domain.workout.entities.MuscleGroup.ABS -> "Пресс"
        com.example.reptrack.domain.workout.entities.MuscleGroup.CARDIO -> "Кардио"
    }
}

/**
 * Swipe to delete template card with modern animation
 */
@Composable
fun SwipeToDeleteTemplateCard(
    template: WorkoutTemplate,
    onClick: () -> Unit,
    onDeleteTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val deleteThreshold = -300f
    val haptic = LocalHapticFeedback.current

    // Calculate icon scale and opacity based on swipe progress
    val swipeProgress = (kotlin.math.abs(offsetX) / kotlin.math.abs(deleteThreshold)).coerceIn(0f, 1f)
    val iconScale by animateFloatAsState(
        targetValue = if (swipeProgress > 0.01f) 0.3f + (swipeProgress * 0.7f) else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "icon_scale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = swipeProgress,
        animationSpec = tween(durationMillis = 100),
        label = "icon_alpha"
    )

    Box(
        modifier = modifier
    ) {
        // Delete icon (appears when swiping)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
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

        // Template card (swipeable)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (offsetX == 0f) {
                            onClick()
                        }
                    }
                )
                .pointerInput(Unit) {
                    var hasVibrated = false
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < deleteThreshold) {
                                // Trigger delete
                                onDeleteTemplate()
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
            TemplateCard(
                template = template,
                onClick = {},
                modifier = Modifier
            )
        }
    }
}
