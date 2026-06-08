package com.example.reptrack.data.local.models.statistics

import java.time.LocalDateTime

data class BestSetData(
    val date: LocalDateTime,
    val bestWeight: Float,
    val reps: Int
)