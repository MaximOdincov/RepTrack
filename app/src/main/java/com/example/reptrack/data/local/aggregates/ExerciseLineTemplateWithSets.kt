package com.example.reptrack.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Relation
import com.example.reptrack.data.local.models.statistics.ExerciseLineConfigDb
import com.example.reptrack.data.local.models.statistics.SetConfigDb

data class ExerciseLineTemplateWithSets(
    @Embedded val exerciseConfig: ExerciseLineConfigDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseConfigId"
    )
    val setConfigs: List<SetConfigDb>
)