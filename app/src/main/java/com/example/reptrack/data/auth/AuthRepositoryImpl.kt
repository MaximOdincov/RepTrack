package com.example.reptrack.data.auth

import android.content.Context
import com.example.reptrack.data.local.AppDatabase
import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.auth.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource,
    private val context: Context
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
        val user = dataSource.getCurrentUser()?.toAuthUser()
        android.util.Log.d("AuthDB", "getCurrentUser: userId=${user?.id}")
        return user
    }

    override suspend fun signOut() {
        android.util.Log.e("AuthDB", "!!! signOut CALLED - DELETING DATABASE !!!")
        android.util.Log.e("AuthDB", "Stack trace:", Exception())
        return withContext(Dispatchers.IO) {
            val userId = getCurrentUser()?.id

            // Delete database first
            if (userId != null) {
                android.util.Log.e("AuthDB", "Deleting database for userId=$userId")
                AppDatabase.deleteUserDatabase(context, userId)
            }

            // Then sign out from Firebase
            dataSource.signOut()
        }
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