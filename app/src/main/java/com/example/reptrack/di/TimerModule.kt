package com.example.reptrack.di

import android.content.Context
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.reptrack.presentation.timer.stores.TimerStore
import com.example.reptrack.presentation.timer.stores.TimerStoreFactory
import com.example.reptrack.service.sound.TimerSoundManager
import com.example.reptrack.service.timer.TimerNotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val timerModule = module {

    factory {
        TimerStoreFactory(
            storeFactory = get(),
            timerSoundManager = get()
        )
    }

    factory<TimerStore> { get<TimerStoreFactory>().create() }

    single<TimerSoundManager> {
        TimerSoundManager(androidContext())
    }

    single<TimerNotificationManager> {
        TimerNotificationManager(androidContext())
    }
}