package com.example.reptrack.data.auth

import com.example.reptrack.domain.profile.User
import com.example.reptrack.domain.auth.AuthUser
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthUser(): AuthUser {
    android.util.Log.d("AuthMapper", "[Google] toAuthUser: uid=$uid, email=$email, isAnonymous=$isAnonymous")
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