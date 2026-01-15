package com.example.reptrack.core.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "weight_records")
data class WeightRecordDb(
    @PrimaryKey val id: String,
    val date: LocalDateTime,
    val value: Float,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
