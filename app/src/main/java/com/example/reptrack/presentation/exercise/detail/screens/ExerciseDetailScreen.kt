package com.example.reptrack.presentation.exercise.detail.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.mvikotlin.core.store.Store
import com.example.reptrack.navigation.ExerciseDetailMode
import com.example.reptrack.presentation.exercise.detail.components.CustomizationBottomSheet
import com.example.reptrack.presentation.exercise.detail.components.ExerciseEditCard
import com.example.reptrack.presentation.exercise.detail.stores.CustomizationSheetMode
import com.example.reptrack.presentation.exercise.detail.stores.ExerciseDetailStore
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars

/**
 * Exercise Detail screen with editable exercise card and customization bottom sheet
 *
 * @param store MVIKotlin store for state management
 * @param exerciseId ID of the exercise to display
 * @param mode Screen mode (DESIGN_MODE or WORKOUT_MODE)
 * @param onNavigateBack Callback when back button is pressed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    store: Store<ExerciseDetailStore.Intent, ExerciseDetailStore.State, ExerciseDetailStore.Label>,
    exerciseId: String,
    mode: ExerciseDetailMode,
    onNavigateBack: () -> Unit = {}
) {
    // Initialize screen
    LaunchedEffect(exerciseId, mode) {
        store.accept(ExerciseDetailStore.Intent.Initialize(exerciseId, mode))
    }

    // Collect state
    val state by store.states.collectAsState(ExerciseDetailStore.State())

    // Collect labels for navigation
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                is ExerciseDetailStore.Label.NavigateBack -> onNavigateBack()
                is ExerciseDetailStore.Label.ShowSavedToast -> {
                    // TODO: Show toast message
                }
                is ExerciseDetailStore.Label.ShowError -> {
                    // TODO: Show error message
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Exercise Details",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            store.accept(ExerciseDetailStore.Intent.SaveAndExit)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),
                windowInsets = WindowInsets(0) // Remove system insets
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                // Loading state
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    // Editable Exercise Card
                    ExerciseEditCard(
                        name = state.name,
                        muscleGroup = state.muscleGroup,
                        iconRes = state.iconRes,
                        iconColor = state.iconColor,
                        onNameChanged = { newName ->
                            store.accept(ExerciseDetailStore.Intent.NameChanged(newName))
                        },
                        onMuscleGroupChanged = { newGroup ->
                            store.accept(ExerciseDetailStore.Intent.MuscleGroupChanged(newGroup))
                        },
                        onEditIconClicked = {
                            store.accept(ExerciseDetailStore.Intent.OpenCustomizationSheet)
                        },
                        onSaveNeeded = null // Auto-save on exit
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Placeholder for additional content (sets, progress, etc.)
                    // This will be implemented in future iterations
                }
            }

            // Customization Bottom Sheet
            CustomizationBottomSheet(
                isVisible = state.isCustomizationSheetVisible,
                sheetMode = state.sheetMode,
                iconRes = state.iconRes,
                iconColor = state.iconColor,
                draftIconRes = state.draftIconRes,
                draftIconColor = state.draftIconColor,
                onModeSelected = { newMode ->
                    store.accept(ExerciseDetailStore.Intent.SheetModeChanged(newMode))
                },
                onIconSelected = { iconRes ->
                    store.accept(ExerciseDetailStore.Intent.IconSelected(iconRes))
                },
                onColorSelected = { color ->
                    store.accept(ExerciseDetailStore.Intent.ColorSelected(color))
                },
                onDismiss = {
                    store.accept(ExerciseDetailStore.Intent.CloseCustomizationSheet)
                }
            )
        }
    }
}
