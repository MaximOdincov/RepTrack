package com.example.reptrack.core.extensions

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.emitAll

/**
 * Extension functions for Flow to provide ergonomic error handling
 */

/**
 * Catches exceptions in the flow, handles them with ErrorHandler, and emits empty flow.
 * Use this when errors should be logged but not break the flow.
 *
 * @param errorHandler The error handler to use
 * @param context Optional error context for better tracking
 * @return A Flow that catches errors and completes successfully
 */
fun <T> Flow<T>.catchAndHandle(
    errorHandler: ErrorHandler,
    context: ErrorContext? = null
): Flow<T> = catch { throwable ->
    errorHandler.handle(throwable, context)
    // Emit empty flow after error
    emitAll(emptyFlow<T>())
}

/**
 * Catches exceptions in the flow, logs them (without Crashlytics), and emits empty flow.
 * Use this for "silent" errors that should be logged but not reported.
 *
 * @param errorHandler The error handler to use
 * @param context Optional error context for better tracking
 * @return A Flow that catches errors and completes successfully
 */
fun <T> Flow<T>.catchAndLog(
    errorHandler: ErrorHandler,
    context: ErrorContext? = null
): Flow<T> = catch { throwable ->
    errorHandler.log(throwable, context)
    // Emit empty flow after error
    emitAll(emptyFlow<T>())
}

/**
 * Catches exceptions in the flow and emits a default value.
 * Use this when you want to provide a fallback value on error.
 *
 * @param errorHandler The error handler to use
 * @param context Optional error context for better tracking
 * @param defaultValue The value to emit on error
 * @return A Flow that emits defaultValue on error
 */
fun <T> Flow<T>.catchAndDefault(
    errorHandler: ErrorHandler,
    context: ErrorContext? = null,
    defaultValue: T
): Flow<T> = catch { throwable ->
    errorHandler.handle(throwable, context)
    // Emit default value
    emit(defaultValue)
}

/**
 * Safely collects from a flow with error handling.
 * Catches exceptions, handles them, and continues.
 *
 * @param errorHandler The error handler to use
 * @param context Optional error context for better tracking
 * @param onEach Action to execute for each emitted value
 */
suspend fun <T> Flow<T>.safeCollect(
    errorHandler: ErrorHandler,
    context: ErrorContext? = null,
    onEach: (T) -> Unit
) = catch { throwable ->
    errorHandler.handle(throwable, context)
}.collect { value ->
    value?.let(onEach)
}

/**
 * Safely collects from a flow with silent error logging only.
 * Catches exceptions, logs them (without Crashlytics), and continues.
 *
 * @param errorHandler The error handler to use
 * @param context Optional error context for better tracking
 * @param onEach Action to execute for each emitted value
 */
suspend fun <T> Flow<T>.safeCollectSilent(
    errorHandler: ErrorHandler,
    context: ErrorContext? = null,
    onEach: (T) -> Unit
) = catch { throwable ->
    errorHandler.log(throwable, context)
}.collect { value ->
    value?.let(onEach)
}
