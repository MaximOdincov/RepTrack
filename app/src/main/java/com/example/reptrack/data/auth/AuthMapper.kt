package com.example.reptrack.data.auth

import com.example.reptrack.domain.workout.User
import com.example.reptrack.domain.auth.AuthUser
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