package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.data.profile.ProfileRepositoryImpl
import com.example.reptrack.domain.profile.ProfileRepository
import com.example.reptrack.domain.profile.usecases.AddUserUseCase
import com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase
import com.example.reptrack.presentation.profile.stores.ProfileStore
import com.example.reptrack.presentation.profile.stores.ProfileStoreFactory
import org.koin.dsl.module

val profileModule = module {

    single<ProfileRepository> {
        ProfileRepositoryImpl(
            userDao = get()
        )
    }

    factory {
        AddUserUseCase(
            repository = get()
        )
    }

    factory {
        GetCurrentUserProfileUseCase(
            authRepository = get(),
            profileRepository = get()
        )
    }

    factory<ProfileStoreFactory> {
        ProfileStoreFactory(
            storeFactory = get(),
            getCurrentUserProfileUseCase = get(),
            signOutUseCase = get()
        )
    }

    factory<ProfileStore> {
        get<ProfileStoreFactory>().create()
    }
}
