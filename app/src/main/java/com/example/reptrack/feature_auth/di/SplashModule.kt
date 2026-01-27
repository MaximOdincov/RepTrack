package com.example.reptrack.feature_auth.di

import com.example.reptrack.feature_auth.domain.usecases.GetCurrentUserUseCase
import com.example.reptrack.feature_auth.domain.usecases.LoginAsGuestUseCase
import com.example.reptrack.feature_auth.domain.usecases.SignInUseCase
import com.example.reptrack.feature_auth.presentation.splash.SplashStore
import com.example.reptrack.feature_auth.presentation.splash.SplashStoreFactory
import com.example.reptrack.navigation.Screen
import org.koin.dsl.module

val splashModule = module {
    factory<SplashStore> {
        SplashStoreFactory(
            storeFactory = get(),
            getCurrentUserUseCase = get()
        ).create()
    }
    factory { GetCurrentUserUseCase(repository = get())}
}