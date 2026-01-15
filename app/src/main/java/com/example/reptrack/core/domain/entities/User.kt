package com.example.reptrack.core.domain.entities
data class User(
    val id: String,
    val isGuest: Boolean = true,
    val username: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val currentWeight: Float? = null,
    val height: Float? = null,
    val gdprConsent: GdprConsent? = null
)
