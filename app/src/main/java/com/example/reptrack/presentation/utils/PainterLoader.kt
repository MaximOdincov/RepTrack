package com.example.reptrack.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.reptrack.R

/**
 * Safely loads a painter resource, falling back to default icon if resource is invalid
 */
@Composable
fun painterResourceSafe(
    id: Int?,
    defaultId: Int = R.drawable.exercise_default_icon
): Painter {
    val resourceId = id ?: defaultId
    val context = LocalContext.current

    // Cache whether this resource ID is safe to load
    val isSafe = remember(resourceId) {
        try {
            // Check if resource exists and is a drawable
            val typeName = context.resources.getResourceTypeName(resourceId)

            // Check if this is an app resource (not a system resource)
            // System resources have package name "android", app resources have the app's package name
            val packageName = context.resources.getResourcePackageName(resourceId)
            val isAppResource = packageName == context.packageName

            if (!isAppResource) {
                android.util.Log.e("PainterLoader", "⚠️ System resource detected: $resourceId, package: $packageName")
            }

            // Get resource entry name to check for .9.png (NinePatch)
            val entryName = context.resources.getResourceEntryName(resourceId)

            // Only allow drawable resources from the app that are NOT NinePatch (.9.png files)
            val result = typeName == "drawable" && isAppResource && !entryName.contains(".9.")

            if (!result) {
                android.util.Log.e("PainterLoader", "⚠️ Unsafe resource: id=$resourceId, type=$typeName, package=$packageName, entry=$entryName")
            }

            result
        } catch (e: Exception) {
            android.util.Log.e("PainterLoader", "❌ Error checking resource $resourceId: ${e.message}")
            false
        }
    }

    return if (isSafe && resourceId != 0) {
        painterResource(id = resourceId)
    } else {
        painterResource(id = defaultId)
    }
}
