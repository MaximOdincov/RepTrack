package com.example.reptrack.domain.workout.statistics

data class ChartTemplate(
    val id: Long,
    val name: String,
    val type: ChartType,
    val dateFrom: Long,
    val dateTo: Long,
    val friendConfigs: List<FriendConfig>,
    val specificConfig: SpecificConfig? = null
)