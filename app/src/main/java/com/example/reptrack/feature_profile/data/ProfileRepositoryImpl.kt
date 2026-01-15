package com.example.reptrack.feature_profile.data

import com.example.reptrack.core.data.local.dao.UserDao
import com.example.reptrack.core.data.local.mappers.toDb
import com.example.reptrack.core.data.local.mappers.toGdprDb
import com.example.reptrack.core.domain.entities.User
import com.example.reptrack.feature_profile.domain.ProfileRepository

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
}