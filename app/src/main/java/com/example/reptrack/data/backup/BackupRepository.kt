package com.example.reptrack.data.backup

import com.example.reptrack.data.local.dao.*
import com.example.reptrack.data.local.models.*
import com.example.reptrack.data.backup.mapper.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import android.util.Log

class BackupRepository(
    private val firebaseDataSource: FirebaseBackupDataSource,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val templateDao: WorkoutTemplateDao,
    private val userDao: UserDao,
    private val statisticDao: StatisticDao,
    private val friendDao: FriendDao
) {

    companion object {
        private const val TAG = "sync"
    }

    suspend fun syncForUser(userId: String) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "========== Starting sync for user: $userId ==========")
            val startTime = System.currentTimeMillis()

            syncExercises(userId)
            syncWorkoutSessions(userId)
            syncWorkoutExercises(userId)  // Сначала синхронизируем упражнения в тренировке
            syncWorkoutSets(userId)       // Потом подходы (зависят от упражнений)
            syncWorkoutTemplates(userId)
            syncTemplateExercises(userId)
            syncUsers(userId)
            syncGdprConsents(userId)
            syncFriends(userId)
            syncChartTemplates(userId)
            syncFriendConfigs(userId)
            syncExerciseLineConfigs(userId)
            syncSetConfigs(userId)

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "========== Sync completed successfully in ${duration}ms ==========")
        } catch (e: Exception) {
            Log.e(TAG, "========== Sync failed for user $userId ==========", e)
            throw SyncException("Failed to sync data for user $userId", e)
        }
    }


    suspend fun syncUserOnly(userId: String) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "========== Syncing user only: $userId ==========")
            syncUsers(userId)
            syncGdprConsents(userId)
            Log.d(TAG, "========== User sync completed ==========")
        } catch (e: Exception) {
            Log.e(TAG, "========== User sync failed ==========", e)
            throw SyncException("Failed to sync user data for $userId", e)
        }
    }
    private suspend fun syncExercises(userId: String) {
        Log.d(TAG, "[Exercises] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = exerciseDao.getAllExercises()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.EXERCISES_COLLECTION)
                .documents.mapNotNull { ExerciseMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0
            var skipped = 0

            syncEntities(
                local, remote,
                { exercise ->
                    Log.d(TAG, "[Exercises] Uploading: ${exercise.id} - ${exercise.name}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.EXERCISES_COLLECTION,
                        exercise.id, ExerciseMapper.toFirestore(exercise))
                    uploaded++
                },
                { exercise ->
                    Log.d(TAG, "[Exercises] Downloading: ${exercise.id} - ${exercise.name}")
                    exerciseDao.insert(exercise)
                    downloaded++
                },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[Exercises] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Skipped: $skipped | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[Exercises] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncWorkoutSessions(userId: String) {
        Log.d(TAG, "[WorkoutSessions] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = workoutDao.observeSessions(userId).first().map { it.session }
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.WORKOUT_SESSIONS_COLLECTION)
                .documents.mapNotNull { WorkoutSessionMapper.fromFirestore(it, userId) }

            var uploaded = 0
            var downloaded = 0

            syncEntities(
                local, remote,
                { session ->
                    Log.d(TAG, "[WorkoutSessions] Uploading: ${session.id} - ${session.name}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.WORKOUT_SESSIONS_COLLECTION,
                        session.id, WorkoutSessionMapper.toFirestore(session))
                    uploaded++
                },
                { workoutDao.insertSession(it)
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[WorkoutSessions] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[WorkoutSessions] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncWorkoutExercises(userId: String) {
        Log.d(TAG, "[WorkoutExercises] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = workoutDao.getAllExercises()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.WORKOUT_EXERCISES_COLLECTION)
                .documents.mapNotNull { WorkoutExerciseMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntities(
                local, remote,
                { exercise ->
                    Log.d(TAG, "[WorkoutExercises] Uploading: ${exercise.id}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.WORKOUT_EXERCISES_COLLECTION,
                        exercise.id, WorkoutExerciseMapper.toFirestore(exercise))
                    uploaded++
                },
                { workoutDao.insertExercises(listOf(it))
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[WorkoutExercises] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[WorkoutExercises] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncWorkoutSets(userId: String) {
        Log.d(TAG, "[WorkoutSets] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = workoutDao.getAllSets()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.WORKOUT_SETS_COLLECTION)
                .documents.mapNotNull { WorkoutSetMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntities(
                local, remote,
                { set ->
                    Log.d(TAG, "[WorkoutSets] Uploading: ${set.id}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.WORKOUT_SETS_COLLECTION,
                        set.id, WorkoutSetMapper.toFirestore(set))
                    uploaded++
                },
                { workoutDao.insertSets(listOf(it))
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[WorkoutSets] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[WorkoutSets] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncWorkoutTemplates(userId: String) {
        Log.d(TAG, "[WorkoutTemplates] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = templateDao.getAllTemplates()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.WORKOUT_TEMPLATES_COLLECTION)
                .documents.mapNotNull { WorkoutTemplateMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntities(
                local, remote,
                { template ->
                    Log.d(TAG, "[WorkoutTemplates] Uploading: ${template.id} - ${template.name}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.WORKOUT_TEMPLATES_COLLECTION,
                        template.id, WorkoutTemplateMapper.toFirestore(template))
                    uploaded++
                },
                { templateDao.insertTemplate(it)
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[WorkoutTemplates] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[WorkoutTemplates] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncTemplateExercises(userId: String) {
        Log.d(TAG, "[TemplateExercises] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = templateDao.getAllTemplateExercises()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.TEMPLATE_EXERCISES_COLLECTION)
                .documents.mapNotNull { TemplateExerciseMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntitiesWithCompositeKey(
                local, remote,
                { templateEx ->
                    val docId = "${templateEx.templateId}_${templateEx.exerciseId}"
                    Log.d(TAG, "[TemplateExercises] Uploading: $docId")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.TEMPLATE_EXERCISES_COLLECTION,
                        docId, TemplateExerciseMapper.toFirestore(templateEx))
                    uploaded++
                },
                { templateDao.insertTemplateExercises(listOf(it))
                    downloaded++ },
                { "${it.templateId}_${it.exerciseId}" },
                { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[TemplateExercises] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[TemplateExercises] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncUsers(userId: String) {
        Log.d(TAG, "[Users] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = userDao.getAllUsers().filter { it.id == userId }
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.USERS_ENTITY_COLLECTION)
                .documents.mapNotNull { UserMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntities(
                local, remote,
                { user ->
                    Log.d(TAG, "[Users] Uploading: ${user.id} - ${user.username}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.USERS_ENTITY_COLLECTION,
                        user.id, UserMapper.toFirestore(user))
                    uploaded++
                },
                { userDao.insertUser(it)
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[Users] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[Users] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncGdprConsents(userId: String) {
        Log.d(TAG, "[GdprConsents] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = userDao.getAllConsents()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.GDPR_CONSENTS_COLLECTION)
                .documents.mapNotNull { GdprConsentMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntities(
                local, remote,
                { consent ->
                    Log.d(TAG, "[GdprConsents] Uploading: ${consent.userId}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.GDPR_CONSENTS_COLLECTION,
                        consent.userId, GdprConsentMapper.toFirestore(consent))
                    uploaded++
                },
                { userDao.insertConsent(it)
                    downloaded++ },
                { it.userId }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[GdprConsents] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[GdprConsents] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncFriends(userId: String) {
        Log.d(TAG, "[Friends] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = friendDao.getAllFriends()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.FRIENDS_COLLECTION)
                .documents.mapNotNull { FriendMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntitiesWithLongKey(
                local, remote,
                { friend ->
                    Log.d(TAG, "[Friends] Uploading: ${friend.id} - ${friend.username}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.FRIENDS_COLLECTION,
                        friend.id.toString(), FriendMapper.toFirestore(friend))
                    uploaded++
                },
                { friendDao.insertFriends(listOf(it))
                    downloaded++ },
                { it.id }, { FriendMapper.getCreatedAt(it) }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[Friends] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[Friends] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncChartTemplates(userId: String) {
        Log.d(TAG, "[ChartTemplates] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = statisticDao.getAllTemplates()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.CHART_TEMPLATES_COLLECTION)
                .documents.mapNotNull { ChartTemplateMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntitiesWithLongKey(
                local, remote,
                { template ->
                    Log.d(TAG, "[ChartTemplates] Uploading: ${template.id}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.CHART_TEMPLATES_COLLECTION,
                        template.id.toString(), ChartTemplateMapper.toFirestore(template))
                    uploaded++
                },
                { statisticDao.insertTemplate(it)
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[ChartTemplates] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[ChartTemplates] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncFriendConfigs(userId: String) {
        Log.d(TAG, "[FriendConfigs] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = statisticDao.getAllFriendConfigs()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.FRIEND_CONFIGS_COLLECTION)
                .documents.mapNotNull { FriendConfigMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntitiesWithLongKey(
                local, remote,
                { config ->
                    Log.d(TAG, "[FriendConfigs] Uploading: ${config.id}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.FRIEND_CONFIGS_COLLECTION,
                        config.id.toString(), FriendConfigMapper.toFirestore(config))
                    uploaded++
                },
                { statisticDao.insertFriendConfigs(listOf(it))
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[FriendConfigs] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[FriendConfigs] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncExerciseLineConfigs(userId: String) {
        Log.d(TAG, "[ExerciseLineConfigs] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = statisticDao.getAllExerciseLineConfigs()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.EXERCISE_LINE_CONFIGS_COLLECTION)
                .documents.mapNotNull { ExerciseLineConfigMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntitiesWithLongKey(
                local, remote,
                { config ->
                    Log.d(TAG, "[ExerciseLineConfigs] Uploading: ${config.id}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.EXERCISE_LINE_CONFIGS_COLLECTION,
                        config.id.toString(), ExerciseLineConfigMapper.toFirestore(config))
                    uploaded++
                },
                { statisticDao.insertExerciseLineConfigs(listOf(it))
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[ExerciseLineConfigs] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[ExerciseLineConfigs] Sync failed", e)
            throw e
        }
    }

    private suspend fun syncSetConfigs(userId: String) {
        Log.d(TAG, "[SetConfigs] Starting sync...")
        val startTime = System.currentTimeMillis()

        try {
            val local = statisticDao.getAllSetConfigs()
            val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.SET_CONFIGS_COLLECTION)
                .documents.mapNotNull { SetConfigMapper.fromFirestore(it) }

            var uploaded = 0
            var downloaded = 0

            syncEntitiesWithLongKey(
                local, remote,
                { config ->
                    Log.d(TAG, "[SetConfigs] Uploading: ${config.id}")
                    firebaseDataSource.uploadDocument(userId, FirestoreConstants.SET_CONFIGS_COLLECTION,
                        config.id.toString(), SetConfigMapper.toFirestore(config))
                    uploaded++
                },
                { statisticDao.insertSetConfigs(listOf(it))
                    downloaded++ },
                { it.id }, { it.updatedAt }, { it.deletedAt != null }
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "[SetConfigs] Sync completed in ${duration}ms | Uploaded: $uploaded | Downloaded: $downloaded | Local: ${local.size} | Remote: ${remote.size}")
        } catch (e: Exception) {
            Log.e(TAG, "[SetConfigs] Sync failed", e)
            throw e
        }
    }

    private suspend fun <T> syncEntities(
        local: List<T>,
        remote: List<T>,
        onUpload: suspend (T) -> Unit,
        onSaveLocal: suspend (T) -> Unit,
        getId: (T) -> String,
        getUpdatedAt: (T) -> java.time.LocalDateTime,
        isDeleted: (T) -> Boolean
    ) {
        val remoteMap = remote.associateBy { getId(it) }

        local.forEach { localItem ->
            val localId = getId(localItem)
            val remoteItem = remoteMap[localId]

            if (remoteItem == null) {
                // Нет удалённых данных - загружаем локальные в Firestore
                onUpload(localItem)
            } else {
                val localDeleted = isDeleted(localItem)
                val remoteDeleted = isDeleted(remoteItem)
                val localUpdated = TimestampMapper.toTimestamp(getUpdatedAt(localItem))
                val remoteUpdated = TimestampMapper.toTimestamp(getUpdatedAt(remoteItem))

                when {
                    // Оба удалены - ничего не делаем
                    localDeleted && remoteDeleted -> Unit

                    // Локальный удалён, а удалённый нет - применяем удаление к локальному
                    localDeleted && !remoteDeleted -> onSaveLocal(remoteItem)

                    // Удалённый удалён, а локальный нет - загружаем локальный в Firestore
                    !localDeleted && remoteDeleted -> onUpload(localItem)

                    // Оба активны - сравниваем по времени
                    localUpdated > remoteUpdated -> onUpload(localItem)
                    remoteUpdated > localUpdated -> onSaveLocal(remoteItem)
                }
            }
        }

        val localIds = local.map(getId).toSet()
        remote.forEach { remoteItem ->
            // Новые записи из Firestore (нет локально и не удалены)
            if (getId(remoteItem) !in localIds && !isDeleted(remoteItem)) {
                onSaveLocal(remoteItem)
            }
        }
    }

    private suspend fun <T> syncEntitiesWithLongKey(
        local: List<T>,
        remote: List<T>,
        onUpload: suspend (T) -> Unit,
        onSaveLocal: suspend (T) -> Unit,
        getId: (T) -> Long,
        getUpdatedAt: (T) -> java.time.LocalDateTime,
        isDeleted: (T) -> Boolean
    ) {
        val remoteMap = remote.associateBy { getId(it) }

        local.forEach { localItem ->
            val localId = getId(localItem)
            val remoteItem = remoteMap[localId]

            if (remoteItem == null) {
                onUpload(localItem)
            } else {
                val localUpdated = TimestampMapper.toTimestamp(getUpdatedAt(localItem))
                val remoteUpdated = TimestampMapper.toTimestamp(getUpdatedAt(remoteItem))

                when {
                    isDeleted(localItem) || isDeleted(remoteItem) -> {
                        if (!isDeleted(localItem)) onSaveLocal(remoteItem)
                        else if (!isDeleted(remoteItem)) onUpload(localItem)
                    }
                    localUpdated > remoteUpdated -> onUpload(localItem)
                    remoteUpdated > localUpdated -> onSaveLocal(remoteItem)
                }
            }
        }

        val localIds = local.map(getId).toSet()
        remote.forEach { remoteItem ->
            if (getId(remoteItem) !in localIds && !isDeleted(remoteItem)) {
                onSaveLocal(remoteItem)
            }
        }
    }

    private suspend fun <T> syncEntitiesWithCompositeKey(
        local: List<T>,
        remote: List<T>,
        onUpload: suspend (T) -> Unit,
        onSaveLocal: suspend (T) -> Unit,
        getKey: (T) -> String,
        getUpdatedAt: (T) -> java.time.LocalDateTime,
        isDeleted: (T) -> Boolean
    ) {
        val remoteMap = remote.associateBy { getKey(it) }

        local.forEach { localItem ->
            val key = getKey(localItem)
            val remoteItem = remoteMap[key]

            if (remoteItem == null) {
                onUpload(localItem)
            } else {
                val localUpdated = TimestampMapper.toTimestamp(getUpdatedAt(localItem))
                val remoteUpdated = TimestampMapper.toTimestamp(getUpdatedAt(remoteItem))

                when {
                    isDeleted(localItem) || isDeleted(remoteItem) -> {
                        if (!isDeleted(localItem)) onSaveLocal(remoteItem)
                        else if (!isDeleted(remoteItem)) onUpload(localItem)
                    }
                    localUpdated > remoteUpdated -> onUpload(localItem)
                    remoteUpdated > localUpdated -> onSaveLocal(remoteItem)
                }
            }
        }

        val localKeys = local.map(getKey).toSet()
        remote.forEach { remoteItem ->
            if (getKey(remoteItem) !in localKeys && !isDeleted(remoteItem)) {
                onSaveLocal(remoteItem)
            }
        }
    }
}

class SyncException(message: String, cause: Throwable? = null) : Exception(message, cause)
