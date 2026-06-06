package com.example.reptrack.presentation.auth.signUp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.reptrack.R
import com.example.reptrack.presentation.auth.components.*
import com.example.reptrack.presentation.auth.components.clearFocusOnTapOutside
import com.example.reptrack.presentation.auth.dialogs.PrivacyPolicyDialog
import com.example.reptrack.presentation.auth.validation.EmailValidator
import com.example.reptrack.presentation.auth.validation.PasswordValidator
import com.example.reptrack.presentation.auth.validation.UsernameValidator
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.example.reptrack.presentation.auth.components.PrimaryButton
import com.example.reptrack.presentation.auth.signUp.SignUpStore
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    store: SignUpStore,
    onAuthorized: () -> Unit,
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by store.states.collectAsState(initial = SignUpStore.State())
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val usernameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    // Validators
    var emailValidator by remember { mutableStateOf(EmailValidator()) }
    var passwordValidator by remember { mutableStateOf(PasswordValidator()) }
    var usernameValidator by remember { mutableStateOf(UsernameValidator()) }

    // Privacy policy dialog visibility
    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Error snackbar
    var showError by remember { mutableStateOf<String?>(null) }

    // Handle labels
    LaunchedEffect(store) {
        store.labels.collectLatest { label ->
            when (label) {
                SignUpStore.Label.Authorize -> onAuthorized()
                is SignUpStore.Label.Error -> {
                    showError = label.msg
                }
                is SignUpStore.Label.EmailValidationChanged -> {
                    emailValidator.isEmailValid = label.isValid
                    emailValidator.emailError = label.error
                }
                is SignUpStore.Label.PasswordValidationChanged -> {
                    passwordValidator.isPasswordValid = label.isValid
                    passwordValidator.passwordError = label.error
                }
                is SignUpStore.Label.UsernameValidationChanged -> {
                    usernameValidator.isUsernameValid = label.isValid
                    usernameValidator.usernameError = label.error
                }
            }
        }
    }


    // Auto-hide error after 5 seconds
    LaunchedEffect(showError) {
        showError?.let {
            kotlinx.coroutines.delay(5000)
            showError = null
        }
    }

    // Update validators with small delay to prevent flickering
    LaunchedEffect(state.email) {
        kotlinx.coroutines.delay(500)
        emailValidator.email = TextFieldValue(state.email)
        emailValidator.validate()
    }

    LaunchedEffect(state.password) {
        kotlinx.coroutines.delay(500)
        passwordValidator.password = TextFieldValue(state.password)
        passwordValidator.validate()
    }

    LaunchedEffect(state.username) {
        kotlinx.coroutines.delay(500)
        usernameValidator.username = TextFieldValue(state.username)
        usernameValidator.validate()
    }

    // Main UI
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clearFocusOnTapOutside(focusManager)
    ) {
        // Handle back press - go back to sign in
        BackHandler {
            onBackToSignIn()
        }

        SnackbarHost(
            hostState = remember { SnackbarHostState() }
        ) { data ->
            AuthErrorSnackbar(
                errorMessage = showError,
                onDismiss = { showError = null }
            )
        }
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "logo",
                )
            }

            // Rounded card with shadow
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Регистрация",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        textAlign = TextAlign.Center
                    )

                    AuthTextField(
                        value = state.username,
                        onValueChange = {
                            store.accept(SignUpStore.Intent.UsernameChanged(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(bringIntoViewRequester),
                        label = stringResource(R.string.auth_username),
                        placeholder = stringResource(R.string.auth_username_placeholder),
                        leadingIcon = Icons.Default.Person,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        enabled = !state.isLoading,
                        error = if (!usernameValidator.isUsernameValid) usernameValidator.usernameError else null,
                        isError = !usernameValidator.isUsernameValid,
                        focusRequester = usernameFocusRequester
                    )

                    Spacer(modifier = Modifier.size(8.dp))
                    AuthTextField(
                        value = state.email,
                        onValueChange = {
                            store.accept(SignUpStore.Intent.EmailChanged(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(bringIntoViewRequester),
                        label = stringResource(R.string.auth_email),
                        placeholder = stringResource(R.string.auth_email_placeholder),
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        enabled = !state.isLoading,
                        error = if (!emailValidator.isEmailValid) emailValidator.emailError else null,
                        isError = !emailValidator.isEmailValid,
                        focusRequester = emailFocusRequester
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    AuthTextField(
                        value = state.password,
                        onValueChange = {
                            store.accept(SignUpStore.Intent.PasswordChanged(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(bringIntoViewRequester),
                        label = stringResource(R.string.auth_password),
                        placeholder = stringResource(R.string.auth_password_placeholder),
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        enabled = !state.isLoading,
                        error = if (!passwordValidator.isPasswordValid) passwordValidator.passwordError else null,
                        isError = !passwordValidator.isPasswordValid,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        isPasswordField = true,
                        focusRequester = passwordFocusRequester
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        PrivacyPolicyCheckbox(
                            checked = state.privacyAccepted,
                            onCheckedChange = {
                                store.accept(SignUpStore.Intent.PrivacyStatusChanged(it))
                            },
                            onShowPrivacyDialog = { showPrivacyDialog = true }
                        )
                    }

                        PrimaryButton(
                            text = stringResource(R.string.auth_sign_up_button),
                            onClick = {
                                focusManager.clearFocus()
                                store.accept(SignUpStore.Intent.SignUpClicked)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            enabled = state.isEmailValid && state.isPasswordValid && state.isUsernameValid && state.privacyAccepted && !state.isLoading,
                            loading = state.isLoading
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.auth_already_have_account),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                onBackToSignIn()
                            },
                            enabled = !state.isLoading
                        ) {
                            Text(
                                text = stringResource(R.string.auth_sign_in),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
            }
        }

        // Privacy policy dialog
        if (showPrivacyDialog) {
            PrivacyPolicyDialog(
                onDismiss = { showPrivacyDialog = false },
                onAccept = {
                    store.accept(SignUpStore.Intent.PrivacyStatusChanged(true))
                    showPrivacyDialog = false
                }
            )
        }

        // Error snackbar
        AnimatedVisibility(
            visible = showError != null,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            if (showError != null) {
                AuthErrorSnackbar(
                    errorMessage = showError,
                    onDismiss = { showError = null },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// Import animations
private fun fadeInAnimation() = fadeIn(
    animationSpec = tween(300)
) + scaleIn(
    animationSpec = tween(300),
    initialScale = 0.9f
)

private fun fadeOutAnimation() = fadeOut(
    animationSpec = tween(200)
) + scaleOut(
    animationSpec = tween(200),
    targetScale = 0.9f
)

private fun slideInFromBottom(
    initialY: Float,
    delayMillis: Int = 0
) = slideInVertically(
    animationSpec = tween(400, delayMillis = delayMillis),
    initialOffsetY = { (initialY * 16).toInt() }
) + fadeInAnimation()

private fun scaleInAnimation() = scaleIn(
    animationSpec = tween(300),
    initialScale = 0.9f
)