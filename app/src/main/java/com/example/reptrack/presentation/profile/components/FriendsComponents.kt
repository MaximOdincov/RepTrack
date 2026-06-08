package com.example.reptrack.presentation.profile.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.presentation.profile.stores.FriendsStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GuestFeaturesCard(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Разблокируйте расширенные функции",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureItem(
                icon = Icons.Default.Refresh,
                title = "Синхронизация между устройствами",
                description = "Ваши данные всегда под рукой — на любом устройстве"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureItem(
                icon = Icons.Default.Person,
                title = "Статистика с друзьями",
                description = "Сравнивайте прогресс, соревнуйтесь и мотивируйте друг друга"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Зарегистрироваться",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ProfileHeader(
    username: String,
    email: String,
    avatarUrl: String?,
    isGuest: Boolean,
    isSyncing: Boolean,
    syncError: String?,
    lastSyncTime: Long,
    onAvatarClick: () -> Unit,
    onSyncClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isGuest) {

        } else {
            // Registered user mode - full view with avatar and sync
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with MaterialTheme.primary background and black person icon
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .clickable { onAvatarClick() }
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    if (avatarUrl != null && avatarUrl.isNotBlank()) {
                        //Todo
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // User info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(Modifier.size(28.dp))

                    // Username with truncation
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Email with truncation
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Sync info - только для зарегистрированных пользователей
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 8.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                            Text(
                                text = "Синхронизация...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            IconButton(
                                onClick = onSyncClick,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Синхронизировать",
                                    tint = if (syncError != null)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (lastSyncTime > 0) {
                                        "Последняя синхронизация"
                                    } else {
                                        "Синхронизация данных"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (lastSyncTime > 0) {
                                        formatTime(lastSyncTime)
                                    } else {
                                        "Нажмите для синхронизации"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (syncError != null)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (syncError != null) {
                                Text(
                                    text = "Ошибка!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return format.format(date)
}

@Composable
fun FriendsSection(
    store: FriendsStore,
    modifier: Modifier = Modifier
) {
    val state by store.states.collectAsState(initial = FriendsStore.State())
    val coroutineScope = rememberCoroutineScope()
    var showAddFriendDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        store.accept(FriendsStore.Intent.LoadFriends)
    }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        store.accept(FriendsStore.Intent.ToggleSection(FriendsStore.Section.Friends))
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Друзья",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Друзья",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${state.friends.size})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Icon(
                    imageVector = if (FriendsStore.Section.Friends in state.expandedSections) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                        contentDescription = if (FriendsStore.Section.Friends in state.expandedSections) {
                            "Свернуть"
                        } else {
                            "Развернуть"
                        },
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Content
            AnimatedVisibility(
                visible = FriendsStore.Section.Friends in state.expandedSections,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Friends List
                    if (state.isLoading && state.friends.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (state.friends.isEmpty()) {
                        Text(
                            text = "Нет друзей. Добавьте первого друга!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.friends.forEach { friend ->
                                FriendItem(
                                    friend = friend,
                                    onDelete = {
                                        coroutineScope.launch {
                                            store.accept(FriendsStore.Intent.DeleteFriend(friend.id))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add Friend Button at the bottom
                    Button(
                        onClick = {
                            Log.d("FriendsSection", "Opening Add Friend Dialog")
                            showAddFriendDialog = true
                            store.accept(FriendsStore.Intent.ClearError)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить друга"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Добавить друга")
                    }
                }
            }
        }
    }

    // Show error/success
    LaunchedEffect(state.error) {
        if (state.error != null) {
            Log.e("FriendsSection", "Error: ${state.error}")
        }
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            Log.d("FriendsSection", "Success: ${state.successMessage}")
            delay(2000)
            store.accept(FriendsStore.Intent.ClearSuccessMessage)
        }
    }

    // Add Friend Dialog
    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = {
                Log.d("FriendsSection", "Closing Add Friend Dialog")
                showAddFriendDialog = false
                store.accept(FriendsStore.Intent.ClearError)
            },
            onAddFriend = { email: String, passkey: String ->
                Log.d("FriendsSection", "Adding friend: $email")
                store.accept(FriendsStore.Intent.AddFriend(email, passkey))
            },
            isLoading = state.isAddingFriend,
            error = state.error
        )
    }
}

@Composable
fun FriendItem(
    friend: Friend,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (friend.avatarUrl != null) {
                    // TODO: Load image from URL (need Coil implementation)
                    Text(
                        text = friend.username?.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Text(
                        text = friend.username?.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Username
                Text(
                    text = friend.username ?: "Unknown",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                // Friend email
                Text(
                    text = friend.email ?: "Unknown email",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Delete button on the right side
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete friend",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AddFriendDialog(
    onDismiss: () -> Unit,
    onAddFriend: (String, String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    var email by remember { mutableStateOf("") }
    var passkey by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    var localIsLoading by remember { mutableStateOf(false) }

    // Sync with props
    LaunchedEffect(isLoading) {
        localIsLoading = isLoading
    }

    LaunchedEffect(error) {
        if (error != null && !isLoading) {
            localError = error
            localIsLoading = false
        }
    }

    // Clear local error when inputs change
    LaunchedEffect(email, passkey) {
        if (email.isNotBlank() || passkey.isNotBlank()) {
            localError = null
        }
    }

    // Close dialog on success
    LaunchedEffect(isLoading, error) {
        if (!isLoading && error == null && (email.isNotBlank() || passkey.isNotBlank())) {
            // Success - close dialog after a short delay
            delay(500)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = { if (!localIsLoading) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !localIsLoading,
            dismissOnClickOutside = !localIsLoading
        )
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Добавить друга",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { if (!localIsLoading) onDismiss() },
                        enabled = !localIsLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Instructions
                Text(
                        text = "Введите email и код дружбы вашего друга.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        localError = null
                    },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = localError != null,
                    enabled = !localIsLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Passkey Field
                OutlinedTextField(
                    value = passkey,
                    onValueChange = {
                        passkey = it
                        localError = null
                    },
                            label = { Text("Код дружбы") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Код дружбы"
                                )
                            },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = localError != null,
                    enabled = !localIsLoading
                )

                // Error Message
                if (localError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = localError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Add Button
                Button(
                    onClick = {
                        Log.d("AddFriendDialog", "Add Friend button clicked")
                        Log.d(
                            "AddFriendDialog",
                            "Email: $email, Код дружбы: $passkey"
                        )

                        if (email.isBlank() || passkey.isBlank()) {
                                localError = "Пожалуйста, заполните все поля"
                            Log.d(
                                "AddFriendDialog",
                                "Validation failed - empty fields"
                            )
                            return@Button
                        }

                        localIsLoading = true
                        localError = null
                        Log.d("AddFriendDialog", "Calling onAddFriend...")
                        onAddFriend(email, passkey)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !localIsLoading && email.isNotBlank() && passkey.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (localIsLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                                    Text("Добавить друга", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}