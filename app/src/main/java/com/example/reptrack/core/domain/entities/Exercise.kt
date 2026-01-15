package com.example.reptrack.core.domain.entities

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val iconUrl: String?,
    val isCustom: Boolean
)
