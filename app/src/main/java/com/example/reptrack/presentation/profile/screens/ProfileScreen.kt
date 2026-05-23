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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.profile.User
import com.example.reptrack.presentation.profile.components.FriendsSection
import com.example.reptrack.presentation.profile.components.ProfileHeader
import com.example.reptrack.presentation.profile.components.StatisticsSection
import com.example.reptrack.presentation.profile.stores.FriendsStore
import com.example.reptrack.presentation.profile.stores.ProfileStore

@Composable
fun ProfileScreen(
    store: ProfileStore,
    friendsStore: FriendsStore,
    statisticsStore: com.example.reptrack.presentation.statistics.stores.StatisticsStore,
    onSignedOut: () -> Unit = {},
    onNavigateToCrashlyticsTest: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {}
) {
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                ProfileStore.Label.SignedOut -> {
                    onSignedOut()
                }
                is ProfileStore.Label.Error -> {
                }
                ProfileStore.Label.SyncCompleted -> {
                    // Sync completed, could show a toast or snackbar
                }
                is ProfileStore.Label.SyncError -> {
                    // Sync error, could show a toast or snackbar
                }
            }
        }
    }

    val state = store.states.collectAsState(ProfileStore.State())

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
                    isSyncing = state.value.isSyncing,
                    syncError = state.value.syncError,
                    lastSyncTime = state.value.lastSyncTime,
                    friendsStore = friendsStore,
                    statisticsStore = statisticsStore,
                    onSignOut = { store.accept(ProfileStore.Intent.SignOut) },
                    onSync = { store.accept(ProfileStore.Intent.SyncData) },
                    onNavigateToCrashlyticsTest = onNavigateToCrashlyticsTest,
                    onNavigateToStatistics = onNavigateToStatistics
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    isLoggingOut: Boolean,
    isSyncing: Boolean,
    syncError: String?,
    lastSyncTime: Long,
    friendsStore: FriendsStore,
    statisticsStore: com.example.reptrack.presentation.statistics.stores.StatisticsStore,
    onSignOut: () -> Unit,
    onSync: () -> Unit,
    onNavigateToCrashlyticsTest: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Header with Avatar
        ProfileHeader(
            username = user.username,
            email = user.email,
            avatarUrl = user.avatarUrl,
            isSyncing = isSyncing,
            syncError = syncError,
            lastSyncTime = lastSyncTime,
            onAvatarClick = {
                // TODO: Implement avatar selection
            },
            onSettingsClick = {
                // TODO: Implement settings
            },
            onSyncClick = onSync
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Friends Section
        FriendsSection(
            store = friendsStore
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Statistics Section
        StatisticsSection(
            store = statisticsStore,
            onNavigateToStatistics = onNavigateToStatistics
        )

        Spacer(modifier = Modifier.height(24.dp))

        // User Info
        Text(
            text = "Account Info",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

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
