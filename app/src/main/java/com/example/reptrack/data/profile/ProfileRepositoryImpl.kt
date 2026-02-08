package com.example.reptrack.data.profile

import com.example.reptrack.data.local.dao.UserDao
import com.example.reptrack.data.local.mappers.toDb
import com.example.reptrack.data.local.mappers.toGdprDb
import com.example.reptrack.domain.workout.User
import com.example.reptrack.domain.profile.ProfileRepository

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