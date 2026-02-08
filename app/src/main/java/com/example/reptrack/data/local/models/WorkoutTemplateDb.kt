package com.example.reptrack.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "workout_templates")
data class WorkoutTemplateDb(
    @PrimaryKey val id: String,
    val name: String,
    val iconId: String?,
    val week1Days: String?,  // JSON строка с набором дней для недели 1
    val week2Days: String?,  // JSON строка с набором дней для недели 2
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
