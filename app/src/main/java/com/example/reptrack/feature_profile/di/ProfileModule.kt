package com.example.reptrack.feature_profile.di

import com.example.reptrack.feature_profile.data.ProfileRepositoryImpl
import com.example.reptrack.feature_profile.domain.ProfileRepository
import com.example.reptrack.feature_profile.domain.usecases.AddUserUseCase
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
}
