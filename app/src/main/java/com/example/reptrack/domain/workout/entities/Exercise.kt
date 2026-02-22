package com.example.reptrack.domain.workout.entities

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val iconRes: Int?,
    val iconColor: String?,
    val backgroundRes: Int?,
    val backgroundColor: String?,
    val isCustom: Boolean
)
