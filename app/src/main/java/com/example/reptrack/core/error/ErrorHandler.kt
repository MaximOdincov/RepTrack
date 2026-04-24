package com.example.reptrack.core.error

import com.example.reptrack.core.error.exceptions.AppException
import com.example.reptrack.core.error.mappers.ErrorToMessageMapper
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.toAppException
import io.github.aakira.napier.Napier
import java.util.concurrent.ConcurrentHashMap

/**
 * Result of handling an error
 */
data class HandledError(
    val userMessage: String,
    val technicalMessage: String,
    val logLevel: AppException.LogLevel
)

/**
 * Central error handling service that:
 * - Logs all errors to Napier with appropriate levels
 * - Reports critical errors to Crashlytics
 * - Provides user-friendly error messages
 * - Tracks error context for debugging
 * - Prevents spam from repeated errors
 */
interface ErrorHandler {
    /**
     * Handles an exception:
     * 1. Converts to AppException if needed
     * 2. Logs to Napier
     * 3. Reports to Crashlytics (if critical)
     * 4. Returns user-friendly error info
     */
    fun handle(throwable: Throwable, context: ErrorContext? = null): HandledError

    /**
     * Logs an exception without reporting to Crashlytics (useful for silent errors)
     */
    fun log(throwable: Throwable, context: ErrorContext? = null)

    /**
     * Sets the user ID for error tracking
     */
    fun setUserId(userId: String?)

    /**
     * Sets a custom key-value pair for error context
     */
    fun setCustomKey(key: String, value: String)

    /**
     * Clears the error spam counter (useful for testing)
     */
    fun clearErrorHistory()
}

/**
 * Default implementation of ErrorHandler with spam protection
 */
