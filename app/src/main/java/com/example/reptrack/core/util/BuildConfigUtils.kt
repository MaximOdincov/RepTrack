package com.example.reptrack.core.util

/**
 * Utility for checking build configuration
 */
object BuildConfigUtils {
    /**
     * Returns true if this is a debug build
     */
    val isDebug: Boolean
        get() = com.example.reptrack.BuildConfig.DEBUG
}
