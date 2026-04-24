package com.example.reptrack.presentation.exercise.list.components

import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.MuscleGroup

/**
 * Get icon resource for muscle group
 */
fun getMuscleGroupIcon(muscleGroup: MuscleGroup): Int {
    return when (muscleGroup) {
        MuscleGroup.CHEST -> R.drawable.muscle_icon_chest
        MuscleGroup.BACK -> R.drawable.muscle_icon_back
        MuscleGroup.LEGS -> R.drawable.muscle_icon_legs
        MuscleGroup.ARMS -> R.drawable.muscle_icon_arms
        MuscleGroup.ABS -> R.drawable.muscle_icon_abs
        MuscleGroup.CARDIO -> R.drawable.muscle_icon_cardio
    }
}

/**
 * Get display name for muscle group
 */
fun getMuscleGroupName(muscleGroup: MuscleGroup): String {
    return when (muscleGroup) {
        MuscleGroup.CHEST -> "Chest"
        MuscleGroup.BACK -> "Back"
        MuscleGroup.LEGS -> "Legs"
        MuscleGroup.ARMS -> "Arms"
        MuscleGroup.ABS -> "Abs"
        MuscleGroup.CARDIO -> "Cardio"
    }
}
