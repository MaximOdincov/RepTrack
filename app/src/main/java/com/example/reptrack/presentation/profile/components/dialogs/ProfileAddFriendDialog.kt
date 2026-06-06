package com.example.reptrack.presentation.profile.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ProfileAddFriendDialog(
    onDismiss: () -> Unit,
    onAddFriend: (String, String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    var email by remember { mutableStateOf("") }
    var passkey by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    // Sync with props
    LaunchedEffect(isLoading) {
        // Handle loading state
    }

    LaunchedEffect(error) {
        if (error != null && !isLoading) {
            localError = error
        }
    }

    // Clear local error when inputs change
    LaunchedEffect(email, passkey) {
        if (email.isNotBlank() || passkey.isNotBlank()) {
            localError = null
        }
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Friend",
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material3.IconButton(
                    onClick = { if (!isLoading) onDismiss() },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        },
        text = {
            Column {
                // Instructions
                Text(
                    text = "Enter your friend's email and passkey.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
                    enabled = !isLoading
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
                    enabled = !isLoading
                )

                // Error Message
                if (localError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = localError!!,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (email.isBlank() || passkey.isBlank()) {
                            localError = "Please fill in all fields"
                            return@Button
                        }

                        localError = null
                        onAddFriend(email, passkey)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && email.isNotBlank() && passkey.isNotBlank(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Add Friend", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}