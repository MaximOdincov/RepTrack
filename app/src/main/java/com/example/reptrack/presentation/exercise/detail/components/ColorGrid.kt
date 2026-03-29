package com.example.reptrack.presentation.exercise.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reptrack.presentation.exercise.detail.utils.ColorUtils

/**
 * Grid of available colors for exercise icons
 *
 * @param selectedColor The currently selected color string
 * @param onColorSelected Callback when a color is selected
 */
@Composable
fun ColorGrid(
    selectedColor: String?,
    onColorSelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        items(ColorUtils.COLOR_PALETTE.size) { index ->
            val colorString = ColorUtils.COLOR_PALETTE[index]
            val color = ColorUtils.parseColor(colorString)
            val isSelected = colorString == selectedColor

            Surface(
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) {
                    BorderStroke(2.dp, Color.Black)
                } else {
                    BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                },
                modifier = Modifier
                    .size(56.dp)
                    .clickable { onColorSelected(colorString) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
                        .padding(8.dp)
                )
            }
        }
    }
}
