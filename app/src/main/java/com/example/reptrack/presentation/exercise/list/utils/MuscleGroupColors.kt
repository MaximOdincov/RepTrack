package com.example.reptrack.presentation.exercise.list.utils

import androidx.compose.ui.graphics.Color

/**
 * Color palette for muscle groups
 * Soft, muted tones that fit the exercise design
 */
object MuscleGroupColors {

    /**
     * Get primary color for muscle group
     */
    fun getPrimaryColor(muscleGroup: com.example.reptrack.domain.workout.entities.MuscleGroup): Color {
        return when (muscleGroup) {
            com.example.reptrack.domain.workout.entities.MuscleGroup.CHEST -> ChestColors.primary
            com.example.reptrack.domain.workout.entities.MuscleGroup.BACK -> BackColors.primary
            com.example.reptrack.domain.workout.entities.MuscleGroup.LEGS -> LegsColors.primary
            com.example.reptrack.domain.workout.entities.MuscleGroup.ARMS -> ArmsColors.primary
            com.example.reptrack.domain.workout.entities.MuscleGroup.ABS -> AbsColors.primary
            com.example.reptrack.domain.workout.entities.MuscleGroup.CARDIO -> CardioColors.primary
        }
    }

    /**
     * Get background color for muscle group (with alpha)
     */
    fun getBackgroundColor(muscleGroup: com.example.reptrack.domain.workout.entities.MuscleGroup): Color {
        return when (muscleGroup) {
            com.example.reptrack.domain.workout.entities.MuscleGroup.CHEST -> ChestColors.background
            com.example.reptrack.domain.workout.entities.MuscleGroup.BACK -> BackColors.background
            com.example.reptrack.domain.workout.entities.MuscleGroup.LEGS -> LegsColors.background
            com.example.reptrack.domain.workout.entities.MuscleGroup.ARMS -> ArmsColors.background
            com.example.reptrack.domain.workout.entities.MuscleGroup.ABS -> AbsColors.background
            com.example.reptrack.domain.workout.entities.MuscleGroup.CARDIO -> CardioColors.background
        }
    }

    /**
     * Get icon background color for muscle group (with alpha)
     */
    fun getIconBackgroundColor(muscleGroup: com.example.reptrack.domain.workout.entities.MuscleGroup): Color {
        return when (muscleGroup) {
            com.example.reptrack.domain.workout.entities.MuscleGroup.CHEST -> ChestColors.iconBackground
            com.example.reptrack.domain.workout.entities.MuscleGroup.BACK -> BackColors.iconBackground
            com.example.reptrack.domain.workout.entities.MuscleGroup.LEGS -> LegsColors.iconBackground
            com.example.reptrack.domain.workout.entities.MuscleGroup.ARMS -> ArmsColors.iconBackground
            com.example.reptrack.domain.workout.entities.MuscleGroup.ABS -> AbsColors.iconBackground
            com.example.reptrack.domain.workout.entities.MuscleGroup.CARDIO -> CardioColors.iconBackground
        }
    }

    // Chest - Bright coral (matches DefaultExercises.kt)
    private object ChestColors {
        val primary = Color(0xFFFF6B6B) // Bright coral red
        val background = Color(0x1AFF6B6B) // 10% opacity
        val iconBackground = Color(0x26FF6B6B) // 15% opacity
    }

    // Back - Bright turquoise (matches DefaultExercises.kt)
    private object BackColors {
        val primary = Color(0xFF4ECDC4) // Bright turquoise
        val background = Color(0x1A4ECDC4) // 10% opacity
        val iconBackground = Color(0x264ECDC4) // 15% opacity
    }

    // Legs - Bright blue (matches DefaultExercises.kt)
    private object LegsColors {
        val primary = Color(0xFF45B7D1) // Bright blue
        val background = Color(0x1A45B7D1) // 10% opacity
        val iconBackground = Color(0x2645B7D1) // 15% opacity
    }

    // Arms - Bright purple (matches DefaultExercises.kt)
    private object ArmsColors {
        val primary = Color(0xFF9B59B6) // Bright purple
        val background = Color(0x1A9B59B6) // 10% opacity
        val iconBackground = Color(0x269B59B6) // 15% opacity
    }

    // Abs - Bright orange (matches DefaultExercises.kt)
    private object AbsColors {
        val primary = Color(0xFFF39C12) // Bright orange
        val background = Color(0x1AF39C12) // 10% opacity
        val iconBackground = Color(0x26F39C12) // 15% opacity
    }

    // Cardio - Bright green (matches DefaultExercises.kt)
    private object CardioColors {
        val primary = Color(0xFF2ECC71) // Bright green
        val background = Color(0x1A2ECC71) // 10% opacity
        val iconBackground = Color(0x262ECC71) // 15% opacity
    }
}
