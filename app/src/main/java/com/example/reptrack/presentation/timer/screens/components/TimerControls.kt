package com.example.reptrack.presentation.timer.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.reptrack.R
import com.example.reptrack.presentation.theme.LightAccentOrange

@Composable
fun TimerControls(
    isRunning: Boolean,
    isPaused: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onOpenTimePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопка сброса (теперь НЕ красная)
        ControlButton(
            icon = Icons.Default.Refresh,
            onClick = onReset,
            enabled = isRunning || isPaused,
            size = 56.dp,
            containerColor = Color(0xFF3A3A3C),
            contentColor = Color.White
        )

        Spacer(modifier = Modifier.width(24.dp))

        // Главная кнопка (Play/Pause/Resume)
        when {
            isRunning -> {
                ControlButton(
                    icon = painterResource(R.drawable.ic_pause),
                    onClick = onPause,
                    enabled = true,
                    size = 80.dp,
                    containerColor = LightAccentOrange,
                    contentColor = Color.White
                )
            }
            isPaused -> {
                ControlButton(
                    icon = Icons.Default.PlayArrow,
                    onClick = onResume,
                    enabled = true,
                    size = 80.dp,
                    containerColor = LightAccentOrange,
                    contentColor = Color.White
                )
            }
            else -> {
                ControlButton(
                    icon = Icons.Default.PlayArrow,
                    onClick = onStart,
                    enabled = true,
                    size = 80.dp,
                    containerColor = LightAccentOrange,
                    contentColor = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Кнопка выбора времени
        ControlButton(
            icon = painterResource(R.drawable.ic_timer),
            onClick = onOpenTimePicker,
            enabled = !isRunning && !isPaused,
            size = 56.dp,
            containerColor = Color(0xFF3A3A3C),
            contentColor = Color.White
        )
    }
}

@Composable
fun ControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    size: androidx.compose.ui.unit.Dp,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (enabled) containerColor else containerColor.copy(alpha = 0.3f),
                CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) contentColor else contentColor.copy(alpha = 0.3f),
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable
fun ControlButton(
    icon: androidx.compose.ui.graphics.painter.Painter,
    onClick: () -> Unit,
    enabled: Boolean,
    size: androidx.compose.ui.unit.Dp,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (enabled) containerColor else containerColor.copy(alpha = 0.3f),
                CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = if (enabled) contentColor else contentColor.copy(alpha = 0.3f),
            modifier = Modifier.size(size * 0.5f)
        )
    }
}