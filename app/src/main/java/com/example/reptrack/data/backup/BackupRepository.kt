package com.example.reptrack.data.backup

import com.example.reptrack.data.local.dao.*
import com.example.reptrack.data.local.models.*
import com.example.reptrack.data.backup.mapper.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class BackupRepository(
    private val firebaseDataSource: FirebaseBackupDataSource,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val templateDao: WorkoutTemplateDao,
    private val userDao: UserDao,
    private val statisticDao: StatisticDao
) {

    suspend fun syncForUser(userId: String) = withContext(Dispatchers.IO) {
        try {
            syncExercises(userId)
            syncWorkoutSessions(userId)
            syncWorkoutSets(userId)
            syncWorkoutExercises(userId)
            syncWorkoutTemplates(userId)
            syncTemplateExercises(userId)
            syncUsers(userId)
            syncGdprConsents(userId)
            syncChartTemplates(userId)
            syncFriendConfigs(userId)
            syncExerciseLineConfigs(userId)
            syncSetConfigs(userId)
        } catch (e: Exception) {
            throw SyncException("Failed to sync data for user $userId", e)
        }
    }

    private suspend fun syncExercises(userId: String) {
        val local = exerciseDao.getAllExercises()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.EXERCISES_COLLECTION)
            .documents.mapNotNull { ExerciseMapper.fromFirestore(it) }

        syncEntities(
            local, remote,
            { exercise ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.EXERCISES_COLLECTION, 
                    exercise.id, ExerciseMapper.toFirestore(exercise))
            },
            { exerciseDao.insert(it) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncWorkoutSessions(userId: String) {
        val local = workoutDao.observeSessions(userId).first().map { it.session }
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.WORKOUT_SESSIONS_COLLECTION)
            .documents.mapNotNull { WorkoutSessionMapper.fromFirestore(it, userId) }

        syncEntities(
            local, remote,
            { session ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.WORKOUT_SESSIONS_COLLECTION,
                    session.id, WorkoutSessionMapper.toFirestore(session))
            },
            { workoutDao.insertSession(it) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncWorkoutExercises(userId: String) {
        val local = workoutDao.getAllExercises()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.WORKOUT_EXERCISES_COLLECTION)
            .documents.mapNotNull { WorkoutExerciseMapper.fromFirestore(it) }

        syncEntities(
            local, remote,
            { exercise ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.WORKOUT_EXERCISES_COLLECTION,
                    exercise.id, WorkoutExerciseMapper.toFirestore(exercise))
            },
            { workoutDao.insertExercises(listOf(it)) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncWorkoutSets(userId: String) {
        val local = workoutDao.getAllSets()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.WORKOUT_SETS_COLLECTION)
            .documents.mapNotNull { WorkoutSetMapper.fromFirestore(it) }

        syncEntities(
            local, remote,
            { set ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.WORKOUT_SETS_COLLECTION,
                    set.id, WorkoutSetMapper.toFirestore(set))
            },
            { workoutDao.insertSets(listOf(it)) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncWorkoutTemplates(userId: String) {
        val local = templateDao.getAllTemplates()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.WORKOUT_TEMPLATES_COLLECTION)
            .documents.mapNotNull { WorkoutTemplateMapper.fromFirestore(it) }

        syncEntities(
            local, remote,
            { template ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.WORKOUT_TEMPLATES_COLLECTION,
                    template.id, WorkoutTemplateMapper.toFirestore(template))
            },
            { templateDao.insertTemplate(it) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncTemplateExercises(userId: String) {
        val local = templateDao.getAllTemplateExercises()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.TEMPLATE_EXERCISES_COLLECTION)
            .documents.mapNotNull { TemplateExerciseMapper.fromFirestore(it) }

        syncEntitiesWithCompositeKey(
            local, remote,
            { templateEx ->
                val docId = "${templateEx.templateId}_${templateEx.exerciseId}"
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.TEMPLATE_EXERCISES_COLLECTION,
                    docId, TemplateExerciseMapper.toFirestore(templateEx))
            },
            { templateDao.insertTemplateExercises(listOf(it)) },
            { "${it.templateId}_${it.exerciseId}" },
            { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncUsers(userId: String) {
        val local = userDao.getAllUsers().filter { it.id == userId }
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.USERS_ENTITY_COLLECTION)
            .documents.mapNotNull { it.toObject(UserDb::class.java) }

        syncEntities(
            local, remote,
            { user ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.USERS_ENTITY_COLLECTION,
                    user.id, mapOf(
                        "id" to user.id,
                        "username" to user.username,
                        "email" to user.email,
                        "avatarUrl" to user.avatarUrl,
                        "currentWeight" to user.currentWeight,
                        "height" to user.height,
                        "updatedAt" to TimestampMapper.toTimestamp(user.updatedAt),
                        "deletedAt" to user.deletedAt?.let { TimestampMapper.toTimestamp(it) }
                    ))
            },
            { userDao.insertUser(it) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncGdprConsents(userId: String) {
        val local = userDao.getAllConsents()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.GDPR_CONSENTS_COLLECTION)
            .documents.mapNotNull { GdprConsentMapper.fromFirestore(it) }

        syncEntities(
            local, remote,
            { consent ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.GDPR_CONSENTS_COLLECTION,
                    consent.userId, GdprConsentMapper.toFirestore(consent))
            },
            { userDao.insertConsent(it) },
            { it.userId }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncChartTemplates(userId: String) {
        val local = statisticDao.getAllTemplates()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.CHART_TEMPLATES_COLLECTION)
            .documents.mapNotNull { ChartTemplateMapper.fromFirestore(it) }

        syncEntitiesWithLongKey(
            local, remote,
            { template ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.CHART_TEMPLATES_COLLECTION,
                    template.id.toString(), ChartTemplateMapper.toFirestore(template))
            },
            { statisticDao.insertTemplate(it) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncFriendConfigs(userId: String) {
        val local = statisticDao.getAllFriendConfigs()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.FRIEND_CONFIGS_COLLECTION)
            .documents.mapNotNull { FriendConfigMapper.fromFirestore(it) }

        syncEntitiesWithLongKey(
            local, remote,
            { config ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.FRIEND_CONFIGS_COLLECTION,
                    config.id.toString(), FriendConfigMapper.toFirestore(config))
            },
            { statisticDao.insertFriendConfigs(listOf(it)) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncExerciseLineConfigs(userId: String) {
        val local = statisticDao.getAllExerciseLineConfigs()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.EXERCISE_LINE_CONFIGS_COLLECTION)
            .documents.mapNotNull { ExerciseLineConfigMapper.fromFirestore(it) }

        syncEntitiesWithLongKey(
            local, remote,
            { config ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.EXERCISE_LINE_CONFIGS_COLLECTION,
                    config.id.toString(), ExerciseLineConfigMapper.toFirestore(config))
            },
            { statisticDao.insertExerciseLineConfigs(listOf(it)) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
    }

    private suspend fun syncSetConfigs(userId: String) {
        val local = statisticDao.getAllSetConfigs()
        val remote = firebaseDataSource.listDocuments(userId, FirestoreConstants.SET_CONFIGS_COLLECTION)
            .documents.mapNotNull { SetConfigMapper.fromFirestore(it) }

        syncEntitiesWithLongKey(
            local, remote,
            { config ->
                firebaseDataSource.uploadDocument(userId, FirestoreConstants.SET_CONFIGS_COLLECTION,
                    config.id.toString(), SetConfigMapper.toFirestore(config))
            },
            { statisticDao.insertSetConfigs(listOf(it)) },
            { it.id }, { it.updatedAt }, { it.deletedAt != null }
        )
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
