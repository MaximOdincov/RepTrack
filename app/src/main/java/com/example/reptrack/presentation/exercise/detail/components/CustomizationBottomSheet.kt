package com.example.reptrack.presentation.exercise.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reptrack.presentation.exercise.detail.stores.CustomizationSheetMode

/**
 * Modal bottom sheet for customizing exercise icon and color
 *
 * @param isVisible Whether the sheet is currently visible
 * @param sheetMode Current mode (ICON or COLOR)
 * @param iconRes Current icon resource
 * @param iconColor Current icon color string
 * @param draftIconRes Draft icon resource (changes in progress)
 * @param draftIconColor Draft icon color (changes in progress)
 * @param onModeSelected Callback when mode is switched
 * @param onIconSelected Callback when icon is selected
 * @param onColorSelected Callback when color is selected
 * @param onDismiss Callback when sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationBottomSheet(
    isVisible: Boolean,
    sheetMode: CustomizationSheetMode,
    iconRes: Int?,
    iconColor: String?,
    draftIconRes: Int?,
    draftIconColor: String?,
    onModeSelected: (CustomizationSheetMode) -> Unit,
    onIconSelected: (Int) -> Unit,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = MaterialTheme.shapes.large,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Customize Exercise",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Preview Fields Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon Preview Field
                    PreviewField(
                        label = "icon",
                        iconRes = draftIconRes ?: iconRes,
                        iconColor = draftIconColor ?: iconColor,
                        isSelected = sheetMode == CustomizationSheetMode.ICON,
                        onClick = { onModeSelected(CustomizationSheetMode.ICON) },
                        isIconMode = true,
                        modifier = Modifier.weight(1f)
                    )

                    // Color Preview Field
                    PreviewField(
                        label = "color",
                        iconRes = draftIconRes ?: iconRes,
                        iconColor = draftIconColor ?: iconColor,
                        isSelected = sheetMode == CustomizationSheetMode.COLOR,
                        onClick = { onModeSelected(CustomizationSheetMode.COLOR) },
                        isIconMode = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Content based on mode
                when (sheetMode) {
                    CustomizationSheetMode.ICON -> {
                        IconGrid(
                            selectedIconRes = draftIconRes ?: iconRes,
                            onIconSelected = onIconSelected
                        )
                    }
                    CustomizationSheetMode.COLOR -> {
                        ColorGrid(
                            selectedColor = draftIconColor ?: iconColor,
                            onColorSelected = onColorSelected
                        )
                    }
                }
            }
        }
    }
}
