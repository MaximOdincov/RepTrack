package com.example.reptrack.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Relation
import com.example.reptrack.data.local.models.statistics.ChartTemplateDb
import com.example.reptrack.data.local.models.statistics.ExerciseLineConfigDb
import com.example.reptrack.data.local.models.statistics.FriendConfigDb
import com.example.reptrack.data.local.models.statistics.SetConfigDb

data class ExerciseLineTemplate(
    @Embedded val template: ChartTemplateDb,

    @Relation(
        parentColumn = "id",
        entityColumn = "templateId"
    )
    val friendConfigs: List<FriendConfigDb>,

    @Relation(
        parentColumn = "id",
        entityColumn = "templateId",
        entity = ExerciseLineConfigDb::class
    )
    val exerciseConfigs: List<ExerciseLineTemplateWithSets>
)