class ErrorHandlerImpl(
    private val crashlyticsManager: CrashlyticsManager,
    private val errorToMessageMapper: ErrorToMessageMapper,
    private val isDebug: Boolean
) : ErrorHandler {

    // Track recent errors to prevent spam: Error signature -> (count, last logged timestamp)
    private val errorHistory = ConcurrentHashMap<String, ErrorRecord>()

    // Minimum time between logging the same error (milliseconds)
    private val LOG_COOLDOWN = 1000L // 1 second

    // Maximum times to log the same error before silencing
    private val MAX_REPEAT_LOGS = 3

    private data class ErrorRecord(
        var count: Int,
        var lastLoggedTime: Long
    )

    override fun handle(throwable: Throwable, context: ErrorContext?): HandledError {
        return try {
            // Convert to AppException for consistent handling
            val appException = throwable.toAppException()

            // Check if we should log this error (spam protection)
            if (shouldLogError(throwable, context)) {
                // 1. Log to Napier with appropriate level
                logToNapier(throwable, appException.logLevel, context)

                // 2. Report to Crashlytics if needed and not in debug mode
                if (appException.reportToCrashlytics && !isDebug) {
                    try {
                        crashlyticsManager.recordException(throwable, context)
                    } catch (e: Exception) {
                        // Don't let crashlytics crashes break error handling
                        Napier.e("Crashlytics recording failed: ${e.message}", tag = "ErrorHandler")
                    }
                }
            }

            // 3. Return user-friendly error info
            HandledError(
                userMessage = try {
                    appException.userMessage ?: errorToMessageMapper.getUserMessage(throwable)
                } catch (e: Exception) {
                    "Error occurred"
                },
                technicalMessage = throwable.message ?: "Unknown error",
                logLevel = appException.logLevel
            )
        } catch (e: Exception) {
            // Last resort - if everything fails, just log and return minimal info
            Napier.e("ErrorHandler.handle() crashed: ${e.message}", throwable = e, tag = "ErrorHandler")
            HandledError(
                userMessage = "An error occurred",
                technicalMessage = throwable.message ?: "Unknown error",
                logLevel = AppException.LogLevel.ERROR
            )
        }
    }

    override fun log(throwable: Throwable, context: ErrorContext?) {
        try {
            val appException = throwable.toAppException()

            // Check if we should log this error (spam protection)
            if (shouldLogError(throwable, context)) {
                // Only log, don't report to Crashlytics
                logToNapier(throwable, appException.logLevel, context)
            }
        } catch (e: Exception) {
            // Last resort - basic logging
            Napier.e("ErrorHandler.log() crashed: ${e.message} | Original error: ${throwable.message}", tag = "ErrorHandler")
        }
    }

    override fun setUserId(userId: String?) {
        crashlyticsManager.setUserId(userId)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlyticsManager.setCustomKey(key, value)
    }

    override fun clearErrorHistory() {
        errorHistory.clear()
    }

    /**
     * Determines if an error should be logged based on recent history
     * Returns true if the error should be logged, false if it's spam
     */
    @Synchronized
    private fun shouldLogError(throwable: Throwable, context: ErrorContext?): Boolean {
        val errorSignature = generateErrorSignature(throwable, context)
        val now = System.currentTimeMillis()

        val existing = errorHistory[errorSignature]
        val record = if (existing == null) {
            ErrorRecord(count = 1, lastLoggedTime = now).also {
                errorHistory[errorSignature] = it
            }
        } else {
            val timeSinceLastLog = now - existing.lastLoggedTime
            if (timeSinceLastLog > LOG_COOLDOWN) {
                // Enough time passed, reset counter
                ErrorRecord(count = 1, lastLoggedTime = now).also {
                    errorHistory[errorSignature] = it
                }
            } else {
                // Same error within cooldown, increment counter
                existing.copy(count = existing.count + 1).also {
                    errorHistory[errorSignature] = it
                }
            }
        }

        val shouldLog = record.count <= MAX_REPEAT_LOGS

        if (!shouldLog && record.count == MAX_REPEAT_LOGS + 1) {
            // Log once that we're silencing this error
            Napier.w(
                "[SPAM PROTECTION] Silencing repeated error: ${throwable.javaClass.simpleName} - " +
                        "has been logged ${record.count} times in quick succession",
                tag = "ErrorHandler"
            )
        }

        return shouldLog
    }

    /**
     * Generates a unique signature for an error to detect duplicates
     */
    private fun generateErrorSignature(throwable: Throwable, context: ErrorContext?): String {
        val stackTraceSig = try {
            throwable.stackTrace?.take(5)?.joinToString("/") { it.className + "." + it.methodName } ?: "no_stack_trace"
        } catch (e: Exception) {
            "error_generating_signature"
        }
        val throwableSig = "${throwable.javaClass.name}:$stackTraceSig"
        val contextSig = try {
            context?.toLogString() ?: "no_context"
        } catch (e: Exception) {
            "error_generating_context"
        }
        return "$throwableSig@$contextSig"
    }

    private fun logToNapier(
        throwable: Throwable,
        logLevel: AppException.LogLevel,
        context: ErrorContext?
    ) {
        try {
            val tag = "ErrorHandler"
            val contextStr = try {
                context?.toLogString()?.let { "[$it] " } ?: ""
            } catch (e: Exception) {
                "[error_context] "
            }
            val message = try {
                "${contextStr}${throwable.message ?: throwable.javaClass.simpleName}"
            } catch (e: Exception) {
                "Error generating log message"
            }

            when (logLevel) {
                AppException.LogLevel.ERROR -> Napier.e(message, throwable, tag)
                AppException.LogLevel.WARN -> Napier.w(message, throwable, tag)
                AppException.LogLevel.INFO -> Napier.i(message, throwable, tag)
                AppException.LogLevel.DEBUG -> Napier.d(message, throwable, tag)
                AppException.LogLevel.VERBOSE -> Napier.v(message, throwable, tag)
            }

            // Log context separately if present
            if (context != null) {
                try {
                    Napier.i("Error context: ${context.toLogString()}", tag = tag)
                } catch (e: Exception) {
                    // Skip context logging if it fails
                }
            }
        } catch (e: Exception) {
            // Absolute last resort
            Napier.e("Critical: logToNapier failed: ${e.message}", tag = "ErrorHandler")
        }
    }
}
