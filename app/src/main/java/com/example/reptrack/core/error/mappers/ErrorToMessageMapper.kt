package com.example.reptrack.core.error.mappers

import com.example.reptrack.core.error.exceptions.*

/**
 * Maps technical exceptions to user-friendly error messages
 */
interface ErrorToMessageMapper {
    /**
     * Returns a user-friendly message for the given throwable
     */
    fun getUserMessage(throwable: Throwable): String
}

/**
 * Default implementation that maps exceptions to localized messages
 */
class ErrorToMessageMapperImpl : ErrorToMessageMapper {

    @Suppress("TooGenericExceptionCaught")
    override fun getUserMessage(throwable: Throwable): String {
        return when (throwable) {
            // AppException hierarchy
            is AppException -> {
                throwable.userMessage ?: getDefaultMessageForAppException(throwable)
            }

            // Standard Java/Kotlin exceptions
            is java.sql.SQLException -> "Database error occurred"
            is java.io.IOException -> "Failed to access data"
            is java.util.NoSuchElementException -> "The requested item was not found"
            is IllegalArgumentException -> "Invalid data provided"
            is IllegalStateException -> "Operation not allowed at this time"
            is NullPointerException -> "Internal application error"
            is IndexOutOfBoundsException -> "Internal data error"

            // Network exceptions - handled through custom exceptions
            // is retrofit2.HttpException -> { ... } // Not using Retrofit in this project

            // Fallback for unknown exceptions
            else -> "An unexpected error occurred. Please try again."
        }
    }

    private fun getDefaultMessageForAppException(exception: AppException): String {
        return when (exception) {
            is DomainException -> when (exception) {
                is DomainException.EntityNotFound -> "Item not found"
                is DomainException.ValidationError -> "Invalid input data"
                is DomainException.BusinessLogicError -> "Operation failed"
                is DomainException.InvalidStateError -> "This action is not available at this time"
            }
            is DataException -> when (exception) {
                is DataException.DatabaseError -> "Failed to save data"
                is DataException.SerializationError -> "Data format error"
                is DataException.DataAccessError -> "Failed to access data"
            }
            is NetworkException -> when (exception) {
                is NetworkException.NoConnection -> "No internet connection"
                is NetworkException.ServerError -> "Server error occurred"
                is NetworkException.Timeout -> "Request timed out"
                is NetworkException.Unauthorized -> "Please sign in to continue"
            }
            is UnknownException -> "An unexpected error occurred"
        }
    }
}
