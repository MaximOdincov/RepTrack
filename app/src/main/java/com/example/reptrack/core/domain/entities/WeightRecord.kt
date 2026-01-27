package com.example.reptrack.core.domain.entities

import java.time.LocalDateTime

data class WeightRecord(
    val id: String,
    val date: LocalDateTime,
    val value: Float
)