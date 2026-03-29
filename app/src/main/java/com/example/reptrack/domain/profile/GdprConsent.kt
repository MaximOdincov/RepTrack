package com.example.reptrack.domain.profile

data class GdprConsent(
    val isAccepted: Boolean,
    val acceptedAt: Long
)
