package com.example.reptrack.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.reptrack.data.preferences.SavedFriendsManager
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val appModule = module{
    single<StoreFactory> {
        DefaultStoreFactory()
    }

    single { SavedFriendsManager(androidContext()) }
}
