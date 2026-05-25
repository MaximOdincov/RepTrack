package com.example.reptrack.presentation.timer.screens

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.presentation.timer.screens.components.TimerCircularProgress
import com.example.reptrack.presentation.timer.screens.components.TimerControls
import com.example.reptrack.presentation.timer.screens.components.TimePickerDialog
import com.example.reptrack.presentation.timer.stores.TimerStore
import com.example.reptrack.service.timer.TimerForegroundService

@Composable
fun TimerScreen(store: TimerStore) {
    val state by store.states.collectAsState(initial = TimerStore.State())
    val context = LocalContext.current

    // Handle labels
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                TimerStore.Label.TimerCompleted -> {
                    // Timer completed - sound is played in the store
                }
                else -> {}
            }
        }
    }

    // Permission state
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    // Request permission on first load
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Broadcast receiver for service updates
    val broadcastReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    TimerForegroundService.BROADCAST_TICK -> {
                        val remaining = intent.getIntExtra(TimerForegroundService.EXTRA_REMAINING, 0)
                        store.accept(TimerStore.Intent.Tick(remaining))
                    }
                    TimerForegroundService.BROADCAST_COMPLETED -> {
                        store.accept(TimerStore.Intent.Tick(0))
                    }
                }
            }
        }
    }

    // Register/unregister broadcast receiver
    DisposableEffect(Unit) {
        val filter = IntentFilter().apply {
            addAction(TimerForegroundService.BROADCAST_TICK)
            addAction(TimerForegroundService.BROADCAST_COMPLETED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        }
        onDispose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    // Time picker dialog state
    var showTimePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular progress with time inside
            TimerCircularProgress(
                progress = state.progress,
                isRunning = state.isRunning,
                formattedTime = state.formattedTime,
                modifier = Modifier.padding(32.dp),
                onClick = {
                    if (!state.isRunning && !state.isPaused) {
                        showTimePicker = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Controls
            TimerControls(
                isRunning = state.isRunning,
                isPaused = state.isPaused,
                onStart = {
                    store.accept(TimerStore.Intent.StartTimer)
                    startTimerService(context, state.durationSeconds)
                },
                onPause = {
                    store.accept(TimerStore.Intent.PauseTimer)
                    pauseTimerService(context)
                },
                onResume = {
                    store.accept(TimerStore.Intent.ResumeTimer)
                    resumeTimerService(context)
                },
                onReset = {
                    store.accept(TimerStore.Intent.ResetTimer)
                    stopTimerService(context)
                },
                onOpenTimePicker = {
                    showTimePicker = true
                }
            )
        }
    }

    // Time picker dialog
    if (showTimePicker) {
        TimePickerDialog(
            currentDurationSeconds = state.durationSeconds,
            onDismiss = { showTimePicker = false },
            onConfirm = { seconds ->
                store.accept(TimerStore.Intent.SetDuration(seconds))
                showTimePicker = false
            }
        )
    }
}

private fun startTimerService(context: Context, durationSeconds: Int) {
    val intent = Intent(context, TimerForegroundService::class.java).apply {
        action = TimerForegroundService.ACTION_START
        putExtra(TimerForegroundService.EXTRA_DURATION, durationSeconds)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

private fun pauseTimerService(context: Context) {
    val intent = Intent(context, TimerForegroundService::class.java).apply {
        action = TimerForegroundService.ACTION_PAUSE
    }
    context.startService(intent)
}

private fun resumeTimerService(context: Context) {
    val intent = Intent(context, TimerForegroundService::class.java).apply {
        action = TimerForegroundService.ACTION_RESUME
    }
    context.startService(intent)
}

private fun stopTimerService(context: Context) {
    val intent = Intent(context, TimerForegroundService::class.java).apply {
        action = TimerForegroundService.ACTION_STOP
    }
    context.startService(intent)
}