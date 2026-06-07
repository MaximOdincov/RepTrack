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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.reptrack.presentation.profile.components.SettingsSection
import com.example.reptrack.presentation.profile.components.StatisticsSection
import com.example.reptrack.presentation.profile.stores.FriendsStore
import com.example.reptrack.presentation.profile.stores.ProfileStore

@Composable
fun ProfileScreen(
    store: ProfileStore,
    friendsStore: FriendsStore,
    statisticsStore: com.example.reptrack.presentation.statistics.stores.StatisticsStore,
    onSignedOut: () -> Unit = {},
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
            state.value.isLoggingOut -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Выход...")
                }
            }
            state.value.isLoading -> {
                android.util.Log.d("ProfileScreen", "Showing loading")
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Загрузка профиля...")
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
                        Text("Повторить")
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
                    store = store,
                    onSignOut = { store.accept(ProfileStore.Intent.SignOut) },
                    onSync = { store.accept(ProfileStore.Intent.SyncData) },
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
    store: ProfileStore,
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
        // Error message if any
        if (syncError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ошибка синхронизации: $syncError",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    IconButton(
                        onClick = { store.accept(ProfileStore.Intent.Retry) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Повторить",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        // Profile Header with Avatar
        ProfileHeader(
            username = user.username,
            email = user.email,
            avatarUrl = user.avatarUrl,
            isGuest = user.isGuest,
            isSyncing = isSyncing,
            syncError = syncError,
            lastSyncTime = lastSyncTime,
            onAvatarClick = {
                // TODO: Implement avatar selection
            },
            onSyncClick = onSync
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Friends Section - only show if not guest
        if (!user.isGuest) {
            FriendsSection(
                store = friendsStore
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Statistics Section - show for all users
        StatisticsSection(
            store = statisticsStore,
            onNavigateToStatistics = onNavigateToStatistics,
            isGuest = user.isGuest
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Settings Section
        SettingsSection(
            user = user,
            isGuest = user.isGuest,
            onSignOut = onSignOut,
            onChangeUsername = { newUsername ->
                store.accept(ProfileStore.Intent.UpdateUsername(newUsername, user.id))
            },
            onChangePasskey = { newPasskey ->
                store.accept(ProfileStore.Intent.UpdatePasskey(newPasskey, user.id))
            }
        )

        Spacer(modifier = Modifier.weight(1f))
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
