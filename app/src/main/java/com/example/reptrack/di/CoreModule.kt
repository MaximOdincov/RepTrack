package com.example.reptrack.di

import com.example.reptrack.core.error.CrashlyticsManager
import com.example.reptrack.core.error.CrashlyticsManagerImpl
import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.ErrorHandlerImpl
import com.example.reptrack.core.error.mappers.ErrorToMessageMapper
import com.example.reptrack.core.error.mappers.ErrorToMessageMapperImpl
import com.example.reptrack.core.util.BuildConfigUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.dsl.module

/**
 * Core module provides error handling infrastructure
 */
val coreModule = module {
    single<CrashlyticsManager> {
        CrashlyticsManagerImpl(
            firebaseCrashlytics = get(),
            isEnabled = !BuildConfigUtils.isDebug  // Disable in debug builds
        )
    }

    single<ErrorToMessageMapper> {
        ErrorToMessageMapperImpl()
    }

    single<ErrorHandler> {
        ErrorHandlerImpl(
            crashlyticsManager = get(),
            errorToMessageMapper = get(),
            isDebug = BuildConfigUtils.isDebug
        )
    }

    // Firebase Crashlytics instance (provided by Firebase BoM)
    single<FirebaseCrashlytics> {
        FirebaseCrashlytics.getInstance()
    }
}
