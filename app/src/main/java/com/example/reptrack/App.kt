package com.example.reptrack

import android.app.Application
import com.example.reptrack.di.appModule
import com.example.reptrack.di.authModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.ERROR)
            modules(
                appModule,
                authModule  // Only auth modules at startup
            )
        }
    }

    companion object {
        private val authenticatedModules = listOf(
            com.example.reptrack.di.profileModule,
            com.example.reptrack.di.workoutModule,
            com.example.reptrack.di.exerciseModule,
            com.example.reptrack.di.databaseModule
        )

        // Load modules that require database (called after user is authenticated)
        fun loadAuthenticatedModules() {
            org.koin.core.context.GlobalContext.get().loadModules(authenticatedModules)
        }

        // Unload authenticated modules (called before sign-out)
        fun unloadAuthenticatedModules() {
            org.koin.core.context.GlobalContext.get().unloadModules(authenticatedModules)
        }
    }
}