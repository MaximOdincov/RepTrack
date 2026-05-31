package com.example.reptrack.domain.statistics.entities

import com.example.reptrack.domain.workout.entities.MuscleGroup
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

data class ChartData(
    val type: ChartType,
    val dateFrom: LocalDateTime,
    val dateTo: LocalDateTime,
    val title: String
)

enum class ChartType {
    WEIGHT_LINE,
    EXERCISE_LINE,
    SPIDER
}

data class WeightDataPoint(
    val date: LocalDate,
    val value: Float,
    val userId: String,
    val userName: String
)

data class ExerciseDataPoint(
    val date: LocalDate,
    val value: Float,
    val setIndex: Int,
    val userId: String,
    val userName: String
)

data class MuscleGroupDataPoint(
    val muscleGroup: MuscleGroup,
    val frequency: Int
)

data class DateRange(
    val from: LocalDateTime,
    val to: LocalDateTime
) {
    companion object {
        fun last7Days(): DateRange {
            val to = LocalDateTime.now()
            val from = to.minusDays(7)
            return DateRange(from, to)
        }

        fun last30Days(): DateRange {
            val to = LocalDateTime.now()
            val from = to.minusDays(30)
            return DateRange(from, to)
        }

        fun last3Months(): DateRange {
            val to = LocalDateTime.now()
            val from = to.minusMonths(3)
            return DateRange(from, to)
        }

        fun lastYear(): DateRange {
            val to = LocalDateTime.now()
            val from = to.minusYears(1)
            return DateRange(from, to)
        }

        fun allTime(): DateRange {
            val to = LocalDateTime.now()
            val from = LocalDateTime.of(2020, 1, 1, 0, 0)
            return DateRange(from, to)
        }
    }
}

@Serializable
data class FriendConfig(
    val friendId: String,
    val friendName: String,
    val color: Long
)

data class SetConfig(
    val setIndex: Int,
    val color: Long,
    val isVisible: Boolean
)

data class ChartTemplate(
    val id: Long? = null,
    val userId: String,
    val name: String,
    val type: ChartType,
    val dateRange: DateRange,
    val friendConfigs: List<FriendConfig> = emptyList(),
    val exerciseId: String? = null,
    val setConfigs: List<SetConfig> = emptyList()
)