package com.example.reptrack.domain.workout.entities

import androidx.compose.runtime.Immutable

@Immutable
data class WorkoutExercise(
    val id: String,
    val exerciseId: String,
    val sets: List<WorkoutSet>,
    val restTimerSeconds: Int = 60
)