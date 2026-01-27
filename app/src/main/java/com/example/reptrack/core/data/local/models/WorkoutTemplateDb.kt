package com.example.reptrack.core.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "workout_templates")
data class WorkoutTemplateDb(
    @PrimaryKey val id: String,
    val name: String,
    val iconId: String?,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
