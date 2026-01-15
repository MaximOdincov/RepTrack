package com.example.reptrack.core.domain.entities

data class WorkoutExercise(
    val id: String,
    val exerciseId: String,
    val sets: List<WorkoutSet>,
    val restTimerSeconds: Int = 60
)