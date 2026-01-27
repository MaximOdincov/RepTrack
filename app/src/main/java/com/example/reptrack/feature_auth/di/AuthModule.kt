package com.example.reptrack.feature_auth.di

import com.example.reptrack.feature_auth.data.AuthRepositoryImpl
import com.example.reptrack.feature_auth.data.FirebaseAuthDataSource
import com.example.reptrack.feature_auth.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val authModule = module {
    includes(
        signInModule,
        signUpModule,
        splashModule
    )

    single { FirebaseAuth.getInstance() }

    single {
        FirebaseAuthDataSource(
            firebaseAuth = get()
        )
    }

    single<AuthRepository> { AuthRepositoryImpl(get()) }

}