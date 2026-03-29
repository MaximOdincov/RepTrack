package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.models.GdprConsentDb
import com.example.reptrack.data.local.models.UserDb
import com.example.reptrack.data.local.aggregates.UserWithConsent
import com.example.reptrack.domain.profile.User
import com.example.reptrack.domain.profile.GdprConsent

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

fun UserWithConsent.toDomain(): User =
    User(
        id = user.id,
        isGuest = user.isGuest,
        username = user.username,
        email = user.email,
        avatarUrl = user.avatarUrl,
        currentWeight = user.currentWeight,
        height = user.height,
        gdprConsent = gdprConsent?.let {
            GdprConsent(
                isAccepted = it.isAccepted,
                acceptedAt = it.acceptedAt
            )
        }
    )
