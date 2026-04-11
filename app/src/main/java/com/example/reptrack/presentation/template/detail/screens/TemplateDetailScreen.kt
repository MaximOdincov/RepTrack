package com.example.reptrack.presentation.template.detail.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.res.painterResource
import com.example.reptrack.presentation.utils.painterResourceSafe
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.R
import com.example.reptrack.presentation.exercise.detail.components.CustomizationBottomSheet
import com.example.reptrack.presentation.template.detail.stores.TemplateDetailStore
import com.example.reptrack.presentation.theme.LightAccentOrange
import androidx.compose.foundation.layout.WindowInsets
import android.widget.Toast
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.core.graphics.toColorInt
import androidx.compose.material.icons.filled.Close

/**
 * Template Detail screen with editable template card and customization bottom sheet
 *
 * @param store MVIKotlin store for state management
 * @param templateId ID of the template to display (null for new template)
 * @param mode Screen mode (CREATE_MODE, EDIT_MODE, or VIEW_MODE)
 * @param onNavigateBack Callback when back button is pressed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateDetailScreen(
    store: Store<TemplateDetailStore.Intent, TemplateDetailStore.State, TemplateDetailStore.Label>,
    templateId: String?,
    mode: TemplateDetailStore.TemplateDetailMode,
    onNavigateBack: () -> Unit = {},
    onNavigateToExerciseSelection: () -> Unit = {}
) {
    // Initialize screen - only when templateId or mode changes
    LaunchedEffect(templateId, mode) {
        store.accept(TemplateDetailStore.Intent.Initialize(templateId, mode))
    }

    // Collect state
    val state by store.states.collectAsState(TemplateDetailStore.State())

    // Log state changes for debugging
    LaunchedEffect(state.isLoading, state.isInitialized, state.mode) {
        io.github.aakira.napier.Napier.i(
            "TemplateDetailScreen: isLoading=${state.isLoading}, isInitialized=${state.isInitialized}, mode=${state.mode}, templateId=${state.templateId}",
            tag = "TemplateDetailScreen"
        )
    }

    // Context for toasts
    val context = LocalContext.current

    // Collect labels for navigation
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                is TemplateDetailStore.Label.NavigateBack -> onNavigateBack()
                is TemplateDetailStore.Label.ShowSavedToast -> {
                    Toast.makeText(
                        context,
                        "Сохранено",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is TemplateDetailStore.Label.ShowError -> {
                    Toast.makeText(
                        context,
                        label.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Шаблон тренировки",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            store.accept(TemplateDetailStore.Intent.ExitWithCheck)
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
                windowInsets = WindowInsets(0)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show loading indicator for EDIT/VIEW_MODE until data is initialized
            // For CREATE_MODE, show content immediately (no data to load)
            val showLoading = state.mode != TemplateDetailStore.TemplateDetailMode.CREATE_MODE && !state.isInitialized

            if (showLoading) {
                io.github.aakira.napier.Napier.i("TemplateDetailScreen: RENDERING LOADING INDICATOR", tag = "TemplateDetailScreen")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                io.github.aakira.napier.Napier.i("TemplateDetailScreen: RENDERING CONTENT", tag = "TemplateDetailScreen")
                // Show content only after loading is complete
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                        // Template edit card
                        item {
                            TemplateEditCard(
                                name = state.name,
                                description = state.description,
                                iconRes = state.iconRes,
                                iconColor = state.iconColor,
                                onNameChanged = { newName ->
                                    store.accept(TemplateDetailStore.Intent.NameChanged(newName))
                                },
                                onDescriptionChanged = { newDescription ->
                                    store.accept(TemplateDetailStore.Intent.DescriptionChanged(newDescription))
                                },
                                onEditIconClicked = {
                                    store.accept(TemplateDetailStore.Intent.OpenCustomizationSheet)
                                },
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // Schedule picker
                        item {
                            com.example.reptrack.presentation.template.detail.components.SchedulePicker(
                                schedule = state.schedule,
                                onDayToggle = { weekNumber, day ->
                                    store.accept(TemplateDetailStore.Intent.ToggleScheduleDay(weekNumber, day))
                                }
                            )
                        }

                        // Add exercise button
                        item {
                            AddExerciseButton(
                                onClick = { onNavigateToExerciseSelection() },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // Exercises header
                        if (state.exerciseIds.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Упражнения",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        // Exercises in template - displayed in order with drag & drop
                        itemsIndexed(
                            items = state.exerciseIds,
                            key = { index, exerciseId -> "${exerciseId}_$index" }
                        ) { index, exerciseId ->
                            state.availableExercises.find { it.id == exerciseId }?.let { ex ->
                                DraggableExerciseItem(
                                    exercise = ex,
                                    index = index,
                                    totalItems = state.exerciseIds.size,
                                    exerciseIds = state.exerciseIds,
                                    onMoveUp = { store.accept(TemplateDetailStore.Intent.MoveExerciseUp(index)) },
                                    onMoveDown = { store.accept(TemplateDetailStore.Intent.MoveExerciseDown(index)) },
                                    onRemove = { store.accept(TemplateDetailStore.Intent.RemoveExerciseFromTemplate(ex.id)) },
                                    onReorder = { _, _ -> }
                                )
                            }
                        }
                    }
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
                    store.accept(TemplateDetailStore.Intent.SheetModeChanged(newMode))
                },
                onIconSelected = { iconRes ->
                    store.accept(TemplateDetailStore.Intent.IconSelected(iconRes))
                },
                onColorSelected = { color ->
                    store.accept(TemplateDetailStore.Intent.ColorSelected(color))
                },
                onDismiss = {
                    store.accept(TemplateDetailStore.Intent.CloseCustomizationSheet)
                }
            )
        }
    }

/**
 * Editable template card
 */
@Composable
private fun TemplateEditCard(
    name: String,
    description: String,
    iconRes: Int?,
    iconColor: String?,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onEditIconClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconTint = try {
        iconColor?.let { Color(it.toColorInt()) }
            ?: Color(0xFF2196F3)
    } catch (e: Exception) {
        Color(0xFF2196F3)
    }

    // Просто используем iconRes с fallback на дефолтную
    // Проверяем и на null, и на 0 (0 - валидное значение int, но не валидный resource ID)
    val iconResId = if (iconRes != null && iconRes != 0) {
        iconRes
    } else {
        R.drawable.exercise_default_icon
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Icon row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = iconTint.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .blur(radius = 8.dp)
                        .clickable { onEditIconClicked() }
                )

                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .matchParentSize(),
                    painter = painterResourceSafe(id = iconResId),
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    placeholder = { Text("Название шаблона") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            placeholder = { Text("Описание (опционально)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
    }
}

/**
 * Button to add exercise to template
 */
@Composable
private fun AddExerciseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LightAccentOrange
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Добавить упражнение",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

