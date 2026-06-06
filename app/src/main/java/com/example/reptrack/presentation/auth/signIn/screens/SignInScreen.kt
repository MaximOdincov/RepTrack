package com.example.reptrack.presentation.auth.signIn

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.R
import com.example.reptrack.presentation.auth.components.*
import com.example.reptrack.presentation.auth.components.clearFocusOnTapOutside
import com.example.reptrack.presentation.auth.dialogs.ForgotPasswordDialog
import com.example.reptrack.presentation.auth.validation.EmailValidator
import com.example.reptrack.presentation.auth.validation.PasswordValidator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    store: SignInStore,
    onAuthorized: () -> Unit,
    onOpenSignUp: () -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by store.states.collectAsState(SignInStore.State(email = "", password = "", error = null))
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    // Email and password validators
    val emailValidator = remember { EmailValidator() }
    val passwordValidator = remember { PasswordValidator() }

    // Forgot password dialog visibility
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    
    val context = LocalContext.current
    val idToken = stringResource(R.string.default_web_client_id)
    // Google Sign-In
    val googleClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(idToken)
                .requestEmail()
                .build()
        )
    }

    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val token = account.idToken
                if (token != null) {
                    store.accept(SignInStore.Intent.GoogleSignedIn(token))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                store.accept(SignInStore.Intent.GoogleSignInError("Google sign-in failed: ${e.message}"))
            }
        }
    }

    // Error state for snackbar
    var showError by remember { mutableStateOf<String?>(null) }

    // Handle labels
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                is SignInStore.Label.Authorized -> onAuthorized()
                SignInStore.Label.OpenSignUp -> onOpenSignUp()
                SignInStore.Label.NavigateBack -> onNavigateBack()
                is SignInStore.Label.Error -> {
                    showError = label.message
                }
                is SignInStore.Label.Success -> {
                    showError = label.message
                }

                is SignInStore.Label.EmailValidationChanged -> {
                    // Update the email validator state
                    emailValidator.isEmailValid = label.isValid
                    emailValidator.emailError = label.error
                }

                is SignInStore.Label.PasswordValidationChanged -> {
                    // Update the password validator state
                    passwordValidator.isPasswordValid = label.isValid
                    passwordValidator.passwordError = label.error
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

    // Main UI
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clearFocusOnTapOutside(focusManager)
    ) {
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
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
                    .padding(bottom = 16.dp),
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
                            text = "Вход",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp),
                            textAlign = TextAlign.Center
                    )
                        AuthTextField(
                            value = state.email,
                            onValueChange = {
                                store.accept(SignInStore.Intent.EmailChanged(it))
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
                            error = emailValidator.emailError,
                            isError = !emailValidator.isEmailValid,
                            focusRequester = emailFocusRequester
                        )

                    Spacer(Modifier.size(10.dp))

                        AuthTextField(
                            value = state.password,
                            onValueChange = {
                                store.accept(SignInStore.Intent.PasswordChanged(it))
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
                            error = passwordValidator.passwordError,
                            isError = !passwordValidator.isPasswordValid,
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            isPasswordField = true,
                            focusRequester = passwordFocusRequester
                        )

                    TextButton(
                        onClick = {
                            showForgotPasswordDialog = true
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.auth_forgot_password),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                        PrimaryButton(
                            text = stringResource(R.string.auth_sign_in_button),
                            onClick = {
                                focusManager.clearFocus()
                                store.accept(SignInStore.Intent.SignInClicked)
                            },
                            modifier = Modifier
                                .padding(top = 8.dp),
                            enabled = !state.isLoading,
                            loading = state.isLoading
                        )
                    }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier
                            .size(2.dp)
                            .weight(1f)
                            .padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.auth_or_continue_with),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .size(2.dp)
                            .weight(1f)
                            .padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                    GoogleSignInButton(
                        onClick = {
                            googleLauncher.launch(googleClient.signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        enabled = !state.isLoading,
                        loading = state.isLoading,
                        text = "Войти через Google"
                    )

                PrimaryButton(
                    text = "Войти как гость",
                    onClick = {
                        focusManager.clearFocus()
                        store.accept(SignInStore.Intent.LoginAsGuest)
                    },
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    enabled = !state.isLoading,
                    loading = state.isLoading
                )

                // Sign up link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.auth_dont_have_account),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        TextButton(
                            onClick = {
                                store.accept(SignInStore.Intent.NavigateToSignUp)
                            },
                            enabled = !state.isLoading
                        ) {
                            Text(
                                text = stringResource(R.string.auth_sign_up),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
            }
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

        // Forgot password dialog
        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                onDismiss = { showForgotPasswordDialog = false },
                onResetPassword = { email ->
                    store.accept(SignInStore.Intent.ResetPasswordClicked(email))
                    showForgotPasswordDialog = false
                }
            )
        }

        // Snackbar for errors - positioned at the bottom
        AnimatedVisibility(
            visible = showError != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            SnackbarHost(
                hostState = remember { SnackbarHostState() }
            ) { data ->
                AuthErrorSnackbar(
                    errorMessage = showError,
                    onDismiss = { showError = null }
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
    initialOffsetY = { initialY.toInt() }
) + fadeInAnimation()

private fun scaleInAnimation() = scaleIn(
    animationSpec = tween(300),
    initialScale = 0.9f
)

private fun scaleOutAnimation() = scaleOut(
    animationSpec = tween(200),
    targetScale = 0.9f
)