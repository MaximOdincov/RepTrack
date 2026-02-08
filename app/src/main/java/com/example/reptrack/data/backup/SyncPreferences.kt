package com.example.reptrack.data.backup

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SyncPreferences(context: Context) {

    private val prefs: SharedPreferences = 
        context.getSharedPreferences("backup_sync_prefs", Context.MODE_PRIVATE)

    fun saveLastSync(timestampMs: Long) {
        prefs.edit { putLong(KEY_LAST_SYNC, timestampMs) }
    }

    fun getLastSync(): Long = prefs.getLong(KEY_LAST_SYNC, 0L)

    fun saveSyncInterval(intervalMs: Long) {
        prefs.edit { putLong(KEY_SYNC_INTERVAL, intervalMs) }
    }

    fun getSyncInterval(): Long = prefs.getLong(KEY_SYNC_INTERVAL, DEFAULT_SYNC_INTERVAL_MS)

    companion object {
        private const val KEY_LAST_SYNC = "last_successful_sync_timestamp"
        private const val KEY_SYNC_INTERVAL = "sync_interval_ms"
        private const val DEFAULT_SYNC_INTERVAL_MS = 5 * 60 * 1000L
    }
}
