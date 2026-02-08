package com.example.reptrack.data.auth

import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryImplTest {

    private lateinit var authRepositoryImpl: AuthRepositoryImpl
    private val firebaseAuthDataSource = mockk<FirebaseAuthDataSource>()

    @Before
    fun setUp() {
        authRepositoryImpl = AuthRepositoryImpl(firebaseAuthDataSource)
    }

    // ============ Sign Up Tests ============

    @Test
    fun `signUp with valid credentials returns success with AuthUser`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { this@mockk.email } returns email
            every { isAnonymous } returns false
        }
        coEvery { firebaseAuthDataSource.signUp(email, password) } returns mockFirebaseUser

        // Act
        val result = authRepositoryImpl.signUp(email, password)

        // Assert
        assertTrue(result.isSuccess)
        val authUser = result.getOrNull()
        assertEquals("user123", authUser?.id)
        assertEquals(email, authUser?.email)
        assertFalse(authUser?.isGuest ?: true)
    }

    @Test
    fun `signUp with invalid email throws exception`() = runTest {
        // Arrange
        val email = "invalid-email"
        val password = "password123"
        val exception = Exception("Invalid email format")
        coEvery { firebaseAuthDataSource.signUp(email, password) } throws exception

        // Act
        val result = authRepositoryImpl.signUp(email, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `signUp with weak password throws exception`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "123"
        val exception = Exception("Password is too weak")
        coEvery { firebaseAuthDataSource.signUp(email, password) } throws exception

        // Act
        val result = authRepositoryImpl.signUp(email, password)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `signUp with null user throws exception`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        coEvery { firebaseAuthDataSource.signUp(email, password) } throws 
            IllegalStateException("Sign up error - user is null")

        // Act
        val result = authRepositoryImpl.signUp(email, password)

        // Assert
        assertTrue(result.isFailure)
    }

    // ============ Sign In Tests ============

    @Test
    fun `signIn with valid credentials returns success with AuthUser`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { this@mockk.email } returns email
            every { isAnonymous } returns false
        }
        coEvery { firebaseAuthDataSource.signIn(email, password) } returns mockFirebaseUser

        // Act
        val result = authRepositoryImpl.signIn(email, password)

        // Assert
        assertTrue(result.isSuccess)
        val authUser = result.getOrNull()
        assertEquals("user123", authUser?.id)
        assertEquals(email, authUser?.email)
        assertFalse(authUser?.isGuest ?: true)
    }

    @Test
    fun `signIn with incorrect password throws exception`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "wrongPassword"
        val exception = Exception("Wrong password")
        coEvery { firebaseAuthDataSource.signIn(email, password) } throws exception

        // Act
        val result = authRepositoryImpl.signIn(email, password)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `signIn with non-existent email throws exception`() = runTest {
        // Arrange
        val email = "nonexistent@example.com"
        val password = "password123"
        val exception = Exception("User not found")
        coEvery { firebaseAuthDataSource.signIn(email, password) } throws exception

        // Act
        val result = authRepositoryImpl.signIn(email, password)

        // Assert
        assertTrue(result.isFailure)
    }

    // ============ Sign In As Guest Tests ============

    @Test
    fun `signInAsGuest returns success with guest AuthUser`() = runTest {
        // Arrange
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "guest123"
            every { email } returns null
            every { isAnonymous } returns true
        }
        coEvery { firebaseAuthDataSource.signInAsGuest() } returns mockFirebaseUser

        // Act
        val result = authRepositoryImpl.signInAsGuest()

        // Assert
        assertTrue(result.isSuccess)
        val authUser = result.getOrNull()
        assertEquals("guest123", authUser?.id)
        assertNull(authUser?.email)
        assertTrue(authUser?.isGuest ?: false)
    }

    @Test
    fun `signInAsGuest failure returns failed result`() = runTest {
        // Arrange
        val exception = Exception("Guest sign in failed")
        coEvery { firebaseAuthDataSource.signInAsGuest() } throws exception

        // Act
        val result = authRepositoryImpl.signInAsGuest()

        // Assert
        assertTrue(result.isFailure)
    }

    // ============ Get Current User Tests ============

    @Test
    fun `getCurrentUser returns AuthUser when user is logged in`() {
        // Arrange
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { email } returns "test@example.com"
            every { isAnonymous } returns false
        }
        every { firebaseAuthDataSource.getCurrentUser() } returns mockFirebaseUser

        // Act
        val authUser = authRepositoryImpl.getCurrentUser()

        // Assert
        assertEquals("user123", authUser?.id)
        assertEquals("test@example.com", authUser?.email)
        assertFalse(authUser?.isGuest ?: true)
    }

    @Test
    fun `getCurrentUser returns null when no user is logged in`() {
        // Arrange
        every { firebaseAuthDataSource.getCurrentUser() } returns null

        // Act
        val authUser = authRepositoryImpl.getCurrentUser()

        // Assert
        assertNull(authUser)
    }

    // ============ Reset Password Tests ============

    @Test
    fun `resetPassword with valid email returns success`() = runTest {
        // Arrange
        val email = "test@example.com"
        coEvery { firebaseAuthDataSource.resetPassword(email) } returns Unit

        // Act
        val result = authRepositoryImpl.resetPassword(email)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { firebaseAuthDataSource.resetPassword(email) }
    }

    @Test
    fun `resetPassword with non-existent email throws exception`() = runTest {
        // Arrange
        val email = "nonexistent@example.com"
        val exception = Exception("User not found")
        coEvery { firebaseAuthDataSource.resetPassword(email) } throws exception

        // Act
        val result = authRepositoryImpl.resetPassword(email)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `resetPassword with invalid email throws exception`() = runTest {
        // Arrange
        val email = "invalid-email"
        val exception = Exception("Invalid email format")
        coEvery { firebaseAuthDataSource.resetPassword(email) } throws exception

        // Act
        val result = authRepositoryImpl.resetPassword(email)

        // Assert
        assertTrue(result.isFailure)
    }

    // ============ Sign In With Google Tests ============

    @Test
    fun `signInWithGoogle with valid token returns success with AuthUser`() = runTest {
        // Arrange
        val idToken = "valid_google_token"
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "google_user_123"
            every { email } returns "user@gmail.com"
            every { isAnonymous } returns false
        }
        coEvery { firebaseAuthDataSource.signInWithGoogle(idToken) } returns mockFirebaseUser

        // Act
        val result = authRepositoryImpl.signInWithGoogle(idToken)

        // Assert
        assertTrue(result.isSuccess)
        val authUser = result.getOrNull()
        assertEquals("google_user_123", authUser?.id)
        assertEquals("user@gmail.com", authUser?.email)
        assertFalse(authUser?.isGuest ?: true)
    }

    @Test
    fun `signInWithGoogle with invalid token throws exception`() = runTest {
        // Arrange
        val idToken = "invalid_token"
        val exception = Exception("Invalid token")
        coEvery { firebaseAuthDataSource.signInWithGoogle(idToken) } throws exception

        // Act
        val result = authRepositoryImpl.signInWithGoogle(idToken)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `signInWithGoogle with null user throws exception`() = runTest {
        // Arrange
        val idToken = "valid_google_token"
        coEvery { firebaseAuthDataSource.signInWithGoogle(idToken) } throws
            IllegalStateException("Sign in error - user is null")

        // Act
        val result = authRepositoryImpl.signInWithGoogle(idToken)

        // Assert
        assertTrue(result.isFailure)
    }
}
