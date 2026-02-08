package com.example.reptrack.di

import com.example.reptrack.data.auth.AuthRepositoryImpl
import com.example.reptrack.data.auth.FirebaseAuthDataSource
import com.example.reptrack.domain.auth.AuthRepository
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
