package com.example.reptrack.presentation.statistics.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reptrack.presentation.statistics.utils.colorToArgb

@Composable
fun FriendChip(
    name: String,
    color: Color,
    onRemove: () -> Unit,
    onChangeColor: (Color) -> Unit = {}
) {
    android.util.Log.d("FriendChip", "FriendChip created: name=$name, color=$color")
    var showColorPicker by androidx.compose.runtime.remember { mutableStateOf(false) }
    val availableColors = androidx.compose.runtime.remember {
        listOf(
            Color(0xFF6366F1), // Indigo
            Color(0xFFEC4899), // Pink
            Color(0xFF10B981), // Emerald
            Color(0xFFF59E0B), // Amber
            Color(0xFFEF4444), // Red
            Color(0xFF8B5CF6), // Violet
            Color(0xFF06B6D4), // Cyan
            Color(0xFF84CC16)  // Lime
        )
    }

    // Convert available colors to ARGB Long for comparison
    val availableColorsArgb = androidx.compose.runtime.remember {
        availableColors.map { com.example.reptrack.presentation.statistics.utils.colorToArgb(it) }
    }

    Row(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
                .clickable { showColorPicker = true }
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "✕",
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.clickable { onRemove() }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            selectedColor = color,
            availableColors = availableColors,
            onColorSelected = { newColor ->
                android.util.Log.d("FriendChip", "Friend selected color: $newColor")
                val argb = colorToArgb(newColor)
                android.util.Log.d("FriendChip", "Friend selected ARGB: 0x${argb.toString(16)}")
                onChangeColor(newColor)
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@Composable
private fun ColorPickerDialog(
    selectedColor: Color,
    availableColors: List<Color>,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Select Color",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                availableColors.chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (color == selectedColor)
                                            Color.Black.copy(alpha = 0.1f)
                                        else
                                            Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { onColorSelected(color) },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(color, CircleShape)
                                        .then(
                                            if (color == selectedColor)
                                                Modifier.border(2.dp, Color.Black, CircleShape)
                                            else
                                                Modifier
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