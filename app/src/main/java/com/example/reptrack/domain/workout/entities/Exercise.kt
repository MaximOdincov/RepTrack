package com.example.reptrack.domain.workout.entities

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val iconUrl: String?,
    val iconColor: String?,        
    val backgroundImageUrl: String?,
    val backgroundColor: String?,
    val isCustom: Boolean
)
