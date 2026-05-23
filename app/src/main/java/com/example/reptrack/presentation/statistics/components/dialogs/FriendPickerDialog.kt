package com.example.reptrack.presentation.statistics.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.friends.Friend

@Composable
fun AddFriendDialog(
    availableFriends: List<Friend>,
    addedFriends: List<String>,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onAddFriend: (String, Long) -> Unit,
    showColorPicker: Boolean = false
) {
    var selectedFriendId by remember { mutableStateOf<String?>(null) }
    var initialColor = Color(android.graphics.Color.parseColor("#6200EE"))
    // Convert Color to ARGB Long
    var selectedColor by remember { mutableLongStateOf(
        (initialColor.alpha.toLong() shl 24) or
        (initialColor.red.toLong() shl 16) or
        (initialColor.green.toLong() shl 8) or
        initialColor.blue.toLong()
    ) }
    var showColorPickerDialog by remember { mutableStateOf(false) }

    if (showColorPickerDialog) {
        ColorPickerDialog(
            selectedColor = selectedColor,
            onColorSelected = { selectedColor = it; showColorPickerDialog = false },
            onDismiss = { showColorPickerDialog = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Friend to Chart") },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column {
                    // Friend selection
                    Text(
                        text = "Select a friend:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (availableFriends.isEmpty()) {
                        Text(
                            text = "No friends available. Add friends in your profile first.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        val selectableFriends = availableFriends.filter { it.friendUserId !in addedFriends }
                        if (selectableFriends.isEmpty()) {
                            Text(
                                text = "All friends are already added to this chart.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.height(200.dp)
                            ) {
                                items(selectableFriends) { friend ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedFriendId == friend.friendUserId,
                                            onClick = { selectedFriendId = friend.friendUserId }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = friend.username ?: "Unknown",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Color selection
                    Text(
                        text = "Select chart color:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val availableColors = remember {
                            listOf(
                                Color(0xFF6200EE),
                                Color(0xFF6366F1),
                                Color(0xFFEC4899),
                                Color(0xFF10B981),
                                Color(0xFFF59E0B),
                                Color(0xFFEF4444)
                            )
                        }

                        availableColors.forEach { color ->
                            val colorArgb = (color.alpha.toLong() shl 24) or
                                            (color.red.toLong() shl 16) or
                                            (color.green.toLong() shl 8) or
                                            color.blue.toLong()
                            val isSelected = selectedColor == colorArgb

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = if (isSelected) {
                                            color
                                        } else {
                                            color.copy(alpha = 0.6f)
                                        },
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = colorArgb }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedFriendId?.let {
                        onAddFriend(it, selectedColor)
                    }
                },
                enabled = selectedFriendId != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ColorPickerDialog(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Color") },
        text = {
            Column {
                val colors = remember {
                    listOf(
                        Color(0xFF6200EE),
                        Color(0xFF6366F1), // Indigo
                        Color(0xFFEC4899), // Pink
                        Color(0xFF10B981), // Emerald
                        Color(0xFFF59E0B), // Amber
                        Color(0xFFEF4444), // Red
                        Color(0xFF8B5CF6), // Violet
                        Color(0xFF06B6D4), // Cyan
                        Color(0xFF84CC16), // Lime
                        Color(0xFF3B82F6), // Blue
                        Color(0xFFF97316), // Orange
                        Color(0xFF14B8A6)  // Teal
                    )
                }

                colors.chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { color ->
                            val colorArgb = (color.alpha.toLong() shl 24) or
                                            (color.red.toLong() shl 16) or
                                            (color.green.toLong() shl 8) or
                                            color.blue.toLong()
                            val isSelected = selectedColor == colorArgb

                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        color = if (isSelected) {
                                            color
                                        } else {
                                            color.copy(alpha = 0.6f)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onColorSelected(colorArgb) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun FriendExerciseErrorDialog(
    message: String = "Friend doesn't have this exercise. You can only track common exercises.",
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cannot Add Friend") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}