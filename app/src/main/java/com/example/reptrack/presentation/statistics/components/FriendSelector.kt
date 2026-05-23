package com.example.reptrack.presentation.statistics.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.domain.statistics.entities.FriendConfig

@Composable
fun FriendSelector(
    friends: List<Friend>,
    selectedFriends: List<FriendConfig>,
    onFriendSelected: (Friend) -> Unit,
    onFriendRemoved: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Compare with Friends",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (selectedFriends.isEmpty()) {
                Text(
                    text = "No friends selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn {
                    items(selectedFriends) { friendConfig ->
                        FriendItem(
                            name = friendConfig.friendName,
                            onRemove = { onFriendRemoved(friendConfig.friendId) }
                        )
                    }
                }
            }

            // Add friend button would go here
            // For now, just showing available friends below
            if (friends.isNotEmpty()) {
                Text(
                    text = "Available friends:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                )
                LazyColumn {
                    items(friends) { friend ->
                        if (friend.status.name == "ACCEPTED" &&
                            selectedFriends.none { it.friendId == friend.friendUserId }) {
                            FriendItem(
                                name = friend.username ?: "Unknown",
                                onClick = { onFriendSelected(friend) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendItem(
    name: String,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        if (onRemove != null) {
            Text(
                text = "Remove",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

@Composable
fun FriendExerciseErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Cannot Add Friend")
        },
        text = {
            Text(message)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}