package com.example.reptrack.presentation.exercise.detail.utils

import androidx.compose.ui.graphics.Color

/**
 * Utility functions for color parsing and manipulation
 */
object ColorUtils {

    /**
     * Parse a color string to Compose Color
     * Supports formats:
     * - "#RRGGBB" or "#AARRGGBB" (hex)
     * - "0xFFRRGGBB" (kotlin hex literal format string)
     */
    fun parseColor(colorString: String?): Color {
        if (colorString.isNullOrBlank()) return Color.Gray

        return try {
            when {
                // Remove "0x" or "0X" prefix if present
                colorString.startsWith("0x", ignoreCase = true) -> {
                    val hexString = colorString.substring(2)
                    Color(hexString.toLong(16))
                }
                // Standard hex format with #
                colorString.startsWith("#") -> {
                    Color(android.graphics.Color.parseColor(colorString))
                }
                // Plain hex string
                else -> {
                    val hexString = if (colorString.length == 6) {
                        "FF$colorString" // Add alpha if missing
                    } else {
                        colorString
                    }
                    Color(hexString.toLong(16))
                }
            }
        } catch (e: Exception) {
            Color.Gray
        }
    }

    /**
     * Convert Compose Color to hex string
     */
    fun colorToHex(color: Color): String {
        return String.format("#%08X", (color.value.toLong() and 0xFFFFFFFFL))
    }

    /**
     * predefined color palette for exercise icons
     * 40 colors across various hues
     */
    val COLOR_PALETTE = listOf(
        // Reds
        "#F44336", "#E53935", "#D32F2F", "#C62828", "#B71C1C",
        // Teals
        "#009688", "#00897B", "#00796B", "#00695C", "#004D40",
        // Greens
        "#4CAF50", "#43A047", "#388E3C", "#2E7D32", "#1B5E20",
        // Oranges
        "#FF9800", "#FB8C00", "#F57C00", "#EF6C00", "#E65100",
        // Purples
        "#9C27B0", "#8E24AA", "#7B1FA2", "#6A1B9A", "#4A148C",
        // Pinks
        "#E91E63", "#D81B60", "#C2185B", "#AD1457", "#880E4F",
        // Blues
        "#2196F3", "#1E88E5", "#1976D2", "#1565C0", "#0D47A1",
        // Yellows
        "#FFEB3B", "#FDD835", "#FBC02D", "#F9A825", "#F57F17",
        // Grays
        "#9E9E9E", "#757575", "#616161", "#424242", "#212121"
    )
}
