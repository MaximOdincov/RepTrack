package com.example.reptrack.data.profile

import com.example.reptrack.data.auth.FirebaseUserDataSource
import com.example.reptrack.data.local.dao.UserDao
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.data.local.mappers.toGdprDb
import com.example.reptrack.domain.profile.User
import com.example.reptrack.domain.profile.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val userDao: UserDao,
    private val firebaseUserDataSource: FirebaseUserDataSource
): ProfileRepository {
    override suspend fun addUser(user: User) {
        val userDb = user.toDb()
        val consentDb = user.toGdprDb()

        userDao.insertFullUser(
            user = userDb,
            consent = consentDb
        )
    }

    override suspend fun deleteUser(userId: String) {
        userDao.deleteUser(userId)
    }

    override fun observeUser(userId: String): Flow<User?> {
        return userDao.observeUser(userId).map { it?.toDomain() }
    }

    override suspend fun updateUser(user: User) {
        val userDb = user.toDb()
        val consentDb = user.toGdprDb()

        // Use updateUser instead of insertFullUser to avoid cascade delete
        userDao.updateUser(userDb)
        consentDb?.let { userDao.insertConsent(it) }
    }

    override suspend fun updateUsername(username: String, userId: String) {
        userDao.updateUsername(username, userId)
        android.util.Log.d("ProfileRepository", "Username updated for user $userId")
    }

    override suspend fun updatePasskey(passkey: String, userId: String) {
        userDao.updatePasskey(passkey, userId)
        
        val user = userDao.observeUser(userId).firstOrNull()
        user?.let {
            val firebaseUser = it.toDomain()
            firebaseUserDataSource.saveUser(
                userId = userId,
                username = firebaseUser.username,
                email = firebaseUser.email,
                avatarUrl = firebaseUser.avatarUrl,
                passkey = passkey
            )
        }
        
        android.util.Log.d("ProfileRepository", "Passkey updated for user $userId")
    }

    override suspend fun updateFirebaseProfile(username: String?, email: String?, avatarUrl: String?, passkey: String?, userId: String) {
        firebaseUserDataSource.saveUser(userId, username, email, avatarUrl, passkey)
    }
}