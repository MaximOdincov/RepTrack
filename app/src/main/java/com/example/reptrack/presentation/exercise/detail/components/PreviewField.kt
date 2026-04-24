package com.example.reptrack.presentation.exercise.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.reptrack.presentation.utils.painterResourceSafe
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.presentation.exercise.detail.utils.ColorUtils

/**
 * A clickable preview field that displays either an icon or color
 *
 * @param label The label to display above the preview
 * @param iconRes The drawable resource ID for the icon (if in icon mode)
 * @param iconColor The color string for the icon (if in icon mode)
 * @param isSelected Whether this field is currently selected
 * @param onClick Callback when the field is clicked
 * @param isIconMode Whether this is showing an icon (true) or color (false)
 * @param modifier Modifier for the component
 */
@Composable
fun PreviewField(
    label: String,
    iconRes: Int?,
    iconColor: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    isIconMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
        },
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    } else {
                        Color.Transparent
                    }
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isIconMode) {
                // Icon preview
                // Проверяем и на null, и на 0 (0 - не валидный resource ID)
                if (iconRes != null && iconRes != 0) {
                    Icon(
                        painter = painterResourceSafe(id = iconRes),
                        contentDescription = "Icon preview",
                        modifier = Modifier.size(48.dp),
                        tint = ColorUtils.parseColor(iconColor)
                    )
                } else {
                    Text(
                        text = "?",
                        fontSize = 32.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // Color preview
                val color = ColorUtils.parseColor(iconColor)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Gray
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
