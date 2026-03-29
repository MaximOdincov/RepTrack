package com.example.reptrack.presentation.exercise.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.MuscleGroup

/**
 * Dropdown menu for selecting muscle groups with icons
 *
 * @param selectedGroup The currently selected muscle group
 * @param onGroupSelected Callback when a muscle group is selected
 * @param modifier Modifier for the component
 */
@Composable
fun MuscleGroupDropdown(
    selectedGroup: MuscleGroup,
    onGroupSelected: (MuscleGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // Get icon for muscle group
    fun getIconForGroup(group: MuscleGroup): Int {
        return when (group) {
            MuscleGroup.CHEST -> R.drawable.muscle_icon_chest
            MuscleGroup.BACK -> R.drawable.muscle_icon_back
            MuscleGroup.LEGS -> R.drawable.muscle_icon_legs
            MuscleGroup.ARMS -> R.drawable.muscle_icon_arms
            MuscleGroup.ABS -> R.drawable.muscle_icon_abs
            MuscleGroup.CARDIO -> R.drawable.muscle_icon_cardio
        }
    }

    // Get color for muscle group
    fun getColorForGroup(group: MuscleGroup): Color {
        return when (group) {
            MuscleGroup.CHEST -> Color(0xFFFF6B6B)
            MuscleGroup.BACK -> Color(0xFF009688)
            MuscleGroup.LEGS -> Color(0xFF43A047)
            MuscleGroup.ARMS -> Color(0xFFE64A19)
            MuscleGroup.ABS -> Color(0xFF7E57C2)
            MuscleGroup.CARDIO -> Color(0xFFEC407A)
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedGroup.name.lowercase().replaceFirstChar { it.uppercase() },
                onValueChange = { },
                label = { Text("Muscle Group") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown menu"
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = getIconForGroup(selectedGroup)),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = getColorForGroup(selectedGroup)
                    )
                },
                readOnly = true
            )

            // Transparent clickable overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .height(56.dp)
                    .clickable { expanded = true }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldWidth.toDp() })
                .background(Color.White),
            // No shape parameter - remove rounded corners
        ) {
            MuscleGroup.entries.forEach { group ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable {
                            onGroupSelected(group)
                            expanded = false
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = getIconForGroup(group)),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = getColorForGroup(group)
                        )
                        Text(
                            text = group.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}
