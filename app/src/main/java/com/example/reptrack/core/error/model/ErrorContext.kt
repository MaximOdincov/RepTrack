package com.example.reptrack.core.error.model

/**
 * Context information about an error for better debugging and tracking
 *
 * @property screen The screen where the error occurred (optional)
 * @property action The action being performed when error occurred (optional)
 * @property entityId The ID of the entity being operated on (optional)
 * @property additionalInfo Additional contextual information
 */
data class ErrorContext(
    val screen: String? = null,
    val action: String? = null,
    val entityId: String? = null,
    val additionalInfo: Map<String, Any?> = emptyMap()
) {
    /**
     * Returns a string representation of the error context for logging
     */
    fun toLogString(): String {
        return buildString {
            if (screen != null) append("screen=$screen")
            if (action != null) {
                if (isNotEmpty()) append(", ")
                append("action=$action")
            }
            if (entityId != null) {
                if (isNotEmpty()) append(", ")
                append("entityId=$entityId")
            }
            if (additionalInfo.isNotEmpty()) {
                if (isNotEmpty()) append(", ")
                append("info=$additionalInfo")
            }
        }
    }

    /**
     * Creates a copy of this ErrorContext with additional information added
     */
    fun withAdditionalInfo(key: String, value: Any?): ErrorContext {
        return copy(additionalInfo = additionalInfo + (key to value))
    }

    /**
     * Creates a copy of this ErrorContext with multiple additional information entries
     */
    fun withAdditionalInfo(vararg pairs: Pair<String, Any?>): ErrorContext {
        return copy(additionalInfo = additionalInfo + pairs)
    }
}
