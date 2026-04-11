package com.example.reptrack.core.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import io.github.aakira.napier.Napier

/**
 * Validates that a drawable resource is supported by painterResource.
 * painterResource ONLY supports: VectorDrawables and rasterized assets (PNG, JPG, WEBP)
 *
 * This function tries to actually load the drawable to verify it's supported.
 *
 * @param context The context
 * @param resId The resource ID to validate
 * @return true if the resource can be loaded by painterResource, false otherwise
 */
fun isValidDrawableResource(context: Context, resId: Int): Boolean {
    if (resId == 0) {
        return false
    }

    return try {
        // Check if the resource exists and is a drawable/mipmap
        val typeName = context.resources.getResourceTypeName(resId)
        if (typeName != "drawable" && typeName != "mipmap") {
            return false
        }

        // Try to load the drawable with a theme to verify it works
        // Use null theme - if it fails, the resource is not supported
        val drawable: Drawable? = ResourcesCompat.getDrawable(
            context.resources,
            resId,
            null
        )

        // If we successfully loaded it, check the type
        when (drawable) {
            is android.graphics.drawable.VectorDrawable,
            is android.graphics.drawable.BitmapDrawable,
            is androidx.vectordrawable.graphics.drawable.VectorDrawableCompat -> {
                true
            }
            else -> {
                // Other types like selectors, layer-list, etc. are NOT supported
                false
            }
        }
    } catch (e: Exception) {
        // Resource not found, invalid, or unsupported type
        val entryName = try {
            context.resources.getResourceEntryName(resId)
        } catch (_: Exception) {
            "unknown"
        }
        Napier.e("isValidDrawableResource: $entryName (id=$resId) failed: ${e.message}", tag = "PainterLoader")
        false
    }
}
