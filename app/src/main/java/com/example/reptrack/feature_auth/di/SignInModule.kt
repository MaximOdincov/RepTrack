package com.example.reptrack.feature_auth.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.feature_auth.domain.usecases.LoginAsGuestUseCase
import com.example.reptrack.feature_auth.domain.usecases.ResetPasswordUseCase
import com.example.reptrack.feature_auth.domain.usecases.SignInUseCase
import com.example.reptrack.feature_auth.domain.usecases.SignInWithGoogleUseCase
import com.example.reptrack.feature_auth.presentation.signIn.SignInStore
import com.example.reptrack.feature_auth.presentation.signIn.SignInStoreFactory
import org.koin.dsl.module

val signInModule = module {
    factory<SignInStore> {
        SignInStoreFactory(
            storeFactory = get<StoreFactory>(),
            signInUseCase = get(),
            loginAsGuestUseCase = get(),
            signInWithGoogleUseCase = get(),
            resetPasswordUseCase = get()
        ).create()
    }

    factory { SignInUseCase(repository = get(), addUserUseCase = get()) }
    factory { LoginAsGuestUseCase(repository = get(), addUserUseCase = get()) }
    factory { SignInWithGoogleUseCase(repository = get(), addUserUseCase = get()) }
    factory { ResetPasswordUseCase(get()) }
}

