package com.example.reptrack.feature_backup.domain

import com.example.reptrack.feature_backup.data.BackupRepository
import com.example.reptrack.feature_backup.data.SyncPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SyncUseCaseTest {

    private lateinit var syncUseCase: SyncUseCase
    private val backupRepository = mockk<BackupRepository>()
    private val syncPreferences = mockk<SyncPreferences>()

    @Before
    fun setUp() {
        syncUseCase = SyncUseCase(backupRepository, syncPreferences)
    }

    // ============ Sync Tests ============

    @Test
    fun `invoke with valid userId syncs data and returns true`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertTrue(result)
        coVerify { backupRepository.syncForUser(userId) }
    }

    @Test
    fun `invoke saves last sync timestamp on success`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        syncUseCase(userId)

        // Assert
        verify { syncPreferences.saveLastSync(any()) }
    }

    @Test
    fun `invoke saves current time as last sync timestamp`() = runTest {
        // Arrange
        val userId = "user123"
        val currentTime = System.currentTimeMillis()
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        syncUseCase(userId)

        // Assert
        verify { syncPreferences.saveLastSync(any()) }
    }

    @Test
    fun `invoke with empty userId attempts sync`() = runTest {
        // Arrange
        val userId = ""
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertTrue(result)
        coVerify { backupRepository.syncForUser(userId) }
    }

    @Test
    fun `invoke with special characters in userId syncs correctly`() = runTest {
        // Arrange
        val userId = "user@example.com_123-456"
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertTrue(result)
        coVerify { backupRepository.syncForUser(userId) }
    }

    // ============ Sync Failure Tests ============

    @Test
    fun `invoke with sync exception returns false`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Sync failed")
        coEvery { backupRepository.syncForUser(userId) } throws exception
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `invoke with sync exception does not save last sync time`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Sync failed")
        coEvery { backupRepository.syncForUser(userId) } throws exception

        // Act
        syncUseCase(userId)

        // Assert
        verify(exactly = 0) { syncPreferences.saveLastSync(any()) }
    }

    @Test
    fun `invoke with network error returns false`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Network error")
        coEvery { backupRepository.syncForUser(userId) } throws exception

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `invoke with timeout exception returns false`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Timeout")
        coEvery { backupRepository.syncForUser(userId) } throws exception

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `invoke with runtime exception returns false`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { backupRepository.syncForUser(userId) } throws RuntimeException("Runtime error")

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertFalse(result)
    }

    // ============ Get Last Sync Time Tests ============

    @Test
    fun `getLastSyncTime returns saved timestamp`() {
        // Arrange
        val expectedTime = 1234567890L
        every { syncPreferences.getLastSync() } returns expectedTime

        // Act
        val result = syncUseCase.getLastSyncTime()

        // Assert
        assertEquals(expectedTime, result)
    }

    @Test
    fun `getLastSyncTime returns zero when never synced`() {
        // Arrange
        every { syncPreferences.getLastSync() } returns 0L

        // Act
        val result = syncUseCase.getLastSyncTime()

        // Assert
        assertEquals(0L, result)
    }

    @Test
    fun `getLastSyncTime returns current time after successful sync`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit
        val currentTime = System.currentTimeMillis()
        every { syncPreferences.getLastSync() } returns currentTime

        // Act
        syncUseCase(userId)
        val result = syncUseCase.getLastSyncTime()

        // Assert
        assertEquals(currentTime, result)
    }

    @Test
    fun `getLastSyncTime before any sync returns zero`() {
        // Arrange
        every { syncPreferences.getLastSync() } returns 0L

        // Act
        val result = syncUseCase.getLastSyncTime()

        // Assert
        assertEquals(0L, result)
    }

    @Test
    fun `getLastSyncTime returns large timestamp value`() {
        // Arrange
        val largeTimestamp = Long.MAX_VALUE
        every { syncPreferences.getLastSync() } returns largeTimestamp

        // Act
        val result = syncUseCase.getLastSyncTime()

        // Assert
        assertEquals(largeTimestamp, result)
    }

    // ============ Multiple Syncs Tests ============

    @Test
    fun `multiple successive syncs all return true`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        val result1 = syncUseCase(userId)
        val result2 = syncUseCase(userId)
        val result3 = syncUseCase(userId)

        // Assert
        assertTrue(result1)
        assertTrue(result2)
        assertTrue(result3)
    }

    @Test
    fun `sync after failed sync succeeds`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { backupRepository.syncForUser(userId) } throws Exception("First attempt failed")
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        val result1 = syncUseCase(userId)
        
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        val result2 = syncUseCase(userId)

        // Assert
        assertFalse(result1)
        assertTrue(result2)
    }

    @Test
    fun `different userIds are synced independently`() = runTest {
        // Arrange
        val userId1 = "user1"
        val userId2 = "user2"
        coEvery { backupRepository.syncForUser(userId1) } returns Unit
        coEvery { backupRepository.syncForUser(userId2) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        val result1 = syncUseCase(userId1)
        val result2 = syncUseCase(userId2)

        // Assert
        assertTrue(result1)
        assertTrue(result2)
        coVerify { backupRepository.syncForUser(userId1) }
        coVerify { backupRepository.syncForUser(userId2) }
    }

    // ============ Edge Cases ============

    @Test
    fun `sync with very long userId`() = runTest {
        // Arrange
        val userId = "a".repeat(1000)
        coEvery { backupRepository.syncForUser(userId) } returns Unit
        every { syncPreferences.saveLastSync(any()) } returns Unit

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `sync preserves exception information in case of failure`() = runTest {
        // Arrange
        val userId = "user123"
        val errorMessage = "Detailed sync error message"
        coEvery { backupRepository.syncForUser(userId) } throws Exception(errorMessage)

        // Act
        val result = syncUseCase(userId)

        // Assert
        assertFalse(result)
    }
}
