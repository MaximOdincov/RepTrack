package com.example.reptrack.presentation.profile.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.friends.Friend
import com.example.reptrack.presentation.profile.stores.FriendsStore
import kotlinx.coroutines.launch

@Composable
fun ProfileHeader(
    username: String?,
    email: String?,
    avatarUrl: String?,
    isSyncing: Boolean = false,
    syncError: String? = null,
    lastSyncTime: Long = 0,
    onAvatarClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable(onClick = onAvatarClick)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl != null) {
                // TODO: Load image from URL (need Coil implementation)
                Text(
                    text = username?.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Avatar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Username and Email
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = username ?: "Guest",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Sync and Settings Icons
        Row {
            // Sync Icon
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Box {
                    IconButton(onClick = onSyncClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = if (syncError != null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                    // Sync status indicator
                    if (syncError != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Sync Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(16.dp)
                                .offset(x = 12.dp, y = (-8).dp)
                        )
                    } else if (lastSyncTime > 0) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Synced",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(12.dp)
                                .offset(x = 10.dp, y = (-6).dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Settings Icon
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
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
                        contentDescription = "Friends",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Friends",
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
                        "Collapse"
                    } else {
                        "Expand"
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

                    // Add Friend Button
                    Button(
                        onClick = {
                            android.util.Log.d("FriendsSection", "Opening Add Friend Dialog")
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
                            contentDescription = "Add Friend"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Friend")
                    }

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
                            text = "No friends yet. Add your first friend!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                }
            }
        }
    }

    // Show error/success
    LaunchedEffect(state.error) {
        if (state.error != null) {
            android.util.Log.e("FriendsSection", "Error: ${state.error}")
        }
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            android.util.Log.d("FriendsSection", "Success: ${state.successMessage}")
            kotlinx.coroutines.delay(2000)
            store.accept(FriendsStore.Intent.ClearSuccessMessage)
        }
    }

    // Add Friend Dialog
    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = {
                android.util.Log.d("FriendsSection", "Closing Add Friend Dialog")
                showAddFriendDialog = false
                store.accept(FriendsStore.Intent.ClearError)
            },
            onAddFriend = { email, passkey ->
                android.util.Log.d("FriendsSection", "Adding friend: $email")
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

                // Username
                Text(
                    text = friend.username ?: "Unknown",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete friend",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
}

@Composable
fun AddFriendDialog(
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
            kotlinx.coroutines.delay(500)
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
                        text = "Add Friend",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { if (!localIsLoading) onDismiss() },
                        enabled = !localIsLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Instructions
                Text(
                    text = "Enter your friend's email and passkey.",
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
                    label = { Text("Passkey") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Passkey"
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
                        android.util.Log.d("AddFriendDialog", "Add Friend button clicked")
                        android.util.Log.d("AddFriendDialog", "Email: $email, Passkey: $passkey")

                        if (email.isBlank() || passkey.isBlank()) {
                            localError = "Please fill in all fields"
                            android.util.Log.d("AddFriendDialog", "Validation failed - empty fields")
                            return@Button
                        }

                        localIsLoading = true
                        localError = null
                        android.util.Log.d("AddFriendDialog", "Calling onAddFriend...")
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
                        Text("Add Friend", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
