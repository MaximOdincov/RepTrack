package com.example.reptrack.presentation.statistics.utils

import androidx.compose.ui.graphics.Color

/**
 * Utility functions for color operations in the statistics module
 */

/**
 * Converts an ARGB Long value to a Compose Color object
 *
 * @param argb The ARGB value as Long (format: 0xAARRGGBB)
 * @return Compose Color object
 */
fun colorFromArgb(argb: Long): Color {
    val alpha = ((argb shr 24) and 0xFF).toFloat() / 255f
    val red = ((argb shr 16) and 0xFF).toFloat() / 255f
    val green = ((argb shr 8) and 0xFF).toFloat() / 255f
    val blue = (argb and 0xFF).toFloat() / 255f
    val color = Color(red, green, blue, alpha)
    android.util.Log.d("ColorUtils", "colorFromArgb: 0x${argb.toString(16)} -> A=$alpha, R=$red, G=$green, B=$blue")
    android.util.Log.d("ColorUtils", "Result: $color")
    return color
}

/**
 * Converts a Compose Color to an ARGB Long value
 *
 * @param color The Compose Color to convert
 * @return ARGB value as Long (format: 0xAARRGGBB)
 */
fun colorToArgb(color: Color): Long {
    val alpha = (color.alpha * 255).toLong()
    val red = (color.red * 255).toLong()
    val green = (color.green * 255).toLong()
    val blue = (color.blue * 255).toLong()
    val argb = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    android.util.Log.d("ColorUtils", "colorToArgb: $color -> A=$alpha, R=$red, G=$green, B=$blue -> 0x${argb.toString(16)}")
    return argb
}