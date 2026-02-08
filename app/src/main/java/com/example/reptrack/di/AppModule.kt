package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import org.koin.dsl.module

val appModule = module{
    single<StoreFactory> {
        DefaultStoreFactory()
    }
}
