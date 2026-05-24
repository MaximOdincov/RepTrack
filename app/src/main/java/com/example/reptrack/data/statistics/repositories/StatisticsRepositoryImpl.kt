package com.example.reptrack.data.statistics.repositories

import com.example.reptrack.data.local.dao.StatisticDao
import com.example.reptrack.data.local.dao.WeightRecordDao
import com.example.reptrack.data.local.dao.WorkoutDao
import com.example.reptrack.data.local.models.WeightRecordDb
import com.example.reptrack.data.local.models.statistics.FriendConfigDb
import com.example.reptrack.domain.statistics.repositories.StatisticsRepository
import com.example.reptrack.domain.statistics.entities.*
import com.example.reptrack.domain.workout.entities.MuscleGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class StatisticsRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val weightRecordDao: WeightRecordDao,
    private val statisticDao: StatisticDao
) : StatisticsRepository {

    override fun observeWeightData(userId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<WeightDataPoint>> {
        return workoutDao.observeWeightRecordsInRange(userId, fromDate, toDate).map { records ->
            records.map { record ->
                WeightDataPoint(
                    date = record.date.toLocalDate(),
                    value = record.value,
                    userId = userId,
                    userName = "You" // TODO: Get from user repository
                )
            }
        }
    }

    override fun observeFriendWeightData(friendId: String, friendName: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<WeightDataPoint>> {
        return workoutDao.observeFriendWeightRecords(friendId, fromDate, toDate).map { records ->
            records.map { record ->
                WeightDataPoint(
                    date = record.date.toLocalDate(),
                    value = record.value,
                    userId = friendId,
                    userName = friendName
                )
            }
        }
    }

    override suspend fun updateWeightRecord(userId: String, date: LocalDateTime, value: Float) {
        val startOfDay = date.toLocalDate().atStartOfDay()
        val endOfDay = date.toLocalDate().plusDays(1).atStartOfDay()

        val existingRecord = weightRecordDao.getRecordForDate(userId, startOfDay, endOfDay)
        val record = existingRecord?.copy(
            value = value,
            updatedAt = LocalDateTime.now()
        ) ?: WeightRecordDb(
            id = UUID.randomUUID().toString(),
            userId = userId,
            date = date,
            value = value
        )

        weightRecordDao.insertOrUpdate(record)
    }

    override fun observeExerciseData(userId: String, userName: String, exerciseId: String, fromDate: LocalDateTime, toDate: LocalDateTime, maxSets: Int): Flow<List<ExerciseDataPoint>> {
        android.util.Log.d("StatisticsRepository", "=== observeExerciseData called ===")
        android.util.Log.d("StatisticsRepository", "userId: $userId, userName: $userName")
        android.util.Log.d("StatisticsRepository", "exerciseId: $exerciseId")
        android.util.Log.d("StatisticsRepository", "Date range: $fromDate to $toDate")
        android.util.Log.d("StatisticsRepository", "maxSets: $maxSets")

        return workoutDao.observeExerciseProgress(userId, exerciseId, fromDate, toDate)
            .onEach { progress ->
                android.util.Log.d("StatisticsRepository", "=== Progress data received from DAO ===")
                android.util.Log.d("StatisticsRepository", "Progress points count: ${progress.size}")
                android.util.Log.d("StatisticsRepository", "Progress data: $progress")
            }
            .map { progress ->
                val exerciseDataPoints = progress.map { data ->
                    val point = ExerciseDataPoint(
                        date = data.date.toLocalDate(),
                        value = data.weight ?: 0f,
                        setIndex = (data.setOrder - 1).coerceIn(0, maxSets - 1),
                        userId = userId,
                        userName = userName
                    )
                    android.util.Log.d("StatisticsRepository", "Mapped data point: date=${data.date}, weight=${data.weight}, setOrder=${data.setOrder} -> setIndex=${point.setIndex}, value=${point.value}")
                    point
                }

                android.util.Log.d("StatisticsRepository", "Mapped ${exerciseDataPoints.size} ExerciseDataPoint(s)")
                android.util.Log.d("StatisticsRepository", "ExerciseDataPoints: $exerciseDataPoints")
                exerciseDataPoints
            }
    }

    override fun observeFriendExerciseData(friendId: String, friendName: String, exerciseId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<ExerciseDataPoint>> {
        return workoutDao.observeFriendBestSetPerDay(friendId, exerciseId, fromDate, toDate).map { sets ->
            sets.map { data ->
                ExerciseDataPoint(
                    date = data.date.toLocalDate(),
                    value = data.bestWeight,
                    setIndex = 0, // Best set is always index 0 for friends
                    userId = friendId,
                    userName = friendName
                )
            }
        }
    }

    override suspend fun friendHasExercise(friendId: String, exerciseId: String): Boolean {
        return workoutDao.friendHasExercise(friendId, exerciseId)
    }

    override fun observeMuscleGroupData(userId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<MuscleGroupDataPoint>> {
        return workoutDao.observeMuscleGroupFrequency(userId, fromDate, toDate).map { frequencyData ->
            frequencyData.mapNotNull { data ->
                try {
                    MuscleGroupDataPoint(
                        muscleGroup = MuscleGroup.valueOf(data.muscleGroup),
                        frequency = data.frequency
                    )
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    override fun observeFriendMuscleGroupData(friendId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<MuscleGroupDataPoint>> {
        return workoutDao.observeMuscleGroupFrequency(friendId, fromDate, toDate).map { frequencyData ->
            frequencyData.mapNotNull { data ->
                try {
                    MuscleGroupDataPoint(
                        muscleGroup = MuscleGroup.valueOf(data.muscleGroup),
                        frequency = data.frequency
                    )
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    override fun observeChartTemplates(userId: String): Flow<List<ChartTemplate>> {
        return statisticDao.getTemplatesWithFriends(userId).map { templatesWithFriends ->
            templatesWithFriends.map { templateWithFriends ->
                ChartTemplate(
                    id = templateWithFriends.template.id,
                    userId = templateWithFriends.template.userId,
                    name = templateWithFriends.template.name,
                    type = when (templateWithFriends.template.type) {
                        "WEIGHT_LINE" -> ChartType.WEIGHT_LINE
                        "EXERCISE_LINE" -> ChartType.EXERCISE_LINE
                        "SPIDER" -> ChartType.SPIDER
                        else -> ChartType.WEIGHT_LINE
                    },
                    dateRange = DateRange(
                        from = LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(templateWithFriends.template.dateFrom),
                            ZoneId.systemDefault()
                        ),
                        to = LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(templateWithFriends.template.dateTo),
                            ZoneId.systemDefault()
                        )
                    ),
                    friendConfigs = templateWithFriends.friendConfigs.map { config ->
                        FriendConfig(
                            friendId = config.friendId,
                            friendName = "", // Name will be filled in store layer
                            color = config.color
                        )
                    }
                )
            }
        }
    }

    override suspend fun saveChartTemplate(template: ChartTemplate) {
        val templateId = template.id ?: System.currentTimeMillis()
        val templateDb = com.example.reptrack.data.local.models.statistics.ChartTemplateDb(
            id = templateId,
            userId = template.userId,
            name = template.name,
            type = template.type.name,
            dateFrom = template.dateRange.from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            dateTo = template.dateRange.to.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )

        statisticDao.insertTemplate(templateDb)

        // Insert friend configs
        val friendConfigs = template.friendConfigs.map { friendConfig ->
            FriendConfigDb(
                templateId = templateId,
                userId = template.userId,
                friendId = friendConfig.friendId,
                color = friendConfig.color
            )
        }
        if (friendConfigs.isNotEmpty()) {
            statisticDao.insertFriendConfigs(friendConfigs)
        }
    }

    override suspend fun deleteChartTemplate(templateId: Long, userId: String) {
        statisticDao.deleteTemplate(userId, templateId)
    }
}