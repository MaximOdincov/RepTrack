package com.example.reptrack.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Relation
import com.example.reptrack.data.local.models.GdprConsentDb
import com.example.reptrack.data.local.models.UserDb

data class UserWithConsent(
    @Embedded val user: UserDb,

    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val gdprConsent: GdprConsentDb?
)
