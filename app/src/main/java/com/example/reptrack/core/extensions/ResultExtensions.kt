package com.example.reptrack.core.extensions

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.HandledError
import com.example.reptrack.core.error.model.ErrorContext

/**
 * Extension functions for Result<T> to provide ergonomic error handling
 */

/**
 * Handles error if Result is a failure.
 * Logs the error and optionally executes an action with the handled error.
 *
 * @param errorHandler The error handler to use
 * @param context Optional error context for better tracking
 * @param action Optional action to execute with the handled error (e.g., show toast)
 * @return The original Result (unchanged)
 */
inline fun <T> Result<T>.onError(
    errorHandler: ErrorHandler,
    context: ErrorContext? = null,
    action: (HandledError) -> Unit = {}
): Result<T> {
    if (isFailure) {
        val throwable = exceptionOrNull()!!
        val handledError = errorHandler.handle(throwable, context)
        action(handledError)
    }
    return this
}

/**
 * Logs error (without Crashlytics) if Result is a failure.
 * Use this for "silent" errors that should be logged but not shown to user.
 *
 * @param errorHandler The error handler to use
 * @param context Optional error context for better tracking
 * @return The original Result (unchanged)
 */
suspend fun <T> Result<T>.logOnFailure(
    errorHandler: ErrorHandler,
    context: ErrorContext? = null
): Result<T> {
    if (isFailure) {
        errorHandler.log(exceptionOrNull()!!, context)
    }
    return this
}

/**
 * Peeks at the error if Result is a failure, without logging.
 * Use this when you want to handle the error yourself.
 *
 * @param action Action to execute with the throwable
 * @return The original Result (unchanged)
 */
inline fun <T> Result<T>.peekOnFailure(
    action: (Throwable) -> Unit
): Result<T> {
    if (isFailure) {
        action(exceptionOrNull()!!)
    }
    return this
}

/**
 * Maps the failure to a different Result with a transformed error.
 *
 * @param transform Function to transform the throwable
 * @return A new Result with transformed error or original success
 */
inline fun <T> Result<T>.mapError(
    transform: (Throwable) -> Throwable
): Result<T> {
    return if (isFailure) {
        Result.failure(transform(exceptionOrNull()!!))
    } else {
        this
    }
}

/**
 * Recovers from a failure by applying a function to the throwable.
 *
 * @param transform Function that returns a new Result
 * @return The original Result if success, or the result of transform
 */
inline fun <T> Result<T>.recover(
    transform: (Throwable) -> Result<T>
): Result<T> {
    return if (isFailure) {
        transform(exceptionOrNull()!!)
    } else {
        this
    }
}

/**
 * Recovers from a failure by returning a fallback value.
 *
 * @param fallback Value to return if Result is a failure
 * @return The original value if success, or the fallback
 */
inline fun <T> Result<T>.getOrDefault(fallback: T): T {
    return getOrElse { fallback }
}

/**
 * Returns true if Result is a success, false otherwise.
 */
val <T> Result<T>.isSuccess: Boolean
    get() = isSuccess

/**
 * Returns true if Result is a failure, false otherwise.
 */
val <T> Result<T>.isFailure: Boolean
    get() = isFailure
