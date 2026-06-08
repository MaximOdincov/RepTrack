package com.example.reptrack.domain.statistics.usecases

import com.example.reptrack.data.backup.FirebaseBackupDataSource
import com.example.reptrack.data.backup.FirestoreConstants
import com.example.reptrack.data.backup.mapper.TimestampMapper
import com.example.reptrack.domain.statistics.entities.MuscleGroupDataPoint
import com.example.reptrack.domain.workout.entities.MuscleGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

/**
 * Fetches friend's muscle group data directly from Firebase
 * without storing it in local database
 */
class GetFriendMuscleGroupDataFromFirebaseUseCase(
    private val firebaseDataSource: FirebaseBackupDataSource
) {
    operator fun invoke(
        friendId: String,
        fromDate: LocalDateTime,
        toDate: LocalDateTime
    ): Flow<List<MuscleGroupDataPoint>> = flow {
        try {
            android.util.Log.d("FirebaseMuscle", "Loading muscle group data from Firebase for friend: $friendId")
            android.util.Log.d("FirebaseMuscle", "Date range: $fromDate to $toDate")

            // Step 1: Get friend's workout sessions
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
                        android.util.Log.e("FirebaseMuscle", "Error parsing session: ${e.message}")
                        null
                    }
                }

            android.util.Log.d("FirebaseMuscle", "Total sessions in Firebase: ${allSessions.size}")
            allSessions.forEach { session ->
                android.util.Log.d("FirebaseMuscle", "  Session: ${session.id}, date: ${session.date}, status: ${session.status}")
            }

            val notDeletedSessions = allSessions.filter { it.deletedAt == null }
            android.util.Log.d("FirebaseMuscle", "Not deleted sessions: ${notDeletedSessions.size}")

            val completedSessions = notDeletedSessions.filter { it.status == "COMPLETED" || it.status == "IN_PROGRESS" }
            android.util.Log.d("FirebaseMuscle", "Completed/In-Progress sessions: ${completedSessions.size}")

            val sessions = completedSessions.filter { it.date >= fromDate && it.date <= toDate }
            android.util.Log.d("FirebaseMuscle", "Sessions in date range: ${sessions.size}")

            // Step 2: Get friend's exercises for these sessions
            val muscleGroupFrequency = mutableMapOf<String, Int>()

            sessions.forEach { session ->
                val exercisesSnapshot = firebaseDataSource.listDocuments(
                    friendId,
                    FirestoreConstants.WORKOUT_EXERCISES_COLLECTION
                )

                val sessionExercises = exercisesSnapshot.documents
                    .mapNotNull { doc ->
                        try {
                            val sessionId = doc.getString("workoutSessionId") ?: return@mapNotNull null
                            val muscleGroupStr = doc.getString("muscleGroup")
                            val deletedAt = doc.getLong("deletedAt")?.let { TimestampMapper.fromTimestamp(it) }

                            if (sessionId == session.id && deletedAt == null && muscleGroupStr != null) {
                                android.util.Log.d("FirebaseMuscle", "  Exercise: $muscleGroupStr for session ${session.id}")
                                muscleGroupStr
                            } else null
                        } catch (e: Exception) {
                            android.util.Log.e("FirebaseMuscle", "Error parsing exercise: ${e.message}")
                            null
                        }
                    }

                sessionExercises.forEach { muscleGroupStr ->
                    muscleGroupFrequency[muscleGroupStr] =
                        muscleGroupFrequency.getOrDefault(muscleGroupStr, 0) + 1
                }
            }

            android.util.Log.d("FirebaseMuscle", "Muscle group frequency: $muscleGroupFrequency")

            // Step 3: Convert to MuscleGroupDataPoint
            val result = muscleGroupFrequency.mapNotNull { (muscleGroupStr, frequency) ->
                try {
                    val muscleGroup = MuscleGroup.valueOf(muscleGroupStr)
                    MuscleGroupDataPoint(muscleGroup = muscleGroup, frequency = frequency)
                } catch (e: IllegalArgumentException) {
                    android.util.Log.w("FirebaseMuscle", "Unknown muscle group: $muscleGroupStr")
                    null
                }
            }

            android.util.Log.d("FirebaseMuscle", "Final result: ${result.size} items")
            result.forEach { data ->
                android.util.Log.d("FirebaseMuscle", "  ${data.muscleGroup}: ${data.frequency}")
            }

            emit(result)

        } catch (e: Exception) {
            android.util.Log.e("FirebaseMuscle", "Error loading friend muscle data: ${e.message}", e)
            emit(emptyList())
        }
    }

    // Helper data class
    data class SessionWithDate(
        val id: String,
        val date: LocalDateTime,
        val deletedAt: LocalDateTime?,
        val status: String
    )
}