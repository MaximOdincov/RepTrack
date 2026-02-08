package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.domain.auth.usecases.LoginAsGuestUseCase
import com.example.reptrack.domain.auth.usecases.SignInUseCase
import com.example.reptrack.domain.auth.usecases.SignUpUseCase
import com.example.reptrack.presentation.auth.signUp.SignUpStore
import com.example.reptrack.presentation.auth.signUp.SignUpStoreFactory
import org.koin.dsl.module

val signUpModule = module {

    factory<SignUpStore> {
        SignUpStoreFactory(
            storeFactory = get<StoreFactory>(),
            signUpUseCase = get()
        ).create()
    }

    factory { SignUpUseCase(repository = get(), addUserUseCase = get()) }
}
