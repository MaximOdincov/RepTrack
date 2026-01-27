package com.example.reptrack.feature_auth.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.feature_auth.domain.usecases.LoginAsGuestUseCase
import com.example.reptrack.feature_auth.domain.usecases.SignInUseCase
import com.example.reptrack.feature_auth.domain.usecases.SignUpUseCase
import com.example.reptrack.feature_auth.presentation.signUp.SignUpStore
import com.example.reptrack.feature_auth.presentation.signUp.SignUpStoreFactory
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
