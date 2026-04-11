package com.example.reptrack.domain.workout.entities

import androidx.compose.runtime.Immutable

@Immutable
data class WorkoutExercise(
    val id: String,
    val workoutSessionId: String, // ID сессии к которой принадлежит упражнение
    val exerciseId: String,
    val sets: List<WorkoutSet>,
    val restTimerSeconds: Int = 60
)
