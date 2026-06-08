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
import androidx.compose.runtime.saveable.rememberSaveable
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
    usernameInput: String = "",
    passkeyInput: String = "",
    isEditingUsername: Boolean = false,
    isEditingPasskey: Boolean = false,
    onUsernameInputChange: (String) -> Unit = {},
    onPasskeyInputChange: (String) -> Unit = {},
    onEditingUsernameChange: (Boolean) -> Unit = {},
    onEditingPasskeyChange: (Boolean) -> Unit = {},
    onSignOut: () -> Unit,
    onChangeUsername: (String) -> Unit,
    onChangePasskey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPasskeyDialog by rememberSaveable { mutableStateOf(false) }
    var showUsernameDialog by rememberSaveable { mutableStateOf(false) }
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
                        .clickable {
                            onUsernameInputChange(user.username ?: "")
                            onEditingUsernameChange(true)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
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
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = user.username ?: "Не указано",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            onUsernameInputChange(user.username ?: "")
                            onEditingUsernameChange(true)
                        }
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
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
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
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (user.friendCode.isNullOrBlank()) "Не установлен" else "Нажмите, чтобы просмотреть и управлять",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(
                        onClick = { showPasskeyDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Просмотр кода",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Inline Username Editor
            if (isEditingUsername && !isGuest) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = onUsernameInputChange,
                        label = { Text("Новое имя пользователя") },
                        placeholder = { Text("Введите новое имя") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onEditingUsernameChange(false)
                                onUsernameInputChange("")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Отмена")
                        }
                        Button(
                            onClick = {
                                if (usernameInput.isNotBlank()) {
                                    onChangeUsername(usernameInput)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = usernameInput.isNotBlank() && usernameInput != user.username
                        ) {
                            Text("Сохранить")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Inline Passkey Editor
            if (isEditingPasskey && !isGuest) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passkeyInput,
                        onValueChange = onPasskeyInputChange,
                        label = { Text("Новый код дружбы") },
                        placeholder = { Text("Введите новый код") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onEditingPasskeyChange(false)
                                onPasskeyInputChange("")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Отмена")
                        }
                        Button(
                            onClick = {
                                if (passkeyInput.isNotBlank()) {
                                    onChangePasskey(passkeyInput)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = passkeyInput.isNotBlank()
                        ) {
                            Text("Сохранить")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
            passkey = user.friendCode ?: "Не установлен",
            onDismiss = { showPasskeyDialog = false },
            onCopy = {
                user.friendCode?.let {
                    clipboardManager.setText(AnnotatedString(it))
                }
            },
            onEdit = {
                showPasskeyDialog = false
                onPasskeyInputChange(user.friendCode ?: "")
                onEditingPasskeyChange(true)
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
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Поделитесь этим кодом с друзьями, чтобы добавить их:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Нажмите \"Копировать\" для сохранения в буфер обмена",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
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