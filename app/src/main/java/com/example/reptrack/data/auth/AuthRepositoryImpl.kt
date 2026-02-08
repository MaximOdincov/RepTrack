package com.example.reptrack.data.auth

import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.auth.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource
): AuthRepository {

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<AuthUser> {
        return runCatching {
            withContext(Dispatchers.IO){
                dataSource.signUp(email, password).toAuthUser()
            }
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<AuthUser> {
        return runCatching {
            withContext(Dispatchers.IO){
                dataSource.signIn(email, password).toAuthUser()
            }
        }
    }

    override suspend fun signInAsGuest(): Result<AuthUser> {
        return runCatching {
            withContext(Dispatchers.IO) {
                dataSource.signInAsGuest().toAuthUser()
            }
        }
    }

    override fun getCurrentUser(): AuthUser? {
        return dataSource.getCurrentUser()?.toAuthUser()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return runCatching {
            withContext(Dispatchers.IO){
                dataSource.resetPassword(email)
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser>{
        return runCatching {
            withContext(Dispatchers.IO){
                dataSource.signInWithGoogle(idToken).toAuthUser()
            }
        }
    }
}