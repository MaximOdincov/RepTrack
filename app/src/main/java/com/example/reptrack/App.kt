package com.example.reptrack

import android.app.Application
import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.di.appModule
import com.example.reptrack.di.authModule
import com.example.reptrack.di.coreModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {

    val errorHandler: ErrorHandler by inject()

    override fun onCreate() {
        super.onCreate()

        // Initialize Napier for logging
        Napier.base(DebugAntilog())

        // LeakCanary 2.x automatically initializes via ContentProvider
        // No manual initialization needed

        startKoin {
            androidContext(this@App)
            androidLogger(Level.ERROR)
            modules(
                appModule,
                coreModule,  // Core module with error handling
                authModule  // Only auth modules at startup
            )
        }

        // Setup global exception handler for uncaught exceptions
        // IMPORTANT: Save the ORIGINAL handler BEFORE setting ours
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                errorHandler.handle(
                    throwable,
                    ErrorContext(screen = "Global", action = "UncaughtException")
                )
            } catch (e: Exception) {
                // Don't let errorHandler crashes cause infinite loop
                try {
                    Napier.e("ErrorHandler crashed in uncaught handler: ${e.message}", throwable = e)
                } catch (ignored: Exception) {
                    // Last resort - ignore if even logging fails
                }
            }

            // Forward to ORIGINAL handler (not the current one to prevent infinite loop)
            try {
                originalHandler?.uncaughtException(thread, throwable)
            } catch (e: Exception) {
                // If original handler crashes, just exit
                System.exit(1)
            }
        }
    }

    companion object {
        private val authenticatedModules = listOf(
            com.example.reptrack.di.profileModule,
            com.example.reptrack.di.workoutModule,
            com.example.reptrack.di.exerciseModule,
            com.example.reptrack.di.databaseModule
        )

        /**
         * Load modules that require database (called after user is authenticated)
         */
        fun loadAuthenticatedModules() {
            org.koin.core.context.GlobalContext.get().loadModules(authenticatedModules)
        }

        /**
         * Unload authenticated modules (called before sign-out)
         */
        fun unloadAuthenticatedModules() {
            org.koin.core.context.GlobalContext.get().unloadModules(authenticatedModules)
        }
    }
}