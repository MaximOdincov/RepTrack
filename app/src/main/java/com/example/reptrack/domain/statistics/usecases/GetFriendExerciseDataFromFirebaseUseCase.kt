package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.data.backup.FirebaseBackupDataSource
import com.example.reptrack.data.backup.FirestoreConstants
import com.example.reptrack.data.backup.mapper.TimestampMapper
import com.example.reptrack.data.local.dao.ExerciseDao
import com.example.reptrack.domain.statistics.entities.ExerciseDataPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fetches friend's exercise data directly from Firebase
 * without storing it in local database
 *
 * IMPORTANT: Exercises have different IDs in different local databases,
 * but the same NAME across all users. We match exercises by NAME, not ID.
 */
class GetFriendExerciseDataFromFirebaseUseCase(
    private val firebaseDataSource: FirebaseBackupDataSource,
    private val exerciseDao: ExerciseDao
) {
    operator fun invoke(
        friendId: String,
        friendName: String,
        exerciseId: String,
        fromDate: java.time.LocalDateTime,
        toDate: java.time.LocalDateTime
    ): Flow<List<ExerciseDataPoint>> = flow {
        android.util.Log.d("FRIEND_FIREBASE", "Loading exercise data from Firebase for friend: $friendId ($friendName), exercise: $exerciseId")
        android.util.Log.d("FRIEND_FIREBASE", "Date range: $fromDate to $toDate")

        // Step 1: Get exercise name from local database (user's exercise)
        val userExercise = exerciseDao.getById(exerciseId)
        if (userExercise == null) {
            android.util.Log.e("FRIEND_FIREBASE", "❌ Exercise with ID $exerciseId not found in local database")
            emit(emptyList())
            return@flow
        }

        val exerciseName = userExercise.name
        android.util.Log.d("FRIEND_FIREBASE", "✅ Found exercise in local DB: $exerciseName (ID: $exerciseId)")

        // Step 2: Get friend's exercises from Firebase to find the friend's exercise ID by name
        val friendExercisesSnapshot = firebaseDataSource.listDocuments(
            friendId,
            FirestoreConstants.EXERCISES_COLLECTION
        )

        val friendExerciseId = friendExercisesSnapshot.documents
            .mapNotNull { doc ->
                try {
                    val id = doc.id
                    val name = doc.getString("name")
                    val deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
                    if (name != null && deletedAt == null) {
                        android.util.Log.d("FRIEND_FIREBASE", "  Friend's exercise: $name (ID: $id)")
                        Pair(id, name)
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
            .firstOrNull { it.second == exerciseName }?.first

        if (friendExerciseId == null) {
            android.util.Log.e("FRIEND_FIREBASE", "❌ Friend doesn't have exercise with name: $exerciseName")
            emit(emptyList())
            return@flow
        }

        android.util.Log.d("FRIEND_FIREBASE", "✅ Found friend's exercise ID: $friendExerciseId (name: $exerciseName)")

        // Step 3: Get friend's workout sessions
        val sessionsSnapshot = firebaseDataSource.listDocuments(
            friendId,
            FirestoreConstants.WORKOUT_SESSIONS_COLLECTION
        )

        val allSessions = sessionsSnapshot.documents
            .mapNotNull { doc ->
                try {
                    val dateStr = doc.getString("date") ?: return@mapNotNull null
                    val date = java.time.LocalDateTime.parse(dateStr)
                    val deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }
                    val status = doc.getString("status") ?: "PLANNED"

                    SessionWithDate(doc.id, date, deletedAt, status)
                } catch (e: Exception) {
                    android.util.Log.e("FRIEND_FIREBASE", "Error parsing session: ${e.message}")
                    null
                }
            }

        android.util.Log.d("FRIEND_FIREBASE", "Total sessions in Firebase: ${allSessions.size}")
        allSessions.forEach { session ->
            android.util.Log.d("FRIEND_FIREBASE", "  Session: ${session.id}, date: ${session.date}, status: ${session.status}, deletedAt: ${session.deletedAt}")
        }

        val notDeletedSessions = allSessions.filter { it.deletedAt == null }
        android.util.Log.d("FRIEND_FIREBASE", "Not deleted sessions: ${notDeletedSessions.size}")

        val completedSessions = notDeletedSessions.filter { it.status == "COMPLETED" || it.status == "IN_PROGRESS" }
        android.util.Log.d("FRIEND_FIREBASE", "Completed/In-Progress sessions: ${completedSessions.size}")
        completedSessions.forEach { session ->
            android.util.Log.d("FRIEND_FIREBASE", "  Session: ${session.id}, date: ${session.date}, status: ${session.status}")
        }

        val sessions = completedSessions.filter { it.date >= fromDate && it.date < toDate }
        android.util.Log.d("FRIEND_FIREBASE", "Completed sessions in date range (${fromDate} - ${toDate}): ${sessions.size}")

        if (sessions.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        val sessionIds = sessions.map { it.id }.toSet()
        val sessionDateMap = sessions.associate { it.id to it.date }

        // Get workout exercises for these sessions
        val exercisesSnapshot = firebaseDataSource.listDocuments(
            friendId,
            FirestoreConstants.WORKOUT_EXERCISES_COLLECTION
        )

        val exercises = exercisesSnapshot.documents
            .mapNotNull { doc ->
                try {
                    val workoutSessionId = doc.getString("workoutSessionId") ?: return@mapNotNull null
                    val exerciseRefId = doc.getString("exerciseId") ?: return@mapNotNull null
                    val deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }

                    ExerciseWithSession(doc.id, workoutSessionId, exerciseRefId, deletedAt)
                } catch (e: Exception) {
                    null
                }
            }
            .filter { it.deletedAt == null && sessionIds.contains(it.workoutSessionId) && it.exerciseRefId == friendExerciseId }

        android.util.Log.d("FRIEND_FIREBASE", "Found ${exercises.size} exercises with ID: $friendExerciseId (name: $exerciseName)")

        if (exercises.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        val exerciseIds = exercises.map { it.id }.toSet()

        // Get workout sets for these exercises
        val setsSnapshot = firebaseDataSource.listDocuments(
            friendId,
            FirestoreConstants.WORKOUT_SETS_COLLECTION
        )

        val sets = setsSnapshot.documents
            .mapNotNull { doc ->
                try {
                    val workoutExerciseId = doc.getString("workoutExerciseId") ?: return@mapNotNull null
                    val weight = doc.getDouble("weight")?.toFloat()
                    val isCompleted = doc.getBoolean("isCompleted") ?: false
                    val deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }

                    SetWithDate(workoutExerciseId, weight, isCompleted, deletedAt)
                } catch (e: Exception) {
                    null
                }
            }
            .filter { it.deletedAt == null && exerciseIds.contains(it.workoutExerciseId) && it.isCompleted && it.weight != null && it.weight!! > 0 }

        android.util.Log.d("FRIEND_FIREBASE", "Found ${sets.size} completed sets with weight")

        if (sets.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        // Map sets to their sessions to get dates
        val exerciseToSessionMap = exercises.associate { it.id to it.workoutSessionId }

        // Group by date and get best weight per day
        val bestWeightsByDate = sets
            .mapNotNull { set ->
                val sessionId = exerciseToSessionMap[set.workoutExerciseId]
                val date = sessionId?.let { sessionDateMap[it] }
                if (date != null) {
                    date to set.weight!!
                } else null
            }
            .groupBy { it.first }
            .mapValues { (_, pairs) -> pairs.maxByOrNull { it.second }?.second }

        // Create data points
        val dataPoints = bestWeightsByDate.map { (date, weight) ->
            ExerciseDataPoint(
                date = date.toLocalDate(),
                value = weight ?: 0f,
                setIndex = 0,
                userId = friendId,
                userName = friendName
            )
        }.filter { it.value > 0f }
        .sortedBy { it.date }

        android.util.Log.d("FRIEND_FIREBASE", "Returning ${dataPoints.size} data points")
        dataPoints.forEach { point ->
            android.util.Log.d("FRIEND_FIREBASE", "  📅 ${point.date} 💪 ${point.value}kg")
        }

        emit(dataPoints)
    }

    private data class SessionWithDate(
        val id: String,
        val date: java.time.LocalDateTime,
        val deletedAt: java.time.LocalDateTime?,
        val status: String
    )

    private data class ExerciseWithSession(
        val id: String,
        val workoutSessionId: String,
        val exerciseRefId: String,
        val deletedAt: java.time.LocalDateTime?
    )

    private data class SetWithDate(
        val workoutExerciseId: String,
        val weight: Float?,
        val isCompleted: Boolean,
        val deletedAt: java.time.LocalDateTime?
    )
}