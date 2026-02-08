package com.example.reptrack.di

import com.example.reptrack.data.profile.ProfileRepositoryImpl
import com.example.reptrack.domain.profile.ProfileRepository
import com.example.reptrack.domain.profile.usecases.AddUserUseCase
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
