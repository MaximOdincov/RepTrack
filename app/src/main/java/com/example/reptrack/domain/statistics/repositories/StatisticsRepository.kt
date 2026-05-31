package com.example.reptrack.domain.statistics.repositories

import com.example.reptrack.domain.statistics.entities.ChartData
import com.example.reptrack.domain.statistics.entities.ChartTemplate
import com.example.reptrack.domain.statistics.entities.ExerciseDataPoint
import com.example.reptrack.domain.statistics.entities.MuscleGroupDataPoint
import com.example.reptrack.domain.statistics.entities.WeightDataPoint
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface StatisticsRepository {
    // Weight
    fun observeWeightData(userId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<WeightDataPoint>>
    fun observeFriendWeightData(friendId: String, friendName: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<WeightDataPoint>>
    suspend fun updateWeightRecord(userId: String, date: LocalDateTime, value: Float)
    suspend fun getCurrentWeight(userId: String): Float?

    // Exercise
    fun observeExerciseData(userId: String, userName: String, exerciseId: String, fromDate: LocalDateTime, toDate: LocalDateTime, maxSets: Int = 10): Flow<List<ExerciseDataPoint>>
    fun observeFriendExerciseData(friendId: String, friendName: String, exerciseId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<ExerciseDataPoint>>
    suspend fun friendHasExercise(friendId: String, exerciseId: String): Boolean
    suspend fun debugGetFriendExercises(friendId: String): List<com.example.reptrack.data.local.models.statistics.FriendExerciseDebugInfo>

    // Muscle Groups
    fun observeMuscleGroupData(userId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<MuscleGroupDataPoint>>
    fun observeFriendMuscleGroupData(friendId: String, fromDate: LocalDateTime, toDate: LocalDateTime): Flow<List<MuscleGroupDataPoint>>

    // Chart Templates
    fun observeChartTemplates(userId: String): Flow<List<ChartTemplate>>
    suspend fun saveChartTemplate(template: ChartTemplate)
    suspend fun deleteChartTemplate(templateId: Long, userId: String)
}