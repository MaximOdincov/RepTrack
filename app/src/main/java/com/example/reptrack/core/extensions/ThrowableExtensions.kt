package com.example.reptrack.core.extensions

import com.example.reptrack.core.error.exceptions.*
import java.sql.SQLException

/**
 * Extension function to convert any Throwable to AppException
 * This ensures consistent error handling throughout the app
 */
fun Throwable.toAppException(): AppException {
    // If already an AppException, return as-is
    if (this is AppException) {
        return this
    }

    // Map standard exceptions to AppException types
    return when (this) {
        // Database errors
        is SQLException -> DataException.DatabaseError(
            operation = "database_operation",
            cause = this
        )

        // IO errors (file system, network, etc.)
        is java.io.IOException -> NetworkException.NoConnection(
            cause = this
        )

        // Collection/not found errors
        is NoSuchElementException -> DomainException.EntityNotFound(
            entityType = "Unknown",
            entityId = "unknown",
            cause = this
        )

        // Validation errors
        is IllegalArgumentException -> DomainException.ValidationError(
            field = "unknown",
            reason = message ?: "Invalid argument",
            cause = this
        )

        // State errors
        is IllegalStateException -> DomainException.InvalidStateError(
            currentState = "unknown",
            expectedState = "valid",
            cause = this
        )

        // Null pointer errors
        is NullPointerException -> DataException.DataAccessError(
            message = "Null reference encountered: ${message ?: "unknown"}",
            cause = this
        )

        // Index out of bounds
        is IndexOutOfBoundsException -> DataException.DataAccessError(
            message = "Index out of bounds: ${message ?: "unknown"}",
            cause = this
        )

        // Retrofit HTTP errors - not used in this project
        // is retrofit2.HttpException -> NetworkException.ServerError(
        //     code = this.code(),
        //     message = this.message()
        // )

        // Timeout errors (OkHttp, Retrofit, etc.)
        is java.util.concurrent.TimeoutException -> NetworkException.Timeout(
            timeoutMillis = -1L, // Unknown
            cause = this
        )

        // Fallback for unknown exceptions
        else -> UnknownException(
            message = this.message ?: this.javaClass.simpleName,
            cause = this
        )
    }
}
