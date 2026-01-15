package com.example.reptrack.feature_auth.presentation.signIn

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
@Composable
fun SignInScreen(
    store: SignInStore,
    onAuthorized: () -> Unit,
    onOpenSignUp: () -> Unit
) {
    val state = store.states.collectAsState(SignInStore.State("","",false, null))
    val context = LocalContext.current

    // --- Labels (one-time events) ---
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                SignInStore.Label.Authorized -> onAuthorized()
                SignInStore.Label.OpenSignUp -> onOpenSignUp()
                is SignInStore.Label.Error -> {
                    Toast
                        .makeText(context, label.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    // --- Google Sign-In ---
    val googleClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(
                    context.getString(
                        com.example.reptrack.R.string.default_web_client_id
                    )
                )
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
                val token = account.idToken ?: return@rememberLauncherForActivityResult
                store.accept(SignInStore.Intent.GoogleSignedIn(token))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Sign In", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = state.value.email,
                onValueChange = {
                    store.accept(SignInStore.Intent.EmailChanged(it))
                },
                label = { Text("Email") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.value.password,
                onValueChange = {
                    store.accept(SignInStore.Intent.PasswordChanged(it))
                },
                label = { Text("Password") },
                singleLine = true
            )

            Button(
                onClick = { store.accept(SignInStore.Intent.SignInClicked) },
                enabled = !state.value.isLoading
            ) {
                Text("Sign in")
            }

            Button(
                onClick = { googleLauncher.launch(googleClient.signInIntent) },
                enabled = !state.value.isLoading
            ) {
                Text("Sign in with Google")
            }

            Button(
                onClick = { store.accept(SignInStore.Intent.LoginAsGuest) },
                enabled = !state.value.isLoading
            ) {
                Text("Continue as Guest")
            }

            TextButton(
                onClick = { store.accept(SignInStore.Intent.ResetPasswordClicked) }
            ) {
                Text("Forgot password?")
            }

            TextButton(
                onClick = { store.accept(SignInStore.Intent.NavigateToSignUp) }
            ) {
                Text("Don't have an account? Sign up")
            }

            if (state.value.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}