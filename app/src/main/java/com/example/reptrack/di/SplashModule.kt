package com.example.reptrack.di

import com.example.reptrack.domain.auth.usecases.GetCurrentUserUseCase
import com.example.reptrack.presentation.auth.splash.SplashStore
import com.example.reptrack.presentation.auth.splash.SplashStoreFactory
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
