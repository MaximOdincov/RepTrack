package com.example.reptrack.presentation.auth.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reptrack.R
import com.example.reptrack.presentation.auth.components.AuthTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onResetPassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(false) }

    // Email validation
    LaunchedEffect(email) {
        isEmailValid = if (email.isEmpty()) {
            false
        } else {
            // Более реалистичная валидация email
            val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            email.matches(Regex(emailRegex))
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = stringResource(R.string.auth_forgot_password),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Enter your email address and we'll send you a link to reset your password.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                AuthTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.auth_email),
                    placeholder = stringResource(R.string.auth_email_placeholder),
                    leadingIcon = Icons.Default.Email,
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                    isError = !isEmailValid && email.isNotEmpty(),
                    error = if (!isEmailValid && email.isNotEmpty()) {
                        if (!email.contains("@")) {
                            "Email must contain @"
                        } else if (email.length < 6) {
                            "Email is too short"
                        } else if (!email.endsWith(".")) {
                            "Email must have domain extension"
                        } else {
                            "Invalid email format"
                        }
                    } else null,
                    enabled = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEmailValid) {
                        onResetPassword(email)
                    }
                },
                enabled = isEmailValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}