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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
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
    var newPasskey by remember { mutableStateOf(user.friendCode ?: "") }
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

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
                        text = "Настройки",
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
                            contentDescription = "Имя пользователя",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Имя пользователя",
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }.copy(alpha = 0.7f)
                            )
                            Text(
                                text = user.username ?: "Не указано",
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
                            contentDescription = "Редактировать имя пользователя",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

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
                            contentDescription = "Код дружбы",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Код дружбы",
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Нажмите, чтобы просмотреть и управлять",
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
            }

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
                Text("Выйти", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onError)
            }
        }
    }

    // Passkey Dialog
    if (showPasskeyDialog) {
        PasskeyDialog(
            passkey = user.friendCode ?: "Not set",
            onDismiss = { showPasskeyDialog = false },
            onCopy = { clipboardManager.setText(AnnotatedString(user.friendCode ?: "")) },
            onEdit = { showPasskeyEditDialog = true }
        )
    }

    // Passkey Edit Dialog
    if (showPasskeyEditDialog) {
        AlertDialog(
            onDismissRequest = { showPasskeyEditDialog = false },
            title = { Text("Редактировать код дружбы") },
            text = {
                Column {
                    Text(
                        text = "Введите новый код дружбы:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPasskey,
                        onValueChange = { newPasskey = it },
                        label = { Text("Код дружбы") },
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
                    Text("Сохранить")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showPasskeyEditDialog = false }
                ) {
                    Text("Отмена")
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
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ваш код дружбы") },
        text = {
            Column {
                Text(
                    text = "Поделитесь этим кодом с друзьями, чтобы добавить их:",
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Нажмите \"Копировать\" для сохранения в буфер обмена",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
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
                    Text("Копировать код дружбы")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Редактировать код дружбы")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Закрыть")
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
                title = { Text("Редактировать имя пользователя") },
        text = {
            Column {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = {
                        newUsername = it
                        localError = null
                    },
                        label = { Text("Имя пользователя") },
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
                        Text("Отмена")
                    }
                    Button(
                        onClick = {
                            if (newUsername.isNotBlank()) {
                                onConfirm(newUsername)
                            } else {
                                    localError = "Имя пользователя не может быть пустым"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = newUsername.isNotBlank()
                    ) {
                        Text("Сохранить")
                    }
                }
            }
    )
}