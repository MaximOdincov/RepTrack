package com.example.reptrack.presentation.profile.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.profile.User
import com.example.reptrack.presentation.profile.components.FriendsSection
import com.example.reptrack.presentation.profile.components.GuestFeaturesCard
import com.example.reptrack.presentation.profile.components.ProfileHeader
import com.example.reptrack.presentation.profile.components.SettingsSection
import com.example.reptrack.presentation.profile.components.StatisticsSection
import com.example.reptrack.presentation.profile.stores.FriendsStore
import com.example.reptrack.presentation.profile.stores.ProfileStore

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    store: ProfileStore,
    friendsStore: FriendsStore,
    statisticsStore: com.example.reptrack.presentation.statistics.stores.StatisticsStore,
    onSignedOut: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {}
) {
    // Для управления клавиатурой
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

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
                    onSignOut = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        store.accept(ProfileStore.Intent.SignOut)
                    },
                    onSync = {
                        store.accept(ProfileStore.Intent.SyncData)
                    },
                    onNavigateToStatistics = onNavigateToStatistics,
                    onNavigateToSignIn = onNavigateToSignIn,
                    keyboardController = keyboardController,
                    focusManager = focusManager
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
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
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {},
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    // Сохраняем состояние полей ввода при перевороте экрана
    var usernameInput by rememberSaveable { mutableStateOf(user.username ?: "") }
    var passkeyInput by rememberSaveable { mutableStateOf("") }
    var isEditingUsername by rememberSaveable { mutableStateOf(false) }
    var isEditingPasskey by rememberSaveable { mutableStateOf(false) }

    // Для автоматического фокуса
    val usernameFocusRequester = remember { FocusRequester() }
    val passkeyFocusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // КЛЮЧЕВЫЕ МОДИФИКАТОРЫ ДЛЯ КЛАВИАТУРЫ:
            .systemBarsPadding()           // Отступ под статус-бар и навигацию
            .imePadding()                  // Отступ под клавиатуру (главный!)
            .navigationBarsPadding()       // Отступ под навигационные кнопки
            .verticalScroll(rememberScrollState())  // Скролл при открытии клавиатуры
            .padding(16.dp)
            // Закрытие клавиатуры при клике на пустое место
            .clickableWithoutRipple {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
    ) {
        // Profile Header with Avatar
        ProfileHeader(
            username = user.username ?: "",
            email = user.email ?: "",
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

        Spacer(modifier = Modifier.height(16.dp))

        if(user.isGuest){
            // Информация о пользователе с обрезкой текста
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Username row с обрезкой
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Имя пользователя:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = user.username ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email row с обрезкой
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Email:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = user.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }

                    // Статус гостя
                    if (user.isGuest) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Статус:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Гость",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }


        // Error message if any (синхронизация вынесена под информацию о пользователе)
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
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
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

        // Guest Features Card - only show for guests
        if (user.isGuest) {
            GuestFeaturesCard(
                onRegisterClick = onNavigateToSignIn
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

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

        // Settings Section с сохранением состояния и поддержкой клавиатуры
        SettingsSection(
            user = user,
            isGuest = user.isGuest,
            usernameInput = usernameInput,
            passkeyInput = passkeyInput,
            isEditingUsername = isEditingUsername,
            isEditingPasskey = isEditingPasskey,
            onUsernameInputChange = {
                usernameInput = it
            },
            onPasskeyInputChange = {
                passkeyInput = it
            },
            onEditingUsernameChange = {
                isEditingUsername = it
                if (it) {
                    // При открытии редактирования - показываем клавиатуру
                    usernameFocusRequester.requestFocus()
                } else {
                    // При закрытии - скрываем клавиатуру
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            },
            onEditingPasskeyChange = {
                isEditingPasskey = it
                if (it) {
                    passkeyFocusRequester.requestFocus()
                } else {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            },
            onSignOut = onSignOut,
            onChangeUsername = { newUsername ->
                store.accept(ProfileStore.Intent.UpdateUsername(newUsername, user.id))
                isEditingUsername = false
                usernameInput = newUsername
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            onChangePasskey = { newPasskey ->
                store.accept(ProfileStore.Intent.UpdatePasskey(newPasskey, user.id))
                isEditingPasskey = false
                passkeyInput = ""
                keyboardController?.hide()
                focusManager.clearFocus()
            },
        )

        Spacer(modifier = Modifier.height(16.dp)) // Небольшой отступ снизу
    }
}

// Расширение для клика без ripple эффекта
@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier {
    return this.composed {
        this.clickable(
            indication = null,
            interactionSource = remember{MutableInteractionSource()},
            onClick = onClick
        )
    }
}