package com.example.reptrack.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reptrack.core.data.local.models.GdprConsentDb
import com.example.reptrack.core.data.local.models.UserDb
import com.example.reptrack.core.data.local.aggregates.UserWithConsent
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<UserWithConsent?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsent(consent: GdprConsentDb)

    @Transaction
    suspend fun insertFullUser(
        user: UserDb,
        consent: GdprConsentDb?
    ) {
        insertUser(user)
        consent?.let { insertConsent(it) }
    }

    @Query("UPDATE users SET deletedAt = (strftime('%s','now') * 1000), updatedAt = (strftime('%s','now') * 1000) WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserDb>

    @Query("SELECT * FROM gdpr_consent")
    suspend fun getAllConsents(): List<GdprConsentDb>
}
