package com.example.reptrack.service.timer

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerForegroundService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.example.reptrack.timer.START"
        const val ACTION_PAUSE = "com.example.reptrack.timer.PAUSE"
        const val ACTION_RESUME = "com.example.reptrack.timer.RESUME"
        const val ACTION_STOP = "com.example.reptrack.timer.STOP"
        const val EXTRA_DURATION = "duration"

        const val BROADCAST_TICK = "com.example.reptrack.timer.TICK"
        const val BROADCAST_COMPLETED = "com.example.reptrack.timer.COMPLETED"
        const val EXTRA_REMAINING = "remaining"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null
    private var remainingSeconds by mutableStateOf(0)
    private var totalDuration by mutableStateOf(0)
    private var isPaused by mutableStateOf(false)

    private lateinit var notificationManager: TimerNotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = TimerNotificationManager(this)
        notificationManager.createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getIntExtra(EXTRA_DURATION, 60)
                startTimer(duration)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(duration: Int) {
        totalDuration = duration
        remainingSeconds = duration
        isPaused = false

        // Start foreground service with notification
        val notification = notificationManager.createNotification(remainingSeconds, false)
        startForeground(NOTIFICATION_ID, notification)

        // Cancel existing timer job if any
        timerJob?.cancel()

        // Start new timer
        timerJob = serviceScope.launch {
            while (remainingSeconds > 0) {
                if (!isPaused) {
                    delay(1000)
                    remainingSeconds--
                    updateNotification()

                    // Send tick broadcast to UI
                    sendTickBroadcast()

                    if (remainingSeconds == 0) {
                        sendCompletedBroadcast()
                        stopSelf()
                    }
                } else {
                    delay(100) // Check frequently if paused
                }
            }
        }
    }

    private fun pauseTimer() {
        isPaused = true
        updateNotification()
    }

    private fun resumeTimer() {
        isPaused = false
        updateNotification()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification() {
        notificationManager.updateNotification(remainingSeconds, isPaused)
    }

    private fun sendTickBroadcast() {
        val intent = Intent(BROADCAST_TICK).apply {
            putExtra(EXTRA_REMAINING, remainingSeconds)
        }
        sendBroadcast(intent)
    }

    private fun sendCompletedBroadcast() {
        val intent = Intent(BROADCAST_COMPLETED)
        sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
    }
}