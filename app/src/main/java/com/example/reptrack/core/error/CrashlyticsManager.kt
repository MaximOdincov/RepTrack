package com.example.reptrack.core.error

import com.example.reptrack.core.error.model.ErrorContext
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Abstraction over Firebase Crashlytics to allow:
 * - Easy testing with mocks
 * - Disabling in debug builds
 * - Consistent context tracking
 */
interface CrashlyticsManager {
    /**
     * Records an exception to Crashlytics with optional context
     */
    fun recordException(throwable: Throwable, context: ErrorContext? = null)

    /**
     * Sets the user ID for all subsequent crash reports
     */
    fun setUserId(userId: String?)

    /**
     * Sets a custom key-value pair for the current crash report
     */
    fun setCustomKey(key: String, value: String)

    /**
     * Logs a message to Crashlytics (visible in crash reports)
     */
    fun log(message: String)

    /**
     * Enables or disables Crashlytics collection
     */
    fun setEnabled(enabled: Boolean)
}

/**
 * Firebase implementation of CrashlyticsManager
 */
class CrashlyticsManagerImpl(
    private val firebaseCrashlytics: FirebaseCrashlytics,
    isEnabled: Boolean
) : CrashlyticsManager {

    init {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(isEnabled)
    }

    override fun recordException(throwable: Throwable, context: ErrorContext?) {
        // Add context as custom keys
        context?.screen?.let { firebaseCrashlytics.setCustomKey("screen", it) }
        context?.action?.let { firebaseCrashlytics.setCustomKey("action", it) }
        context?.entityId?.let { firebaseCrashlytics.setCustomKey("entityId", it) }

        // Log additional context info
        if (context?.additionalInfo?.isNotEmpty() == true) {
            context.additionalInfo.forEach { (key, value) ->
                firebaseCrashlytics.setCustomKey("info_$key", value.toString())
            }
        }

        // Log exception type
        firebaseCrashlytics.log("Exception: ${throwable.javaClass.simpleName}")

        // Record the exception
        firebaseCrashlytics.recordException(throwable)
    }

    override fun setUserId(userId: String?) {
        firebaseCrashlytics.setUserId(userId ?: "")
    }

    override fun setCustomKey(key: String, value: String) {
        firebaseCrashlytics.setCustomKey(key, value)
    }

    override fun log(message: String) {
        firebaseCrashlytics.log(message)
    }

    override fun setEnabled(enabled: Boolean) {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(enabled)
    }
}
