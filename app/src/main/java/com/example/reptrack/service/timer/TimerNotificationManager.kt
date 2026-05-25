package com.example.reptrack.service.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.reptrack.R

class TimerNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_PAUSE = "com.example.reptrack.timer.PAUSE"
        const val ACTION_RESUME = "com.example.reptrack.timer.RESUME"
        const val ACTION_STOP = "com.example.reptrack.timer.STOP"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Timer notifications"
                setShowBadge(false)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(
        remainingSeconds: Int,
        isPaused: Boolean
    ): Notification {
        // Format time for display
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)

        // Create intent for notification tap (opens app)
        val notificationIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(if (isPaused) "Timer Paused" else "Timer Running")
            .setContentText("Time remaining: $timeText")
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setOngoing(!isPaused)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        // Add action buttons
        val actionIcon = if (isPaused) R.drawable.ic_play else R.drawable.ic_pause
        val actionText = if (isPaused) "Resume" else "Pause"
        val action = if (isPaused) ACTION_RESUME else ACTION_PAUSE

        builder.addAction(
            actionIcon,
            actionText,
            createPendingIntent(action)
        )

        builder.addAction(
            R.drawable.ic_stop,
            "Stop",
            createPendingIntent(ACTION_STOP)
        )

        return builder.build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            this.action = action
        }

        return PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun updateNotification(remainingSeconds: Int, isPaused: Boolean) {
        val notification = createNotification(remainingSeconds, isPaused)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}