package com.example.reptrack

import android.app.Application
import com.example.reptrack.core.di.appModule
import com.example.reptrack.core.di.databaseModule
import com.example.reptrack.feature_auth.di.authModule
import com.example.reptrack.feature_auth.di.signInModule
import com.example.reptrack.feature_auth.di.signUpModule
import com.example.reptrack.feature_auth.di.splashModule
import com.example.reptrack.feature_profile.di.profileModule
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
                databaseModule
            )
        }
    }
}