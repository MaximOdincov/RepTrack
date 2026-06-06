package com.example.reptrack.presentation.profile.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.profile.User
import com.example.reptrack.presentation.profile.stores.ProfileStore

@Composable
fun SettingsSection(
    user: User,
    isGuest: Boolean = false,
    onSignOut: () -> Unit,
    onChangeUsername: (String) -> Unit,
    onChangePasskey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPasskeyEditDialog by remember { mutableStateOf(false) }
    var showPasskeyDialog by remember { mutableStateOf(false) }
    var showThemeInfo by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf(user.username ?: "") }
    var newPasskey by remember { mutableStateOf(user.passkey ?: "") }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Username Section - only show if not guest
            if (!isGuest) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showUsernameDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Username",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Username",
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }.copy(alpha = 0.7f)
                            )
                            Text(
                                text = user.username ?: "Not set",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    IconButton(
                        onClick = { showUsernameDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Edit username",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Passkey Section - only show if not guest
            if (!isGuest) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPasskeyDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Passkey",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Friend Passkey",
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    else -> MaterialTheme.colorScheme.onSurface
                            }.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Tap to view and manage",
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }.copy(alpha = 0.5f)
                            )
                        }
                    }
                    IconButton(
                        onClick = { showPasskeyDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "View passkey",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.height(16.dp))


            // Sign Out Button
            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onError)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.height(16.dp))

                    }
    }

    // Passkey Dialog
    if (showPasskeyDialog) {
        PasskeyDialog(
            passkey = user.passkey ?: "Not set",
            onDismiss = { showPasskeyDialog = false },
            onCopy = { /* TODO: Implement copy to clipboard */ },
            onShare = { /* TODO: Implement share functionality */ },
            onEdit = { showPasskeyEditDialog = true }
        )
    }

    // Passkey Edit Dialog
    if (showPasskeyEditDialog) {
        AlertDialog(
            onDismissRequest = { showPasskeyEditDialog = false },
            title = { Text("Edit Friend Passkey") },
            text = {
                Column {
                    Text(
                        text = "Enter your new friend passkey:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPasskey,
                        onValueChange = { newPasskey = it },
                        label = { Text("Passkey") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPasskey.isNotBlank()) {
                            onChangePasskey(newPasskey)
                            showPasskeyEditDialog = false
                        }
                    },
                    enabled = newPasskey.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showPasskeyEditDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Username Dialog
    if (showUsernameDialog) {
        UsernameDialog(
            currentUsername = user.username ?: "",
            onDismiss = { showUsernameDialog = false },
            onConfirm = { newUsername ->
                if (newUsername.isNotBlank()) {
                    onChangeUsername(newUsername)
                    // TODO: Update username in database
                }
            }
        )
    }
}

@Composable
private fun PasskeyDialog(
    passkey: String,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your Friend Passkey") },
        text = {
            Column {
                Text(
                    text = "Share this passkey with friends to add them:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                                false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.onSurface
                            }.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = passkey,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                                false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                    )
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onCopy,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text("Copy Passkey")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Passkey")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share Passkey")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}


@Composable
private fun UsernameDialog(
    currentUsername: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newUsername by remember { mutableStateOf(currentUsername) }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Username") },
        text = {
            Column {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = {
                        newUsername = it
                        localError = null
                    },
                    label = { Text("Username") },
                    singleLine = true,
                    isError = localError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (localError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = localError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (newUsername.isNotBlank()) {
                            onConfirm(newUsername)
                        } else {
                            localError = "Username cannot be empty"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = newUsername.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    )
}