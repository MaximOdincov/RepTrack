package com.example.reptrack.domain.workout.entities

import androidx.compose.runtime.Immutable

@Immutable
data class WorkoutExercise(
    val id: String,
    val workoutSessionId: String, // ID сессии к которой принадлежит упражнение
    val exerciseId: String, // Для справки, но данные берутся из полей ниже
    // Денормализованные данные (собственная копия)
    val exerciseName: String,
    val muscleGroup: MuscleGroup,
    val exerciseType: ExerciseType,
    val iconRes: Int?,
    val sets: List<WorkoutSet>,
    val restTimerSeconds: Int = 60
)
