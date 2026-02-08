package com.example.reptrack.domain.auth

import com.example.reptrack.domain.workout.GdprConsent

data class AuthUser(
    val id: String,
    val isGuest: Boolean,
    val email: String?
)

