package com.example.reptrack.data.backup

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SyncPreferencesTest {

    private lateinit var syncPreferences: SyncPreferences
    private val mockContext = mockk<Context>()
    private val mockSharedPreferences = mockk<SharedPreferences>(relaxed = true)
    private val mockEditor = mockk<SharedPreferences.Editor>(relaxed = true)

    @Before
    fun setUp() {
        every { mockContext.getSharedPreferences("backup_sync_prefs", Context.MODE_PRIVATE) }
            .returns(mockSharedPreferences)
        every { mockSharedPreferences.edit() }.returns(mockEditor)

        syncPreferences = SyncPreferences(mockContext)
    }

    // ============ Save Last Sync Tests ============

    @Test
    fun `saveLastSync stores timestamp in SharedPreferences`() {
        // Arrange
        val timestamp = System.currentTimeMillis()
        every { mockEditor.putLong("last_successful_sync_timestamp", timestamp) }
            .returns(mockEditor)

        // Act
        syncPreferences.saveLastSync(timestamp)

        // Assert
        verify { mockEditor.putLong("last_successful_sync_timestamp", timestamp) }
    }

    @Test
    fun `saveLastSync with zero timestamp`() {
        // Arrange
        val timestamp = 0L
        every { mockEditor.putLong("last_successful_sync_timestamp", timestamp) }
            .returns(mockEditor)

        // Act
        syncPreferences.saveLastSync(timestamp)

        // Assert
        verify { mockEditor.putLong("last_successful_sync_timestamp", timestamp) }
    }

    @Test
    fun `saveLastSync with large timestamp`() {
        // Arrange
        val timestamp = Long.MAX_VALUE
        every { mockEditor.putLong("last_successful_sync_timestamp", timestamp) }
            .returns(mockEditor)

        // Act
        syncPreferences.saveLastSync(timestamp)

        // Assert
        verify { mockEditor.putLong("last_successful_sync_timestamp", timestamp) }
    }

    // ============ Get Last Sync Tests ============

    @Test
    fun `getLastSync returns saved timestamp`() {
        // Arrange
        val expectedTimestamp = 1234567890L
        every { mockSharedPreferences.getLong("last_successful_sync_timestamp", 0L) }
            .returns(expectedTimestamp)

        // Act
        val result = syncPreferences.getLastSync()

        // Assert
        assertEquals(expectedTimestamp, result)
    }

    @Test
    fun `getLastSync returns default value when nothing is saved`() {
        // Arrange
        every { mockSharedPreferences.getLong("last_successful_sync_timestamp", 0L) }
            .returns(0L)

        // Act
        val result = syncPreferences.getLastSync()

        // Assert
        assertEquals(0L, result)
    }

    @Test
    fun `getLastSync returns zero as default`() {
        // Arrange
        every { mockSharedPreferences.getLong("last_successful_sync_timestamp", 0L) }
            .returns(0L)

        // Act
        val result = syncPreferences.getLastSync()

        // Assert
        assertEquals(0L, result)
    }

    // ============ Save Sync Interval Tests ============

    @Test
    fun `saveSyncInterval stores interval in SharedPreferences`() {
        // Arrange
        val interval = 10 * 60 * 1000L // 10 minutes
        every { mockEditor.putLong("sync_interval_ms", interval) }
            .returns(mockEditor)

        // Act
        syncPreferences.saveSyncInterval(interval)

        // Assert
        verify { mockEditor.putLong("sync_interval_ms", interval) }
    }

    @Test
    fun `saveSyncInterval with custom interval`() {
        // Arrange
        val interval = 30 * 60 * 1000L // 30 minutes
        every { mockEditor.putLong("sync_interval_ms", interval) }
            .returns(mockEditor)

        // Act
        syncPreferences.saveSyncInterval(interval)

        // Assert
        verify { mockEditor.putLong("sync_interval_ms", interval) }
    }

    @Test
    fun `saveSyncInterval with zero interval`() {
        // Arrange
        val interval = 0L
        every { mockEditor.putLong("sync_interval_ms", interval) }
            .returns(mockEditor)

        // Act
        syncPreferences.saveSyncInterval(interval)

        // Assert
        verify { mockEditor.putLong("sync_interval_ms", interval) }
    }

    // ============ Get Sync Interval Tests ============

    @Test
    fun `getSyncInterval returns saved interval`() {
        // Arrange
        val expectedInterval = 10 * 60 * 1000L
        every { mockSharedPreferences.getLong("sync_interval_ms", 5 * 60 * 1000L) }
            .returns(expectedInterval)

        // Act
        val result = syncPreferences.getSyncInterval()

        // Assert
        assertEquals(expectedInterval, result)
    }

    @Test
    fun `getSyncInterval returns default interval when nothing is saved`() {
        // Arrange
        val defaultInterval = 5 * 60 * 1000L
        every { mockSharedPreferences.getLong("sync_interval_ms", 5 * 60 * 1000L) }
            .returns(defaultInterval)

        // Act
        val result = syncPreferences.getSyncInterval()

        // Assert
        assertEquals(defaultInterval, result)
    }

    @Test
    fun `getSyncInterval default is 5 minutes`() {
        // Arrange
        every { mockSharedPreferences.getLong("sync_interval_ms", 5 * 60 * 1000L) }
            .returns(5 * 60 * 1000L)

        // Act
        val result = syncPreferences.getSyncInterval()

        // Assert
        assertEquals(5 * 60 * 1000L, result)
    }

    // ============ Integration Tests ============

    @Test
    fun `save and retrieve last sync timestamp`() {
        // Arrange
        val timestamp = System.currentTimeMillis()
        every { mockEditor.putLong("last_successful_sync_timestamp", timestamp) }
            .returns(mockEditor)
        every { mockSharedPreferences.getLong("last_successful_sync_timestamp", 0L) }
            .returns(timestamp)

        // Act
        syncPreferences.saveLastSync(timestamp)
        val result = syncPreferences.getLastSync()

        // Assert
        assertEquals(timestamp, result)
    }

    @Test
    fun `save and retrieve sync interval`() {
        // Arrange
        val interval = 15 * 60 * 1000L
        every { mockEditor.putLong("sync_interval_ms", interval) }
            .returns(mockEditor)
        every { mockSharedPreferences.getLong("sync_interval_ms", 5 * 60 * 1000L) }
            .returns(interval)

        // Act
        syncPreferences.saveSyncInterval(interval)
        val result = syncPreferences.getSyncInterval()

        // Assert
        assertEquals(interval, result)
    }

    @Test
    fun `context should use correct SharedPreferences name`() {
        // Assert
        verify { mockContext.getSharedPreferences("backup_sync_prefs", Context.MODE_PRIVATE) }
    }

    @Test
    fun `context should use MODE_PRIVATE`() {
        // Assert
        verify { mockContext.getSharedPreferences("backup_sync_prefs", Context.MODE_PRIVATE) }
    }
}
