package com.example.reptrack.data.local.models.statistics

import java.time.LocalDateTime

data class ExerciseProgressData(
    val date: LocalDateTime,
    val weight: Float?,
    val setOrder: Int,
    val exerciseName: String
)