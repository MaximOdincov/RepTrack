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
    onAddFriend: (String, String) -> Unit
) {
    android.util.Log.d("important", "=== AddFriendDialog created ===")
    android.util.Log.d("important", "availableFriends: ${availableFriends.map { it.friendUserId to (it.username ?: it.email ?: "Unknown") }}")
    android.util.Log.d("important", "addedFriends: $addedFriends")

    var selectedFriendId by remember { mutableStateOf<String?>(null) }
    var selectedFriendName by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить друга в график") },
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
                            text = "Выберите друга:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (availableFriends.isEmpty()) {
                        Text(
                            text = "Нет доступных друзей. Сначала добавьте друзей в вашем профиле.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        val selectableFriends = availableFriends.filter { it.friendUserId !in addedFriends }
                        if (selectableFriends.isEmpty()) {
                            Text(
                                text = "Все друзья уже добавлены в этот график.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.height(80.dp)
                            ) {
                                items(selectableFriends) { friend ->
                                    val friendDisplayName = friend.username ?: friend.email ?: "Friend ${friend.friendUserId.take(8)}"
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                android.util.Log.d("important", "=== Friend row clicked ===")
                                                android.util.Log.d("important", "friendUserId: ${friend.friendUserId}")
                                                android.util.Log.d("important", "username: ${friend.username}")
                                                android.util.Log.d("important", "email: ${friend.email}")
                                                android.util.Log.d("important", "friendDisplayName: $friendDisplayName")
                                                selectedFriendId = friend.friendUserId
                                                selectedFriendName = friendDisplayName
                                                android.util.Log.d("important", "After selection: selectedFriendId=$selectedFriendId, selectedFriendName=$selectedFriendName")
                                            }
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedFriendId == friend.friendUserId,
                                            onClick = {
                                                android.util.Log.d("important", "=== RadioButton clicked ===")
                                                android.util.Log.d("important", "friendUserId: ${friend.friendUserId}")
                                                android.util.Log.d("important", "username: ${friend.username}")
                                                android.util.Log.d("important", "email: ${friend.email}")
                                                android.util.Log.d("important", "friendDisplayName: $friendDisplayName")
                                                selectedFriendId = friend.friendUserId
                                                selectedFriendName = friendDisplayName
                                                android.util.Log.d("important", "After selection: selectedFriendId=$selectedFriendId, selectedFriendName=$selectedFriendName")
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = friendDisplayName,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    android.util.Log.d("important", "=== Add friend button clicked ===")
                    android.util.Log.d("important", "selectedFriendId: $selectedFriendId")
                    android.util.Log.d("important", "selectedFriendName: $selectedFriendName")
                    selectedFriendId?.let { friendId ->
                        selectedFriendName?.let { friendName ->
                            android.util.Log.d("important", "Calling onAddFriend with friendId=$friendId, friendName=$friendName")
                            onAddFriend(friendId, friendName)
                            onDismiss() // Close dialog after adding
                        }
                    } ?: android.util.Log.d("important", "No friend selected!")
                },
                enabled = selectedFriendId != null
            ) {
                    Text("Добавить")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Отмена")
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
        title = { Text("Выберите цвет") },
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
                Text("Готово")
            }
        }
    )
}

@Composable
fun FriendExerciseErrorDialog(
            message: String = "У друга нет этого упражнения. Вы можете отслеживать только общие упражнения.",
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Невозможно добавить друга") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}