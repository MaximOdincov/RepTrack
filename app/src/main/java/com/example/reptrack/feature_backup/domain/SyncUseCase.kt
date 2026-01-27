package com.example.reptrack.feature_backup.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.feature_backup.data.BackupRepository
import com.example.reptrack.feature_backup.data.SyncPreferences

class SyncUseCase(
    private val backupRepository: BackupRepository,
    private val syncPreferences: SyncPreferences
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(userId: String): Boolean {
        return try {
            backupRepository.syncForUser(userId)
            syncPreferences.saveLastSync(System.currentTimeMillis())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getLastSyncTime(): Long = syncPreferences.getLastSync()
}
