package com.example.reptrack.presentation.auth.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import com.example.reptrack.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    error: String? = null,
    isError: Boolean = false,
    shouldShowError: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPasswordField: Boolean = false,
    focusRequester: FocusRequester? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val localFocusRequester = remember { FocusRequester() }
    val effectiveFocusRequester = focusRequester ?: localFocusRequester

    val showPasswordToggle = isPasswordField

    val actualVisualTransformation = when {
        isPasswordField && !isPasswordVisible -> PasswordVisualTransformation()
        else -> visualTransformation
    }

    val actualKeyboardType = when {
        isPasswordField && !isPasswordVisible -> KeyboardType.Password
        else -> keyboardType
    }

    val hasError = shouldShowError && (error != null || isError)

    // Отступы только при наличии ошибки
    val verticalPadding = if (hasError) 6.dp else 0.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
                    .focusRequester(effectiveFocusRequester)
                    .clickable(
                        onClick = {
                            focusManager.clearFocus()
                        }
                    ),
                leadingIcon = {
                    leadingIcon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                trailingIcon = {
                    when {
                        showPasswordToggle -> {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    painter = if (isPasswordVisible) painterResource(R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24) else painterResource(R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                    tint = Color.Gray
                                )
                            }
                        }
                        trailingIcon != null -> {
                            trailingIcon()
                        }
                    }
                },
                placeholder = {
                    if (placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = actualKeyboardType,
                    imeAction = imeAction
                ),
                keyboardActions = keyboardActions ?: KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                singleLine = singleLine,
                enabled = enabled,
                isError = hasError,
                visualTransformation = actualVisualTransformation,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    errorCursorColor = MaterialTheme.colorScheme.primary,
                    errorIndicatorColor = MaterialTheme.colorScheme.primary,
                    disabledIndicatorColor = Color.Gray.copy(alpha = 0.12f),
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    disabledTextColor = Color.Black.copy(alpha = 0.38f),
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedPlaceholderColor = Color.Gray,
                    errorTextColor = Color.Black,
                    disabledPlaceholderColor = Color.Gray.copy(alpha = 0.5f),
                    errorContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Gray,
                    errorLabelColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.extraSmall,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black
                )
            )
        }

        // Error message - появляется только при ошибке с вертикальным отступом
        if (hasError) {
            Text(
                text = error ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(top = verticalPadding)
            )
        }
    }
}