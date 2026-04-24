package com.example.reptrack.presentation.crashlytics_test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.Napier

@Composable
fun CrashlyticsTestScreen() {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Тестирование Crashlytics")

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { triggerNonFatalError() }) {
                Text("Non-Fatal Error")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { triggerFatalError() }) {
                Text("Fatal Error (Crash)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { logCustomError() }) {
                Text("Log Custom Error")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { logWithNapier() }) {
                Text("Log with Napier")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { sendMultipleNonFatal() }) {
                Text("Send 5 Non-Fatal Errors")
            }
        }
    }
}

private fun triggerNonFatalError() {
    try {
        Napier.i("Creating non-fatal exception...")
        throw RuntimeException("Это тестовая non-fatal ошибка для Crashlytics!")
    } catch (e: Exception) {
        val crashlytics = FirebaseCrashlytics.getInstance()

        // Включаем сбор
        crashlytics.setCrashlyticsCollectionEnabled(true)

        // Логируем перед отправкой
        crashlytics.log("About to send non-fatal exception")

        // Отправляем
        crashlytics.recordException(e)

        Napier.e("Non-fatal sent", e)
        android.util.Log.e("CrashlyticsTest", "Non-fatal error sent: ${e.message}")
    }
}

private fun triggerFatalError() {
    Napier.e("Triggering fatal error...")
    throw RuntimeException("Это тестовая FATAL ошибка для Crashlytics!")
}

private fun logCustomError() {
    val crashlytics = FirebaseCrashlytics.getInstance()

    // Проверяем, что Crashlytics включен
    crashlytics.setUserId("test_user_123")
    crashlytics.setCustomKey("screen", "CrashlyticsTestScreen")
    crashlytics.setCustomKey("action", "custom_error_test")

    try {
        throw IllegalStateException("Custom error with key-value pairs")
    } catch (e: Exception) {
        crashlytics.recordException(e)
        Napier.e("Custom error logged with keys", throwable = e)
        android.util.Log.d("CrashlyticsTest", "Custom error with keys sent")
    }
}

private fun logWithNapier() {
    Napier.d("Debug message from Napier")
    Napier.i("Info message from Napier")
    Napier.w("Warning message from Napier")
    Napier.e("Error message from Napier", tag = "CrashlyticsTest")

    try {
        throw Exception("Test exception for Napier logging")
    } catch (e: Exception) {
        Napier.e("Exception logged via Napier", throwable = e)

        // Also send to Crashlytics
        FirebaseCrashlytics.getInstance().recordException(e)
    }
}

private fun sendMultipleNonFatal() {
    val crashlytics = FirebaseCrashlytics.getInstance()
    crashlytics.setCrashlyticsCollectionEnabled(true)

    repeat(5) { index ->
        try {
            throw RuntimeException("Non-fatal error #${index + 1} sent at ${System.currentTimeMillis()}")
        } catch (e: Exception) {
            crashlytics.recordException(e)
            Napier.e("Sent non-fatal error #${index + 1}")
            android.util.Log.e("CrashlyticsTest", "Non-fatal #${index + 1} sent")
        }
    }
}
