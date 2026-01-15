package com.example.reptrack.core.data.local.models.statistics

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "exercise_line_configs",
    foreignKeys = [ForeignKey(
        entity = ChartTemplateDb::class,
        parentColumns = ["id"], childColumns = ["templateId"]
    )]
)
data class ExerciseLineConfigDb(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long,
    val exerciseId: String,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)