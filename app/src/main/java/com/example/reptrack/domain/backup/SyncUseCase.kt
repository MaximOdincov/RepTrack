package com.example.reptrack.domain.backup

import com.example.reptrack.data.backup.BackupRepository
import com.example.reptrack.data.backup.SyncPreferences

class SyncUseCase(
    private val backupRepository: BackupRepository,
    private val syncPreferences: SyncPreferences
) {
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
