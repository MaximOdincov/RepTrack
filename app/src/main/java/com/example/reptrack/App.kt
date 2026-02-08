package com.example.reptrack

import android.app.Application
import com.example.reptrack.di.appModule
import com.example.reptrack.di.authModule
import com.example.reptrack.di.databaseModule
import com.example.reptrack.di.profileModule
import com.example.reptrack.di.workoutModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.ERROR)
            modules(
                appModule,
                authModule,
                profileModule,
                workoutModule,
                databaseModule
            )
        }
    }
}