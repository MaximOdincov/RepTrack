package com.example.reptrack.domain.workout

import java.time.LocalDateTime

data class WeightRecord(
    val id: String,
    val date: LocalDateTime,
    val value: Float
)