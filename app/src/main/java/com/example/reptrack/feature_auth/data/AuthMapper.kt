package com.example.reptrack.feature_auth.data

import com.example.reptrack.core.domain.entities.User
import com.example.reptrack.feature_auth.domain.AuthUser
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthUser(): AuthUser {
    return AuthUser(
        id = uid,
        email = email,
        isGuest = isAnonymous
    )
}

fun AuthUser.toDomain(): User {
    return User(
        id = id,
        email = email,
        isGuest = isGuest
    )
}