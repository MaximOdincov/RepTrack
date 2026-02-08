package com.example.reptrack.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.mockk
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.test.fail

class FirebaseAuthDataSourceTest {

    private lateinit var firebaseAuthDataSource: FirebaseAuthDataSource
    private val firebaseAuth = mockk<FirebaseAuth>()

    @Before
    fun setUp() {
        firebaseAuthDataSource = FirebaseAuthDataSource(firebaseAuth)
    }

    // ============ Sign Up Tests ============

    @Test
    fun `signUp with valid credentials returns FirebaseUser`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { this@mockk.email } returns email
        }
        val authResult = mockk<AuthResult> {
            every { user } returns mockFirebaseUser
        }
        every { firebaseAuth.createUserWithEmailAndPassword(email, password) } returns Tasks.forResult(authResult)

        val result = firebaseAuthDataSource.signUp(email, password)

        assertEquals("user123", result.uid)
        assertEquals(email, result.email)
    }

    @Test
    fun `signUp with invalid credentials throws exception`() = runTest {
        val email = "invalid-email"
        val password = "123"
        val exception = Exception("Invalid email or weak password")
        every { firebaseAuth.createUserWithEmailAndPassword(email, password) } returns Tasks.forException(exception)

        try {
            firebaseAuthDataSource.signUp(email, password)
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertTrue(e.message?.contains("Invalid email") ?: true)
        }
    }

    @Test
    fun `signUp with null user throws IllegalStateException`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val authResult = mockk<AuthResult> {
            every { user } returns null
        }
        every { firebaseAuth.createUserWithEmailAndPassword(email, password) } returns Tasks.forResult(authResult)

        try {
            firebaseAuthDataSource.signUp(email, password)
            fail("Should have thrown IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("Sign up error - user is null", e.message)
        }
    }

    // ============ Sign In Tests ============

    @Test
    fun `signIn with valid credentials returns FirebaseUser`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { this@mockk.email } returns email
        }
        val authResult = mockk<AuthResult> {
            every { user } returns mockFirebaseUser
        }
        every { firebaseAuth.signInWithEmailAndPassword(email, password) } returns Tasks.forResult(authResult)

        val result = firebaseAuthDataSource.signIn(email, password)

        assertEquals("user123", result.uid)
        assertEquals(email, result.email)
    }

    @Test
    fun `signIn with wrong password throws exception`() = runTest {
        val email = "test@example.com"
        val password = "wrongPassword"
        val exception = Exception("Wrong password")
        every { firebaseAuth.signInWithEmailAndPassword(email, password) } returns Tasks.forException(exception)

        try {
            firebaseAuthDataSource.signIn(email, password)
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertEquals("Wrong password", e.message)
        }
    }

    @Test
    fun `signIn with non-existent email throws exception`() = runTest {
        val email = "nonexistent@example.com"
        val password = "password123"
        val exception = Exception("User not found")
        every { firebaseAuth.signInWithEmailAndPassword(email, password) } returns Tasks.forException(exception)

        try {
            firebaseAuthDataSource.signIn(email, password)
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertEquals("User not found", e.message)
        }
    }

    @Test
    fun `signIn with null user throws IllegalStateException`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val authResult = mockk<AuthResult> {
            every { user } returns null
        }
        every { firebaseAuth.signInWithEmailAndPassword(email, password) } returns Tasks.forResult(authResult)

        try {
            firebaseAuthDataSource.signIn(email, password)
            fail("Should have thrown IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("Sign in error - user is null", e.message)
        }
    }

    // ============ Sign In As Guest Tests ============

    @Test
    fun `signInAsGuest returns anonymous FirebaseUser`() = runTest {
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "guest_user_123"
            every { isAnonymous } returns true
        }
        val authResult = mockk<AuthResult> {
            every { user } returns mockFirebaseUser
        }
        every { firebaseAuth.signInAnonymously() } returns Tasks.forResult(authResult)

        val result = firebaseAuthDataSource.signInAsGuest()

        assertEquals("guest_user_123", result.uid)
        assertTrue(result.isAnonymous)
    }

    @Test
    fun `signInAsGuest with null user throws IllegalStateException`() = runTest {
        val authResult = mockk<AuthResult> {
            every { user } returns null
        }
        every { firebaseAuth.signInAnonymously() } returns Tasks.forResult(authResult)

        try {
            firebaseAuthDataSource.signInAsGuest()
            fail("Should have thrown IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("Sign In Anonymously error - user is null", e.message)
        }
    }

    @Test
    fun `signInAsGuest failure throws exception`() = runTest {
        val exception = Exception("Anonymous sign in failed")
        every { firebaseAuth.signInAnonymously() } returns Tasks.forException(exception)

        try {
            firebaseAuthDataSource.signInAsGuest()
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertEquals("Anonymous sign in failed", e.message)
        }
    }

    // ============ Reset Password Tests ============

    @Test
    fun `resetPassword with valid email succeeds`() = runTest {
        val email = "test@example.com"
        every { firebaseAuth.sendPasswordResetEmail(email) } returns Tasks.forResult<Void>(null)

        firebaseAuthDataSource.resetPassword(email)
    }

    @Test
    fun `resetPassword with non-existent email throws exception`() = runTest {
        val email = "nonexistent@example.com"
        val exception = Exception("User not found")
        every { firebaseAuth.sendPasswordResetEmail(email) } returns Tasks.forException<Void>(exception)

        try {
            firebaseAuthDataSource.resetPassword(email)
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertEquals("User not found", e.message)
        }
    }

    // ============ Get Current User Tests ============

    @Test
    fun `getCurrentUser returns FirebaseUser when logged in`() {
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { email } returns "test@example.com"
        }
        every { firebaseAuth.currentUser } returns mockFirebaseUser

        val result = firebaseAuthDataSource.getCurrentUser()

        assertEquals("user123", result?.uid)
        assertEquals("test@example.com", result?.email)
    }

    @Test
    fun `getCurrentUser returns null when not logged in`() {
        every { firebaseAuth.currentUser } returns null

        val result = firebaseAuthDataSource.getCurrentUser()

        assertEquals(null, result)
    }

    // ============ Sign In With Google Tests ============

    @Test
    fun `signInWithGoogle with valid token returns FirebaseUser`() = runTest {
        val idToken = "valid_google_token"
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "google_user_123"
            every { email } returns "user@gmail.com"
        }
        val authResult = mockk<AuthResult> {
            every { user } returns mockFirebaseUser
        }
        
        mockkStatic(GoogleAuthProvider::class)
        val mockCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential(idToken, null) } returns mockCredential
        every { firebaseAuth.signInWithCredential(mockCredential) } returns Tasks.forResult(authResult)

        val result = firebaseAuthDataSource.signInWithGoogle(idToken)

        assertEquals("google_user_123", result.uid)
        assertEquals("user@gmail.com", result.email)
    }

    @Test
    fun `signInWithGoogle with invalid token throws exception`() = runTest {
        val idToken = "invalid_token"
        
        mockkStatic(GoogleAuthProvider::class)
        val mockCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential(idToken, null) } returns mockCredential
        
        val exception = Exception("Invalid token")
        every { firebaseAuth.signInWithCredential(mockCredential) } returns Tasks.forException(exception)

        try {
            firebaseAuthDataSource.signInWithGoogle(idToken)
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertEquals("Invalid token", e.message)
        }
    }

    @Test
    fun `signInWithGoogle with null user throws IllegalStateException`() = runTest {
        val idToken = "valid_google_token"

        mockkStatic(GoogleAuthProvider::class)
        val mockCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential(idToken, null) } returns mockCredential

        val authResult = mockk<AuthResult> {
            every { user } returns null
        }
        every { firebaseAuth.signInWithCredential(mockCredential) } returns Tasks.forResult(authResult)

        try {
            firebaseAuthDataSource.signInWithGoogle(idToken)
            fail("Should have thrown IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("Sign in error - user is null", e.message)
        }
    }
}