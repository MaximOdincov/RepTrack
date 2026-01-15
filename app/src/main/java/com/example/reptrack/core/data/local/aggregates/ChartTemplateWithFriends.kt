package com.example.reptrack.core.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Relation
import com.example.reptrack.core.data.local.models.statistics.ChartTemplateDb
import com.example.reptrack.core.data.local.models.statistics.FriendConfigDb

data class ChartTemplateWithFriends(
    @Embedded val template: ChartTemplateDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "templateId"
    )
    val friendConfigs: List<FriendConfigDb> = emptyList()
)