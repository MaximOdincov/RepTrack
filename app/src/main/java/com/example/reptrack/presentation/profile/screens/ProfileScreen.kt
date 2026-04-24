package com.example.reptrack.presentation.profile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.profile.User
import com.example.reptrack.presentation.profile.stores.ProfileStore

@Composable
fun ProfileScreen(
    store: ProfileStore,
    onSignedOut: () -> Unit = {},
    onNavigateToCrashlyticsTest: () -> Unit = {}
) {
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                ProfileStore.Label.SignedOut -> {
                    onSignedOut()
                }
                is ProfileStore.Label.Error -> {
                }
            }
        }
    }

    val state = store.states.collectAsState(ProfileStore.State())

    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                ProfileStore.Label.SignedOut -> {
                    onSignedOut()
                }
                is ProfileStore.Label.Error -> {
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        store.accept(ProfileStore.Intent.LoadProfile)
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            state.value.isLoading -> {
                android.util.Log.d("ProfileScreen", "Showing loading")
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading profile...")
                }
            }
            state.value.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${state.value.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { store.accept(ProfileStore.Intent.Retry) }) {
                        Text("Retry")
                    }
                }
            }
            state.value.user != null -> {
                android.util.Log.d("ProfileScreen", "Showing profile for user: ${state.value.user!!.id}")
                ProfileContent(
                    user = state.value.user!!,
                    isLoggingOut = state.value.isLoggingOut,
                    onSignOut = { store.accept(ProfileStore.Intent.SignOut) },
                    onNavigateToCrashlyticsTest = onNavigateToCrashlyticsTest
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    isLoggingOut: Boolean,
    onSignOut: () -> Unit,
    onNavigateToCrashlyticsTest: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        UserInfoRow(label = "Username", value = user.username ?: "Not set")
        UserInfoRow(label = "Email", value = user.email ?: "Not set")
        UserInfoRow(label = "Account Type", value = if (user.isGuest) "Guest" else "Registered")
        user.currentWeight?.let {
            UserInfoRow(label = "Weight", value = "$it kg")
        }
        user.height?.let {
            UserInfoRow(label = "Height", value = "$it cm")
        }

        if (user.gdprConsent != null) {
            UserInfoRow(
                label = "GDPR Consent",
                value = if (user.gdprConsent.isAccepted) "Accepted" else "Not Accepted"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateToCrashlyticsTest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Crashlytics")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSignOut,
            enabled = !isLoggingOut,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign Out")
            }
        }
    }
}

@Composable
private fun UserInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
