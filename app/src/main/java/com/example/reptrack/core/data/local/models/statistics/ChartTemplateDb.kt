package com.example.reptrack.core.data.local.models.statistics

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.reptrack.core.data.local.models.UserDb
import java.time.LocalDateTime

@Entity(
    tableName = "chart_templates",
    foreignKeys = [ForeignKey(
        entity = UserDb::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class ChartTemplateDb(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val name: String,
    val type: String,
    val dateFrom: Long,
    val dateTo: Long,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
