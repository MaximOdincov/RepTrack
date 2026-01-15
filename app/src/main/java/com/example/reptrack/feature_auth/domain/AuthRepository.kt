package com.example.reptrack.feature_auth.domain

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<AuthUser>

    suspend fun signIn(email: String, password: String): Result<AuthUser>

    suspend fun signInAsGuest(): Result<AuthUser>

    fun getCurrentUser(): AuthUser?

    suspend fun resetPassword(email: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
}