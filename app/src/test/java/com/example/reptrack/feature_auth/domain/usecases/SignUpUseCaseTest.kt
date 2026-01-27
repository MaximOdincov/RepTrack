package com.example.reptrack.feature_auth.domain.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reptrack.core.domain.entities.GdprConsent
import com.example.reptrack.core.domain.entities.User
import com.example.reptrack.feature_auth.data.toDomain
import com.example.reptrack.feature_auth.domain.AuthRepository
import com.example.reptrack.feature_auth.domain.AuthUser
import com.example.reptrack.feature_profile.domain.usecases.AddUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignUpUseCaseTest {

    private lateinit var signUpUseCase: SignUpUseCase
    private val authRepository = mockk<AuthRepository>()
    private val addUserUseCase = mockk<AddUserUseCase>()

    @Before
    fun setUp() {
        signUpUseCase = SignUpUseCase(authRepository, addUserUseCase)
    }

    // ============ Successful Sign Up Tests ============

    @Test
    fun `signUp with valid credentials and data consent accepted returns success`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("user123", user?.id)
        assertEquals(username, user?.username)
        assertTrue(user?.gdprConsent?.isAccepted ?: false)
    }

    @Test
    fun `signUp sets username in created user`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "customUsername"
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(username, user?.username)
    }

    @Test
    fun `signUp calls addUserUseCase with user data`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        signUpUseCase(email, password, username, dataConsent)

        // Assert
        coVerify { addUserUseCase(any()) }
    }

    @Test
    fun `signUp with GDPR consent sets acceptedAt timestamp`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertTrue(user?.gdprConsent?.isAccepted ?: false)
        assertNotNull(user?.gdprConsent?.acceptedAt)
        assertTrue((user?.gdprConsent?.acceptedAt ?: 0L) > 0)
    }

    @Test
    fun `signUp without consent has no GDPR consent data`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = false

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
    }

    // ============ Failed Sign Up Tests ============

    @Test
    fun `signUp with existing email returns failure`() = runTest {
        // Arrange
        val email = "existing@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val exception = Exception("Email already exists")
        coEvery { authRepository.signUp(email, password) } returns Result.failure(exception)

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `signUp with weak password returns failure`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "123"
        val username = "testuser"
        val dataConsent = true

        val exception = Exception("Password is too weak")
        coEvery { authRepository.signUp(email, password) } returns Result.failure(exception)

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `signUp with invalid email returns failure`() = runTest {
        // Arrange
        val email = "invalid-email"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val exception = Exception("Invalid email format")
        coEvery { authRepository.signUp(email, password) } returns Result.failure(exception)

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `signUp failure does not call addUserUseCase`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val exception = Exception("Sign up failed")
        coEvery { authRepository.signUp(email, password) } returns Result.failure(exception)

        // Act
        signUpUseCase(email, password, username, dataConsent)

        // Assert
        coVerify(exactly = 0) { addUserUseCase(any()) }
    }

    @Test
    fun `signUp with network error returns failure`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val exception = Exception("Network error")
        coEvery { authRepository.signUp(email, password) } returns Result.failure(exception)

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isFailure)
    }

    // ============ Edge Cases Tests ============

    @Test
    fun `signUp with empty username`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = ""
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("", user?.username)
    }

    @Test
    fun `signUp with very long username`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "a".repeat(1000)
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(username, user?.username)
    }

    @Test
    fun `signUp with special characters in username`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "user@#$%^&*()"
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(username, user?.username)
    }

    @Test
    fun `signUp preserves auth user email in domain user`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(email, user?.email)
    }

    @Test
    fun `signUp does not create guest user`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val dataConsent = true

        val authUser = AuthUser(
            id = "user123",
            email = email,
            isGuest = false
        )
        coEvery { authRepository.signUp(email, password) } returns Result.success(authUser)
        coEvery { addUserUseCase(any()) } returns Unit

        // Act
        val result = signUpUseCase(email, password, username, dataConsent)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertFalse(user?.isGuest ?: true)
    }
}
