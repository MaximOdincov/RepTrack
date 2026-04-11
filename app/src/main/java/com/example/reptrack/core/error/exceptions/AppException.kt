package com.example.reptrack.core.error.exceptions

import android.R.id.message
import com.example.reptrack.core.error.model.ErrorContext

/**
 * Base class for all application exceptions.
 * Sealed class allows exhaustive handling in when expressions.
 *
 * @property message Technical error message
 * @property cause The underlying throwable that caused this exception
 * @property userMessage User-friendly error message to display in UI (optional)
 * @property errorCode Application-specific error code for mapping to localized messages (optional)
 */
sealed class AppException(
    message: String,
    cause: Throwable? = null,
    open val userMessage: String? = null,
    val errorCode: String? = null
) : Exception(message, cause) {

    /**
     * Log level for this exception type
     */
    abstract val logLevel: LogLevel

    /**
     * Whether this exception should be reported to Crashlytics
     */
    abstract val reportToCrashlytics: Boolean

    /**
     * Error severity levels for logging
     */
    enum class LogLevel { VERBOSE, DEBUG, INFO, WARN, ERROR }
}

// ============================================================================
// Domain Exceptions - Business logic and validation errors
// ============================================================================

/**
 * Base class for domain layer exceptions
 */
sealed class DomainException(
    message: String,
    cause: Throwable? = null,
    userMessage: String? = null,
    errorCode: String? = null
) : AppException(message, cause, userMessage, errorCode) {

    /**
     * Entity was not found in the data source
     */
    data class EntityNotFound(
        val entityType: String,
        val entityId: String,
        override val cause: Throwable? = null
    ) : DomainException(
        message = "$entityType with id '$entityId' not found",
        cause = cause,
        userMessage = "The requested $entityType was not found",
        errorCode = "entity_not_found"
    ) {
        override val logLevel = LogLevel.WARN
        override val reportToCrashlytics = false
    }

    /**
     * Validation error for invalid input data
     */
    data class ValidationError(
        val field: String,
        val reason: String,
        override val cause: Throwable? = null
    ) : DomainException(
        message = "Validation failed for field '$field': $reason",
        cause = cause,
        userMessage = "Invalid $field: $reason",
        errorCode = "validation_error"
    ) {
        override val logLevel = LogLevel.INFO
        override val reportToCrashlytics = false
    }

    /**
     * Business logic rule violation
     */
    data class BusinessLogicError(
        override val message: String,
        override val cause: Throwable? = null,
        override val userMessage: String? = null
    ) : DomainException(
        message = "Business logic error: $message",
        cause = cause,
        userMessage = userMessage ?: "Operation not allowed",
        errorCode = "business_logic_error"
    ) {
        override val logLevel = LogLevel.WARN
        override val reportToCrashlytics = true
    }

    /**
     * Invalid state error - operation not allowed in current state
     */
    data class InvalidStateError(
        val currentState: String,
        val expectedState: String,
        override val cause: Throwable? = null
    ) : DomainException(
        message = "Invalid state: expected '$expectedState' but was '$currentState'",
        cause = cause,
        userMessage = "This action is not available at this time",
        errorCode = "invalid_state"
    ) {
        override val logLevel = LogLevel.WARN
        override val reportToCrashlytics = false
    }
}

// ============================================================================
// Data Exceptions - Database and data access errors
// ============================================================================

/**
 * Base class for data layer exceptions
 */
sealed class DataException(
    message: String,
    cause: Throwable? = null,
    userMessage: String? = null,
    errorCode: String? = null
) : AppException(message, cause, userMessage, errorCode) {

    /**
     * Database operation error
     */
    data class DatabaseError(
        val operation: String,
        override val cause: Throwable? = null
    ) : DataException(
        message = "Database error during operation: $operation",
        cause = cause,
        userMessage = "Failed to save data. Please try again.",
        errorCode = "database_error"
    ) {
        override val logLevel = LogLevel.ERROR
        override val reportToCrashlytics = true
    }

    /**
     * Serialization/deserialization error
     */
    data class SerializationError(
        val dataType: String,
        override val cause: Throwable? = null
    ) : DataException(
        message = "Failed to serialize/deserialize: $dataType",
        cause = cause,
        userMessage = "Data format error occurred",
        errorCode = "serialization_error"
    ) {
        override val logLevel = LogLevel.ERROR
        override val reportToCrashlytics = true
    }

    /**
     * Data access error - generic data source issue
     */
    data class DataAccessError(
        override val message: String,
        override val cause: Throwable? = null
    ) : DataException(
        message = "Data access error: $message",
        cause = cause,
        userMessage = "Failed to access data. Please try again.",
        errorCode = "data_access_error"
    ) {
        override val logLevel = LogLevel.ERROR
        override val reportToCrashlytics = true
    }
}

// ============================================================================
// Network Exceptions - Network and remote service errors
// ============================================================================

/**
 * Base class for network layer exceptions
 */
sealed class NetworkException(
    message: String,
    cause: Throwable? = null,
    userMessage: String? = null,
    errorCode: String? = null
) : AppException(message, cause, userMessage, errorCode) {

    /**
     * No internet connection or network unreachable
     */
    data class NoConnection(
        override val cause: Throwable? = null
    ) : NetworkException(
        message = "No internet connection",
        cause = cause,
        userMessage = "No internet connection. Please check your network.",
        errorCode = "no_connection"
    ) {
        override val logLevel = LogLevel.WARN
        override val reportToCrashlytics = false
    }

    /**
     * Server returned an error response
     */
    data class ServerError(
        val code: Int,
        override val cause: Throwable? = null
    ) : NetworkException(
        message = "Server error: $code - ${message ?: "Unknown error"}",
        userMessage = "Server error occurred. Please try again later.",
        errorCode = "server_error_$code"
    ) {
        override val logLevel = LogLevel.ERROR
        override val reportToCrashlytics = true
    }

    /**
     * Request timeout
     */
    data class Timeout(
        val timeoutMillis: Long,
        override val cause: Throwable? = null
    ) : NetworkException(
        message = "Request timeout after ${timeoutMillis}ms",
        cause = cause,
        userMessage = "Request timed out. Please check your connection.",
        errorCode = "timeout"
    ) {
        override val logLevel = LogLevel.WARN
        override val reportToCrashlytics = false
    }

    /**
     * Unauthorized - authentication required
     */
    data class Unauthorized(
        override val message: String = "Authentication required",
        override val cause: Throwable? = null
    ) : NetworkException(
        message = message,
        cause = cause,
        userMessage = "Please sign in to continue",
        errorCode = "unauthorized"
    ) {
        override val logLevel = LogLevel.INFO
        override val reportToCrashlytics = false
    }
}

// ============================================================================
// Unknown Exception - Fallback for unexpected errors
// ============================================================================

/**
 * Unknown or unexpected exception
 */
data class UnknownException(
    override val message: String,
    override val cause: Throwable? = null
) : AppException(
    message = "Unknown error: $message",
    cause = cause,
    userMessage = "An unexpected error occurred. Please try again.",
    errorCode = "unknown_error"
) {
    override val logLevel = LogLevel.ERROR
    override val reportToCrashlytics = true
}
