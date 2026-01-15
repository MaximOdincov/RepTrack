package com.example.reptrack.core.data.local.models.statistics

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "set_configs",
    foreignKeys = [ForeignKey(
        entity = ExerciseLineConfigDb::class,
        parentColumns = ["id"], childColumns = ["exerciseConfigId"]
    )]
)
data class SetConfigDb(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseConfigId: Long,
    val setIndex: Int,
    val color: Long,
    val visible: Boolean,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)