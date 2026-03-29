package com.example.reptrack.presentation.exercise.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.R
import com.example.reptrack.presentation.exercise.detail.utils.ColorUtils

/**
 * Editable exercise card with name, muscle group, and icon/color customization
 *
 * @param name The exercise name
 * @param muscleGroup The selected muscle group
 * @param iconRes The icon resource
 * @param iconColor The icon color string
 * @param onNameChanged Callback when name changes
 * @param onMuscleGroupChanged Callback when muscle group changes
 * @param onEditIconClicked Callback when edit icon button is clicked
 * @param onSaveNeeded Callback to trigger save (optional)
 * @param modifier Modifier for the card
 */
@Composable
fun ExerciseEditCard(
    name: String,
    muscleGroup: com.example.reptrack.domain.workout.entities.MuscleGroup,
    iconRes: Int?,
    iconColor: String?,
    onNameChanged: (String) -> Unit,
    onMuscleGroupChanged: (com.example.reptrack.domain.workout.entities.MuscleGroup) -> Unit,
    onEditIconClicked: () -> Unit,
    onSaveNeeded: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with edit button
            Box(
                modifier = Modifier.size(100.dp)
            ) {
                // Icon background
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            ColorUtils.parseColor(iconColor).copy(alpha = 0.2f)
                        )
                        .border(
                            BorderStroke(2.dp, Color.Gray.copy(alpha = 0.3f)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconRes != null) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Exercise icon",
                            modifier = Modifier.size(60.dp),
                            tint = ColorUtils.parseColor(iconColor)
                        )
                    } else {
                        Text(
                            text = "?",
                            fontSize = 48.sp,
                            color = Color.Gray.copy(alpha = 0.5f)
                        )
                    }
                }

                // Edit button
                IconButton(
                    onClick = {
                        // Clear focus when opening icon editor
                        focusManager.clearFocus()
                        onEditIconClicked()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit icon",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name field with focus management
            val focusRequester = remember { FocusRequester() }

            OutlinedTextField(
                value = name,
                onValueChange = onNameChanged,
                label = { Text("Exercise Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onSaveNeeded?.invoke()
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Muscle group dropdown
            MuscleGroupDropdown(
                selectedGroup = muscleGroup,
                onGroupSelected = { group ->
                    onMuscleGroupChanged(group)
                    // Clear focus after selection
                    focusManager.clearFocus()
                    onSaveNeeded?.invoke()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
