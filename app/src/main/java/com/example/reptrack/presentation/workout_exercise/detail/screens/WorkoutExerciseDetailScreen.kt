package com.example.reptrack.presentation.workout_exercise.detail.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.reptrack.presentation.utils.painterResourceSafe
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.R
import com.example.reptrack.presentation.workout_exercise.detail.components.SetCard
import com.example.reptrack.presentation.workout_exercise.detail.stores.WorkoutExerciseDetailStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutExerciseDetailScreen(
    store: WorkoutExerciseDetailStore,
    workoutExerciseId: String,
    onNavigateBack: () -> Unit = {}
) {
    LaunchedEffect(workoutExerciseId) {
        android.util.Log.d("WorkoutExercise", "Screen initialized: workoutExerciseId=$workoutExerciseId")
        store.accept(WorkoutExerciseDetailStore.Intent.Initialize(workoutExerciseId))
    }

    val state by store.states.collectAsState(WorkoutExerciseDetailStore.State())

    val expandedStates = remember {
        mutableStateMapOf<String, Boolean>()
    }

    LaunchedEffect(store) {
        store.labels.collect { label ->
            when (label) {
                is WorkoutExerciseDetailStore.Label.NavigateBack -> onNavigateBack()
                is WorkoutExerciseDetailStore.Label.ShowError -> {
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.exercise?.name ?: "Exercise",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            store.accept(WorkoutExerciseDetailStore.Intent.SaveAndExit)
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    store.accept(WorkoutExerciseDetailStore.Intent.AddSet())
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add set"
                )
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        state.exercise?.let { exercise ->
                            ExerciseInfoCard(
                                exerciseName = exercise.name,
                                muscleGroup = exercise.muscleGroup.toString(),
                                iconRes = exercise.iconRes ?: R.drawable.exercise_default_icon,
                                iconColor = try {
                                    exercise.iconColor?.let { Color(it.toColorInt()) } ?: Color.Black
                                } catch (e: Exception) {
                                    Color.Black
                                },
                                restTimerSeconds = state.workoutExercise?.restTimerSeconds ?: 60,
                                setsCount = state.sets.size
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Sets",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(
                        items = state.sets,
                        key = { it.id },
                        contentType = { "set_item" }
                    ) { set ->
                        if (set.id !in expandedStates) {
                            expandedStates[set.id] = true
                        }

                        val onWeightChanged = remember(set.id) {
                            { weight: Float ->
                                store.accept(WorkoutExerciseDetailStore.Intent.WeightChanged(set.id, weight))
                            }
                        }

                        val onRepsChanged = remember(set.id) {
                            { reps: Int ->
                                store.accept(WorkoutExerciseDetailStore.Intent.RepsChanged(set.id, reps))
                            }
                        }

                        val onDelete = remember(set.id) {
                            {
                                store.accept(WorkoutExerciseDetailStore.Intent.RemoveSet(set.id))
                            }
                        }

                        val onToggleExpanded = remember(set.id) {
                            {
                                val current = expandedStates[set.id] ?: true
                                expandedStates[set.id] = !current
                            }
                        }

                        SetCard(
                            set = set,
                            isExpanded = expandedStates[set.id] ?: true,
                            onToggleExpanded = onToggleExpanded,
                            onWeightChanged = onWeightChanged,
                            onRepsChanged = onRepsChanged,
                            onDelete = onDelete
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (state.sets.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No sets yet",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap + to add your first set",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseInfoCard(
    exerciseName: String,
    muscleGroup: String,
    iconRes: Int,
    iconColor: Color,
    restTimerSeconds: Int,
    setsCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(iconColor.copy(alpha = 0.2f))
                        .blur(radius = 0.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Проверяем что iconRes не равен 0 (0 - не валидный resource ID)
                    if (iconRes != 0) {
                        Icon(
                            painter = painterResourceSafe(id = iconRes),
                            contentDescription = "Exercise icon",
                            modifier = Modifier.size(48.dp),
                            tint = iconColor
                        )
                    } else {
                        Text(
                            text = "?",
                            fontSize = 32.sp,
                            color = iconColor.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = exerciseName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = muscleGroup,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$setsCount Sets",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

