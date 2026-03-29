package com.example.reptrack.domain.auth

data class AuthUser(
    val id: String,
    val isGuest: Boolean,
    val email: String?
)

