package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.models.GdprConsentDb
import com.example.reptrack.data.local.models.UserDb
import com.example.reptrack.domain.workout.User

fun User.toDb(): UserDb =
    UserDb(
        id = id,
        isGuest = isGuest,
        username = username,
        email = email,
        avatarUrl = avatarUrl,
        currentWeight = currentWeight,
        height = height
    )

fun User.toGdprDb(): GdprConsentDb? =
    gdprConsent?.let {
        GdprConsentDb(
            userId = id,
            isAccepted = it.isAccepted,
            acceptedAt = it.acceptedAt
        )
    }
