package com.example.reptrack.data.auth

import com.example.reptrack.domain.auth.AuthUser
import com.google.firebase.auth.FirebaseUser
import io.mockk.mockk
import io.mockk.every
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthMapperTest {

    @Test
    fun `firebaseUserToAuthUser maps uid to id`() {
        // Arrange
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { email } returns "test@example.com"
            every { isAnonymous } returns false
        }

        // Act
        val authUser = mockFirebaseUser.toAuthUser()

        // Assert
        assertEquals("user123", authUser.id)
    }

    @Test
    fun `firebaseUserToAuthUser maps email correctly`() {
        // Arrange
        val email = "test@example.com"
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { this@mockk.email } returns email
            every { isAnonymous } returns false
        }

        // Act
        val authUser = mockFirebaseUser.toAuthUser()

        // Assert
        assertEquals(email, authUser.email)
    }

    @Test
    fun `firebaseUserToAuthUser maps isAnonymous correctly for regular user`() {
        // Arrange
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { email } returns "test@example.com"
            every { isAnonymous } returns false
        }

        // Act
        val authUser = mockFirebaseUser.toAuthUser()

        // Assert
        assertFalse(authUser.isGuest)
    }

    @Test
    fun `firebaseUserToAuthUser maps isAnonymous correctly for guest user`() {
        // Arrange
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "guest123"
            every { email } returns null
            every { isAnonymous } returns true
        }

        // Act
        val authUser = mockFirebaseUser.toAuthUser()

        // Assert
        assertTrue(authUser.isGuest)
    }

    @Test
    fun `firebaseUserToAuthUser handles null email`() {
        // Arrange
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { email } returns null
            every { isAnonymous } returns true
        }

        // Act
        val authUser = mockFirebaseUser.toAuthUser()

        // Assert
        assertNull(authUser.email)
    }

    @Test
    fun `authUserToDomain converts all fields correctly`() {
        // Arrange
        val authUser = AuthUser(
            id = "user123",
            email = "test@example.com",
            isGuest = false
        )

        // Act
        val user = authUser.toDomain()

        // Assert
        assertEquals("user123", user.id)
        assertEquals("test@example.com", user.email)
        assertFalse(user.isGuest)
    }

    @Test
    fun `authUserToDomain converts guest user correctly`() {
        // Arrange
        val authUser = AuthUser(
            id = "guest123",
            email = null,
            isGuest = true
        )

        // Act
        val user = authUser.toDomain()

        // Assert
        assertEquals("guest123", user.id)
        assertNull(user.email)
        assertTrue(user.isGuest)
    }

    @Test
    fun `authUserToDomain creates independent User object`() {
        // Arrange
        val authUser = AuthUser(
            id = "user123",
            email = "test@example.com",
            isGuest = false
        )

        // Act
        val user1 = authUser.toDomain()
        val user2 = authUser.toDomain()

        // Assert
        assertEquals(user1, user2)
    }

    @Test
    fun `firebaseUser to authUser to domain creates correct User object`() {
        // Arrange
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { email } returns "test@example.com"
            every { isAnonymous } returns false
        }

        // Act
        val authUser = mockFirebaseUser.toAuthUser()
        val user = authUser.toDomain()

        // Assert
        assertEquals("user123", user.id)
        assertEquals("test@example.com", user.email)
        assertFalse(user.isGuest)
    }

    @Test
    fun `mapping preserves data for user with special characters in email`() {
        // Arrange
        val email = "test+tag@example.co.uk"
        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
            every { this@mockk.email } returns email
            every { isAnonymous } returns false
        }

        // Act
        val authUser = mockFirebaseUser.toAuthUser()
        val user = authUser.toDomain()

        // Assert
        assertEquals(email, user.email)
    }
}
