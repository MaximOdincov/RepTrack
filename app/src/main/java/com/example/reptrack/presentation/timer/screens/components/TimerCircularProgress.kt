package com.example.reptrack.presentation.timer.screens.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.presentation.theme.LightAccentOrange
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerCircularProgress(
    progress: Float,
    isRunning: Boolean,
    formattedTime: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Анимированный прогресс с плавным переходом
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )

    // Анимация вращения для бегущего таймера
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRunning) 360f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "rotation_animation"
    )

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick, enabled = !isRunning)
        ) {
            drawTimerProgress(
                progress = animatedProgress,
                isRunning = isRunning,
                rotationAngle = rotationAngle
            )
        }

        // Отображение времени внутри круга
        androidx.compose.material3.Text(
            text = formattedTime,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = LightAccentOrange,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun DrawScope.drawTimerProgress(
    progress: Float,
    isRunning: Boolean,
    rotationAngle: Float
) {
    val strokeWidth = size.width * 0.08f
    val radius = (size.width - strokeWidth) / 2
    val center = Offset(size.width / 2, size.height / 2)

    if (isRunning) {
        // Эффект вращающихся точек для визуализации работы таймера
        rotate(rotationAngle) {
            for (i in 0..11) {
                val angle = (i * 30).toDouble()
                val pointRadius = radius * 0.85f
                val x = center.x + (pointRadius * cos(Math.toRadians(angle))).toFloat()
                val y = center.y + (pointRadius * sin(Math.toRadians(angle))).toFloat()

                drawCircle(
                    color = LightAccentOrange.copy(alpha = 0.3f),
                    radius = 4f,
                    center = Offset(x, y)
                )
            }
        }
    }

    // Основной прогресс с градиентом
    if (progress > 0f) {
        val sweepAngle = 360f * progress
        val startAngle = -90f

        // Основная дуга прогресса
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    LightAccentOrange,
                    LightAccentOrange.copy(alpha = 0.7f),
                    LightAccentOrange
                ),
                center = center
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}