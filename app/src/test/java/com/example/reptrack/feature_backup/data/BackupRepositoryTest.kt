package com.example.reptrack.feature_backup.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.core.data.local.dao.*
import com.example.reptrack.core.data.local.models.ExerciseDb
import com.example.reptrack.core.data.local.models.UserDb
import com.example.reptrack.core.data.local.models.WorkoutSessionDb
import com.example.reptrack.core.data.local.models.WorkoutExerciseDb
import com.example.reptrack.core.data.local.models.WorkoutSetDb
import com.example.reptrack.core.data.local.models.WorkoutTemplateDb
import com.example.reptrack.feature_backup.data.mapper.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BackupRepositoryTest {

    private lateinit var backupRepository: BackupRepository
    private val firebaseDataSource = mockk<FirebaseBackupDataSource>()
    private val exerciseDao = mockk<ExerciseDao>()
    private val workoutDao = mockk<WorkoutDao>()
    private val templateDao = mockk<WorkoutTemplateDao>()
    private val userDao = mockk<UserDao>()
    private val statisticDao = mockk<StatisticDao>()

    @Before
    fun setUp() {
        backupRepository = BackupRepository(
            firebaseDataSource,
            exerciseDao,
            workoutDao,
            templateDao,
            userDao,
            statisticDao
        )
    }

    // ============ Sync For User Tests ============

    @Test
    fun `syncForUser with valid userId executes all sync operations`() = runTest {
        // Arrange
        val userId = "user123"
        
        setupMockData()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { firebaseDataSource.listDocuments(userId, any()) }
    }

    @Test
    fun `syncForUser with different userId`() = runTest {
        // Arrange
        val userId = "different_user_456"
        
        setupMockData()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { firebaseDataSource.listDocuments(userId, any()) }
    }

    @Test
    fun `syncForUser syncs exercises`() = runTest {
        // Arrange
        val userId = "user123"
        setupMockData()
        coEvery { exerciseDao.getAllExercises() } returns emptyList()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { exerciseDao.getAllExercises() }
    }

    @Test
    fun `syncForUser syncs workout sessions`() = runTest {
        // Arrange
        val userId = "user123"
        setupMockData()
        coEvery { workoutDao.observeSessions(userId) } returns flowOf(emptyList())

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { workoutDao.observeSessions(userId) }
    }

    @Test
    fun `syncForUser syncs users`() = runTest {
        // Arrange
        val userId = "user123"
        setupMockData()
        coEvery { userDao.getAllUsers() } returns emptyList()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { userDao.getAllUsers() }
    }

    @Test
    fun `syncForUser with firebase exception throws SyncException`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Firebase error")
        coEvery { firebaseDataSource.listDocuments(userId, any()) } throws exception
        coEvery { exerciseDao.getAllExercises() } returns emptyList()

        // Act & Assert
        val syncException = assertFailsWith<SyncException> {
            backupRepository.syncForUser(userId)
        }
        assertTrue(syncException.message?.contains(userId) == true)
    }

    @Test
    fun `syncForUser with dao exception throws SyncException`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Database error")
        coEvery { exerciseDao.getAllExercises() } throws exception

        // Act & Assert
        assertFailsWith<SyncException> {
            backupRepository.syncForUser(userId)
        }
    }

    @Test
    fun `syncForUser with empty userId`() = runTest {
        // Arrange
        val userId = ""
        setupMockData()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { firebaseDataSource.listDocuments(userId, any()) }
    }

    @Test
    fun `syncForUser with special characters in userId`() = runTest {
        // Arrange
        val userId = "user@example.com_123-456"
        setupMockData()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { firebaseDataSource.listDocuments(userId, any()) }
    }

    // ============ Sync Exercises Tests ============

    @Test
    fun `syncForUser calls exercise dao`() = runTest {
        // Arrange
        val userId = "user123"
        setupMockData()
        coEvery { exerciseDao.getAllExercises() } returns emptyList()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { exerciseDao.getAllExercises() }
    }

    @Test
    fun `syncForUser calls template dao`() = runTest {
        // Arrange
        val userId = "user123"
        setupMockData()
        coEvery { templateDao.getAllTemplates() } returns emptyList()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { templateDao.getAllTemplates() }
    }

    @Test
    fun `syncForUser calls statistics dao`() = runTest {
        // Arrange
        val userId = "user123"
        setupMockData()
        coEvery { statisticDao.getAllTemplates() } returns emptyList()
        coEvery { statisticDao.getAllFriendConfigs() } returns emptyList()
        coEvery { statisticDao.getAllExerciseLineConfigs() } returns emptyList()
        coEvery { statisticDao.getAllSetConfigs() } returns emptyList()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { statisticDao.getAllTemplates() }
    }

    // ============ Multiple Sync Tests ============

    @Test
    fun `multiple successive syncs succeed`() = runTest {
        // Arrange
        val userId = "user123"
        setupMockData()

        // Act
        backupRepository.syncForUser(userId)
        backupRepository.syncForUser(userId)
        backupRepository.syncForUser(userId)

        // Assert
        coVerify(atLeast = 3) { firebaseDataSource.listDocuments(userId, any()) }
    }

    @Test
    fun `sync different users independently`() = runTest {
        // Arrange
        val userId1 = "user1"
        val userId2 = "user2"
        setupMockData()

        // Act
        backupRepository.syncForUser(userId1)
        backupRepository.syncForUser(userId2)

        // Assert
        coVerify { firebaseDataSource.listDocuments(userId1, any()) }
        coVerify { firebaseDataSource.listDocuments(userId2, any()) }
    }

    // ============ Edge Cases ============

    @Test
    fun `syncForUser with very long userId`() = runTest {
        // Arrange
        val userId = "a".repeat(1000)
        setupMockData()

        // Act
        backupRepository.syncForUser(userId)

        // Assert
        coVerify { firebaseDataSource.listDocuments(userId, any()) }
    }

    @Test
    fun `syncForUser exception message contains userId`() = runTest {
        // Arrange
        val userId = "user_with_error"
        coEvery { exerciseDao.getAllExercises() } throws Exception("DB error")

        // Act & Assert
        val exception = assertFailsWith<SyncException> {
            backupRepository.syncForUser(userId)
        }
        assertTrue(exception.message?.contains(userId) == true)
        assertTrue(exception.message?.contains("Failed to sync") == true)
    }

    @Test
    fun `syncForUser exception preserves cause`() = runTest {
        // Arrange
        val userId = "user123"
        val cause = Exception("Original error")
        coEvery { exerciseDao.getAllExercises() } throws cause

        // Act & Assert
        val exception = assertFailsWith<SyncException> {
            backupRepository.syncForUser(userId)
        }
        assertTrue(exception.cause != null)
    }

    private fun setupMockData() {
        // Mock all DAO calls to return empty lists/flows
        coEvery { exerciseDao.getAllExercises() } returns emptyList()
        coEvery { workoutDao.observeSessions(any()) } returns flowOf(emptyList())
        coEvery { workoutDao.getAllExercises() } returns emptyList()
        coEvery { workoutDao.getAllSets() } returns emptyList()
        coEvery { templateDao.getAllTemplates() } returns emptyList()
        coEvery { templateDao.getAllTemplateExercises() } returns emptyList()
        coEvery { userDao.getAllUsers() } returns emptyList()
        coEvery { userDao.getAllConsents() } returns emptyList()
        coEvery { statisticDao.getAllTemplates() } returns emptyList()
        coEvery { statisticDao.getAllFriendConfigs() } returns emptyList()
        coEvery { statisticDao.getAllExerciseLineConfigs() } returns emptyList()
        coEvery { statisticDao.getAllSetConfigs() } returns emptyList()

        // Mock all firebase calls
        val mockQuerySnapshot = mockk<com.google.firebase.firestore.QuerySnapshot> {
            every { documents } returns emptyList()
        }
        coEvery { firebaseDataSource.listDocuments(any(), any()) } returns mockQuerySnapshot
        coEvery { firebaseDataSource.uploadDocument(any(), any(), any(), any()) } returns Unit
        
        // Mock DAO insert operations
        coEvery { exerciseDao.insert(any()) } returns Unit
        coEvery { workoutDao.insertSession(any()) } returns Unit
        coEvery { workoutDao.insertExercises(any()) } returns Unit
        coEvery { workoutDao.insertSets(any()) } returns Unit
        coEvery { templateDao.insertTemplate(any()) } returns Unit
        coEvery { templateDao.insertTemplateExercises(any()) } returns Unit
        coEvery { userDao.insertUser(any()) } returns Unit
        coEvery { userDao.insertConsent(any()) } returns Unit
        coEvery { statisticDao.insertTemplate(any()) } returns Unit
        coEvery { statisticDao.insertFriendConfigs(any()) } returns Unit
        coEvery { statisticDao.insertExerciseLineConfigs(any()) } returns Unit
        coEvery { statisticDao.insertSetConfigs(any()) } returns Unit
    }
}

class SyncExceptionTest {

    @Test
    fun `SyncException with message only`() {
        // Arrange
        val message = "Test error message"

        // Act
        val exception = SyncException(message)

        // Assert
        assertTrue(exception.message == message)
        assertTrue(exception.cause == null)
    }

    @Test
    fun `SyncException with message and cause`() {
        // Arrange
        val message = "Test error message"
        val cause = Exception("Original error")

        // Act
        val exception = SyncException(message, cause)

        // Assert
        assertTrue(exception.message == message)
        assertTrue(exception.cause == cause)
    }

    @Test
    fun `SyncException message contains userId`() {
        // Arrange
        val userId = "user123"
        val message = "Failed to sync data for user $userId"

        // Act
        val exception = SyncException(message)

        // Assert
        assertTrue(exception.message?.contains(userId) == true)
    }
}
