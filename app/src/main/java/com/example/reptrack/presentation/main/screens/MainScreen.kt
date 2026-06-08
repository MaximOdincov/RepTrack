package com.example.reptrack.presentation.main.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.entities.WorkoutStatus
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.presentation.exercise.list.utils.MuscleGroupColors
import com.example.reptrack.presentation.main.components.Calendar
import com.example.reptrack.presentation.main.components.WorkoutExerciseCard
import com.example.reptrack.presentation.main.stores.MainScreenStore
import java.time.LocalDate

/**
 * Saver for LocalDate to enable rememberSaveable
 */
private val LocalDateSaver = Saver<LocalDate, String>(
    save = { it.toString() },
    restore = { LocalDate.parse(it) }
)

@Composable
internal fun MainScreen(
    store: MainScreenStore,
    calendarUseCase: com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase,
    onNavigateToExerciseDetail: (String) -> Unit = {},
    onNavigateToLibrary: () -> Unit = {}
) {
    val state by store.states.collectAsState(MainScreenStore.State())
    android.util.Log.d("SessionDB", "MainScreen render: state.workoutSession=${state.workoutSession?.id}, exercises=${state.workoutSession?.exercises?.size}, exerciseDataSize=${state.exerciseData.size}")

    // Сохраняем выбранную дату между навигациями
    val selectedDate by rememberSaveable(state.currentDate) { mutableStateOf(state.currentDate) }

    // Инициализация при входе на экран
    LaunchedEffect(selectedDate) {
        store.accept(MainScreenStore.Intent.SelectDate(selectedDate))
    }



    // Handle navigation labels
    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                is MainScreenStore.Label.NavigateToExerciseDetail -> {
                    onNavigateToExerciseDetail(label.workoutExerciseId)
                }
                is MainScreenStore.Label.NavigateToTemplateExercise -> {
                    // This should not happen anymore as sessions are auto-created
                    onNavigateToExerciseDetail(label.exerciseId) // Fallback to exercise detail
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToLibrary,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить в тренировку",
                    tint = Color.White
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Calendar(
                initialDate = selectedDate,
                selectedDate = selectedDate,
                onDateSelected = { newDate ->
                    store.accept(MainScreenStore.Intent.SelectDate(newDate))
                },
                calendarUseCase = calendarUseCase
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.isLoading || state.isInitializing -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.workoutSession != null -> {
                    WorkoutDetails(
                        workout = state.workoutSession!!,
                        store = store,
                        exerciseData = state.exerciseData,
                        onExerciseClick = { workoutExerciseId ->
                            store.accept(MainScreenStore.Intent.ExerciseClicked(workoutExerciseId))
                        },
                        onExerciseDelete = { workoutExerciseId ->
                            store.accept(MainScreenStore.Intent.DeleteExercise(workoutExerciseId))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                state.applicableTemplates.isNotEmpty() -> {
                    // Show exercises from templates
                    TemplateWorkoutDetails(
                        templates = state.applicableTemplates,
                        exerciseData = state.templateExerciseData,
                        onExerciseClick = { workoutExerciseId ->
                            store.accept(MainScreenStore.Intent.ExerciseClicked(workoutExerciseId))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                else -> {
                    NoWorkoutPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun TemplateWorkoutDetails(
    templates: List<WorkoutTemplate>,
    exerciseData: Map<String, Exercise>,
    onExerciseClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        templates.forEach { template ->
            // Get unique muscle groups from exercises
            val muscleGroups = template.exerciseIds
                .mapNotNull { exerciseData[it]?.muscleGroup }
                .distinct()

            // Template Status Card
            TemplateStatusCard(
                templateName = template.name,
                exerciseCount = template.exerciseIds.size,
                muscleGroups = muscleGroups
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Exercise List
            template.exerciseIds.forEach { exerciseId ->
                val exercise = exerciseData[exerciseId]
                if (exercise != null) {
                    Box(
                        modifier = Modifier
                            .clickable { onExerciseClick(exerciseId) }
                    ) {
                        WorkoutExerciseCard(
                            exercise = exercise,
                            workoutExercise = com.example.reptrack.domain.workout.entities.WorkoutExercise(
                                id = "temp_${template.id}_$exerciseId",
                                workoutSessionId = template.id,
                                exerciseId = exerciseId,
                                exerciseName = exercise.name,
                                muscleGroup = exercise.muscleGroup,
                                exerciseType = exercise.type,
                                iconRes = exercise.iconRes,
                                sets = emptyList()
                            ),
                            bestSet = null,
                            muscleGroupColor = MuscleGroupColors.getPrimaryColor(exercise.muscleGroup)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TemplateStatusCard(
    templateName: String,
    exerciseCount: Int,
    muscleGroups: List<MuscleGroup>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            // Template Name and Exercise Count
            Column {
                Text(
                    templateName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    formatExerciseCount(exerciseCount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    muscleGroups.forEach { muscleGroup ->
                        MuscleGroupChip(muscleGroup = muscleGroup)
                    }
                }
            }
        }
    }
}

@Composable
private fun NoWorkoutPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Нет тренировки на этот день",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Добавьте шаблон или начните новую тренировку",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WorkoutDetails(
    workout: WorkoutSession,
    store: MainScreenStore,
    exerciseData: Map<String, MainScreenStore.ExerciseData>,
    onExerciseClick: (String) -> Unit = {},
    onExerciseDelete: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Get unique muscle groups from exercises
    val muscleGroups = workout.exercises
        .mapNotNull { exerciseData[it.exerciseId]?.exercise?.muscleGroup }
        .distinct()

    // State for delete confirmation dialog
    var exerciseToDelete by remember { mutableStateOf<String?>(null) }
    var exerciseNameToDelete by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Session Status Card
            item {
                SessionStatusCard(
                    status = workout.status,
                    exerciseCount = workout.exercises.size,
                    muscleGroups = muscleGroups
                )
            }

            // Combined White Card with Exercises
            android.util.Log.d("SessionDB", "WorkoutDetails: workout.exercises.size=${workout.exercises.size}, exerciseData.size=${exerciseData.size}")
            if (workout.exercises.isNotEmpty()) {
                android.util.Log.d("SessionDB", "WorkoutDetails: rendering exercises")
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            // Exercises Header
                            Text(
                                "Упражнения",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Exercise List
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                workout.exercises.forEach { workoutExercise ->
                                    val data = exerciseData[workoutExercise.exerciseId]

                                    if (data != null) {
                                        SwipeableExerciseCard(
                                            workoutExerciseId = workoutExercise.id,
                                            exercise = data.exercise,
                                            workoutExercise = data.workoutExercise,
                                            bestSet = data.bestSet,
                                            onClick = { onExerciseClick(workoutExercise.id) },
                                            onShowDeleteDialog = {
                                                exerciseToDelete = workoutExercise.id
                                                exerciseNameToDelete = data.exercise.name
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Comment section
            if (workout.comment != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Text(
                            "Comment: ${workout.comment}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Complete workout button (only show if IN_PROGRESS)
            if (workout.status == WorkoutStatus.IN_PROGRESS) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CompleteWorkoutButton(
                        onClick = { store.accept(MainScreenStore.Intent.CompleteWorkout) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Delete confirmation dialog
    if (exerciseToDelete != null) {
        DeleteConfirmationDialog(
            exerciseName = exerciseNameToDelete,
            onDismiss = {
                exerciseToDelete = null
                exerciseNameToDelete = ""
            },
            onConfirm = {
                exerciseToDelete?.let { onExerciseDelete(it) }
                exerciseToDelete = null
                exerciseNameToDelete = ""
            }
        )
    }
}

@Composable
private fun SwipeableExerciseCard(
    workoutExerciseId: String,
    exercise: Exercise,
    workoutExercise: com.example.reptrack.domain.workout.entities.WorkoutExercise,
    bestSet: com.example.reptrack.domain.workout.entities.WorkoutSet?,
    onClick: () -> Unit,
    onShowDeleteDialog: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val deleteThreshold = -600f

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Exercise card
        Box(
            modifier = Modifier
                .offset(x = offsetX.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < deleteThreshold) {
                                onShowDeleteDialog()
                                offsetX = 0f
                            } else {
                                offsetX = 0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            if (newOffset <= 0f) {
                                offsetX = newOffset
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable { if (offsetX == 0f) onClick() }
            ) {
                WorkoutExerciseCard(
                    exercise = exercise,
                    workoutExercise = workoutExercise,
                    bestSet = bestSet,
                    muscleGroupColor = MuscleGroupColors.getPrimaryColor(exercise.muscleGroup)
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    exerciseName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Delete Exercise",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to delete \"$exerciseName\"?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    "Delete",
                    color = Color(0xFFEF5350),
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SessionStatusCard(
    status: WorkoutStatus,
    exerciseCount: Int,
    muscleGroups: List<MuscleGroup>
) {
    val statusColor = when (status) {
        WorkoutStatus.PLANNED -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        WorkoutStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        WorkoutStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
    }

    val statusText = when (status) {
        WorkoutStatus.PLANNED -> "Запланировано"
        WorkoutStatus.IN_PROGRESS -> "В процессе"
        WorkoutStatus.COMPLETED -> "Завершено"
    }

    val statusIcon = when (status) {
        WorkoutStatus.PLANNED -> Icons.Default.Edit
        WorkoutStatus.IN_PROGRESS -> Icons.Default.PlayArrow
        WorkoutStatus.COMPLETED -> Icons.Default.CheckCircle
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            // Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        statusText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            // Exercise Count (without badge, larger font)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    formatExerciseCount(exerciseCount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            // Muscle Groups
            if (muscleGroups.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Группы мышц",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    muscleGroups.forEach { muscleGroup ->
                        MuscleGroupChip(muscleGroup = muscleGroup)
                    }
                }
            }
        }
    }
}

@Composable
private fun MuscleGroupChip(muscleGroup: MuscleGroup) {
    val backgroundColor = MuscleGroupColors.getBackgroundColor(muscleGroup)
    val textColor = MuscleGroupColors.getPrimaryColor(muscleGroup)

    val names = mapOf(
        MuscleGroup.CHEST to "Грудь",
        MuscleGroup.BACK to "Спина",
        MuscleGroup.LEGS to "Ноги",
        MuscleGroup.ARMS to "Руки",
        MuscleGroup.ABS to "Пресс",
        MuscleGroup.CARDIO to "Кардио"
    )
    val groupName = names[muscleGroup] ?: muscleGroup.name.lowercase().replace("_", " ")

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = textColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = groupName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun CompleteWorkoutButton(
    onClick: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
            contentDescription = "Завершить тренировку",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            "Завершить тренировку",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatExerciseCount(count: Int): String {
    val lastDigit = count % 10
    val lastTwoDigits = count % 100

    val suffix = when {
        lastTwoDigits in 11..14 -> "упражнений"
        lastDigit == 1 -> "упражнение"
        lastDigit in 2..4 -> "упражнения"
        else -> "упражнений"
    }

    return "$count $suffix"
}
    
