package com.example.reptrack.presentation.auth.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasswordStrengthIndicator(
    strength: Int, // 0-4 scale
    modifier: Modifier = Modifier
) {
    val animatedStrength by animateFloatAsState(
        targetValue = strength.toFloat(),
        label = "strength_animation"
    )

    val strengthText = when (strength) {
        0 -> "Very Weak"
        1 -> "Weak"
        2 -> "Medium"
        3 -> "Strong"
        4 -> "Very Strong"
        else -> "Unknown"
    }

    val strengthColor = when (strength) {
        0 -> Color(0xFF5252) // Red - Very Weak
        1 -> Color(0xFF9800) // Orange - Weak
        2 -> Color(0xFFC107) // Yellow - Medium
        3 -> Color(0xFF4CAF50) // Green - Strong
        4 -> Color(0xFF2E7D32) // Dark Green - Very Strong
        else -> Color(0xFF9E9E9E) // Gray - Unknown
    }

    val progressColors = listOf(
        Color(0xFF5252),    // Red
        Color(0xFF9800),    // Orange
        Color(0xFFC107),    // Yellow
        Color(0xFF4CAF50),   // Green
        Color(0xFF2E7D32)   // Dark Green
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        AnimatedVisibility(
            visible = strength > 0,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
            exit = androidx.compose.animation.fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Strength label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Сила пароля: ",
                        style = MaterialTheme.typography.bodyLarge.copy(
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = strengthText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            color = strengthColor
                        )
                    )
                }

                // Progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Strength levels
                    val levels = listOf("Very", "Weak", "Medium", "Strong", "Very Strong")

                    levels.forEachIndexed { index, level ->
                        val isActive = index < strength
                        val isCurrent = index == strength - 1
                        val color = if (isActive) progressColors[index] else Color(0xFFE0E0E0)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp)
                                .padding(horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(
                                        color = color,
                                        shape = MaterialTheme.shapes.extraLarge
                                    )
                            )
                        }
                    }
                }

                // Requirements list
                if (strength < 4) {
                    val requirements = mutableListOf<String>()

                    when {
                        strength < 1 -> {
                            requirements.add("8+ characters")
                            requirements.add("Uppercase letter")
                            requirements.add("Lowercase letter")
                            requirements.add("Number")
                        }
                        strength < 2 -> {
                            requirements.add("Uppercase letter")
                            requirements.add("Lowercase letter")
                            requirements.add("Number")
                        }
                        strength < 3 -> {
                            requirements.add("Uppercase letter")
                            requirements.add("Lowercase letter")
                        }
                        strength < 4 -> {
                            requirements.add("More characters")
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        requirements.forEach { requirement ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (strength >= 2) Icons.Default.CheckCircle else Icons.Default.AddCircle,
                                    contentDescription = null,
                                    tint = if (strength >= 2) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    text = requirement,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (strength >= 2) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimplePasswordStrengthIndicator(
    strength: Int,
    modifier: Modifier = Modifier
) {
    val strengthText = when (strength) {
        0 -> "Very Weak"
        1 -> "Weak"
        2 -> "Medium"
        3 -> "Strong"
        4 -> "Very Strong"
        else -> "Unknown"
    }

    val strengthColor = when (strength) {
        0 -> MaterialTheme.colorScheme.error
        1 -> Color(0xFFFF9800)
        2 -> Color(0xFFFFC107)
        3 -> Color(0xFF4CAF50)
        4 -> Color(0xFF2E7D32)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    AnimatedVisibility(
        visible = strength > 0,
        enter = androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.fadeOut()
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (strength) {
                    0, 1 -> Icons.Default.Close
                    2 -> Icons.Default.Info
                    3, 4 -> Icons.Default.CheckCircle
                    else -> Icons.Default.AddCircle
                },
                contentDescription = null,
                tint = strengthColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = strengthText,
                style = MaterialTheme.typography.bodySmall,
                color = strengthColor
            )
        }
    }
}