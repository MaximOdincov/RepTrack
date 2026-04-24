package com.example.reptrack.data.profile

import com.example.reptrack.data.local.dao.UserDao
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.data.local.mappers.toDomain
import com.example.reptrack.data.local.mappers.toGdprDb
import com.example.reptrack.domain.profile.User
import com.example.reptrack.domain.profile.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val userDao: UserDao
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
}