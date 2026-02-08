package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.domain.auth.usecases.LoginAsGuestUseCase
import com.example.reptrack.domain.auth.usecases.ResetPasswordUseCase
import com.example.reptrack.domain.auth.usecases.SignInUseCase
import com.example.reptrack.domain.auth.usecases.SignInWithGoogleUseCase
import com.example.reptrack.presentation.auth.signIn.SignInStore
import com.example.reptrack.presentation.auth.signIn.SignInStoreFactory
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

