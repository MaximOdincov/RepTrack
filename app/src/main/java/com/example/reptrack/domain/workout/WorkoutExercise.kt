package com.example.reptrack.domain.workout

data class WorkoutExercise(
    val id: String,
    val exerciseId: String,
    val sets: List<WorkoutSet>,
    val restTimerSeconds: Int = 60
)