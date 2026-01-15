package com.example.reptrack.core.domain.entities

import java.time.LocalDateTime

data class WorkoutSession(
    val id: String,
    val userId: String,
    val date: LocalDateTime,
    val status: WorkoutStatus,
    val name: String,
    val durationSeconds: Long,
    val exercises: List<WorkoutExercise>,
    val comment: String?
)