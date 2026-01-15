package com.example.reptrack.core.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Relation
import com.example.reptrack.core.data.local.models.GdprConsentDb
import com.example.reptrack.core.data.local.models.UserDb

data class UserWithConsent(
    @Embedded val user: UserDb,

    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val gdprConsent: GdprConsentDb?
)
