package com.example.reptrack.presentation.timer.screens.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import com.example.reptrack.presentation.theme.LightAccentOrange

@Composable
fun TimerCircularProgress(
    progress: Float,
    isRunning: Boolean,
    formattedTime: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )

    // Pulsing effect when running
    val pulseScale by animateFloatAsState(
        targetValue = if (isRunning) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "pulse_animation"
    )

    Box(
        modifier = modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick, enabled = !isRunning),
            onDraw = {
                drawTimerProgress(
                    progress = animatedProgress,
                    pulseScale = pulseScale,
                    isRunning = isRunning
                )
            }
        )

        // Time display inside the circle
        androidx.compose.material3.Text(
            text = formattedTime,
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = LightAccentOrange,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun DrawScope.drawTimerProgress(
    progress: Float,
    pulseScale: Float,
    isRunning: Boolean
) {
    val size = size.width
    val strokeWidth = size * 0.06f
    val center = Offset(size / 2, size / 2)
    val radius = (size - strokeWidth) / 2 * pulseScale

    // Draw background circle (grey)
    drawCircle(
        color = Color(0xFFE0E0E0),
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Draw progress arc (orange)
    if (progress > 0f) {
        drawProgressArc(
            center = center,
            radius = radius,
            strokeWidth = strokeWidth,
            progress = progress,
            isRunning = isRunning
        )
    }
}

private fun DrawScope.drawProgressArc(
    center: Offset,
    radius: Float,
    strokeWidth: Float,
    progress: Float,
    isRunning: Boolean
) {
    // Start from top (-90 degrees)
    val startAngle = -90f
    val sweepAngle = 360f * progress

    // Add glow effect when running
    if (isRunning) {
        // Outer glow
        drawArc(
            color = LightAccentOrange.copy(alpha = 0.3f),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius - strokeWidth, center.y - radius - strokeWidth),
            size = Size(radius * 2 + strokeWidth * 2, radius * 2 + strokeWidth * 2),
            style = Stroke(width = strokeWidth * 2, cap = StrokeCap.Round)
        )
    }

    // Main progress arc with gradient
    drawArc(
        brush = Brush.linearGradient(
            colors = listOf(
                LightAccentOrange.copy(alpha = 0.8f),
                LightAccentOrange
            ),
            start = Offset(center.x - radius, center.y + radius),
            end = Offset(center.x + radius, center.y - radius)
        ),
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}