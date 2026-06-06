package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.data.profile.ProfileRepositoryImpl
import com.example.reptrack.domain.profile.ProfileRepository
import com.example.reptrack.domain.profile.usecases.AddUserUseCase
import com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase
import com.example.reptrack.domain.profile.usecases.InitializeDatabaseUseCase
import com.example.reptrack.domain.profile.usecases.UpdateUsernameUseCase
import com.example.reptrack.domain.profile.usecases.UpdatePasskeyUseCase
import com.example.reptrack.domain.backup.SyncUseCase
import com.example.reptrack.presentation.profile.stores.ProfileStore
import com.example.reptrack.presentation.profile.stores.ProfileStoreFactory
import com.example.reptrack.presentation.profile.stores.FriendsStore
import com.example.reptrack.presentation.profile.stores.FriendsStoreFactory
import org.koin.android.ext.koin.androidContext
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

    factory {
        InitializeDatabaseUseCase(
            context = androidContext(),
            database = get()
        )
    }

    factory {
        UpdateUsernameUseCase(
            repository = get()
        )
    }

    factory {
        UpdatePasskeyUseCase(
            repository = get()
        )
    }


    factory<ProfileStoreFactory> {
        ProfileStoreFactory(
            storeFactory = get(),
            getCurrentUserProfileUseCase = get(),
            signOutUseCase = get(),
            syncUseCase = get(),
            updateUsernameUseCase = get(),
            updatePasskeyUseCase = get()
        )
    }

    factory<ProfileStore> {
        get<ProfileStoreFactory>().create()
    }

    
    factory<FriendsStoreFactory> {
        FriendsStoreFactory(
            storeFactory = get(),
            getFriendsUseCase = get(),
            addFriendUseCase = get(),
            deleteFriendUseCase = get()
        )
    }

    factory<FriendsStore> {
        get<FriendsStoreFactory>().create()
    }
}
