package com.example.reptrack.core.domain.entities

data class GdprConsent(
    val isAccepted: Boolean,
    val acceptedAt: Long
)
