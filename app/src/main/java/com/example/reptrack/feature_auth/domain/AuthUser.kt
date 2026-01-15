package com.example.reptrack.feature_auth.domain

import com.example.reptrack.core.domain.entities.GdprConsent

data class AuthUser(
    val id: String,
    val isGuest: Boolean,
    val email: String?
)

