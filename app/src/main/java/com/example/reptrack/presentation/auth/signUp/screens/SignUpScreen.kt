package com.example.reptrack.presentation.auth.signUp
import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpScreen(
    store: SignUpStore,
    onAuthorized: () -> Unit
) {
    val context = LocalContext.current

    val state by store.states.collectAsState(
        initial = SignUpStore.State()
    )

    LaunchedEffect(store) {
        store.labels.collectLatest { label ->
            when (label) {
                SignUpStore.Label.Authorize -> onAuthorized()
                is SignUpStore.Label.Error -> {
                    Toast
                        .makeText(context, label.msg, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Sign Up", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    store.accept(SignUpStore.Intent.EmailChanged(it))
                },
                label = { Text("Email") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    store.accept(SignUpStore.Intent.PasswordChanged(it))
                },
                label = { Text("Password") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.username,
                onValueChange = {
                    store.accept(SignUpStore.Intent.UsernameChanged(it))
                },
                label = { Text("Username") },
                singleLine = true
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.privacyAccepted,
                    onCheckedChange = {
                        store.accept(
                            SignUpStore.Intent.PrivacyStatusChanged(it)
                        )
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text("I agree with Privacy Policy")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.dataConsent,
                    onCheckedChange = {
                        store.accept(
                            SignUpStore.Intent.DataConsentChanged(it)
                        )
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text("I agree to personal data processing")
            }

            Button(
                onClick = {
                    store.accept(SignUpStore.Intent.SignUpClicked)
                },
                enabled = state.privacyAccepted && !state.isLoading
            ) {
                Text("Create account")
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}
