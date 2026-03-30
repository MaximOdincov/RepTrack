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

    // Chest - Soft coral/rose tones
    private object ChestColors {
        val primary = Color(0xFFE57373) // Soft coral red
        val background = Color(0x1AE57373) // 10% opacity
        val iconBackground = Color(0x26E57373) // 15% opacity
    }

    // Back - Calming blue tones
    private object BackColors {
        val primary = Color(0xFF64B5F6) // Soft blue
        val background = Color(0x1A64B5F6) // 10% opacity
        val iconBackground = Color(0x2664B5F6) // 15% opacity
    }

    // Legs - Fresh green tones
    private object LegsColors {
        val primary = Color(0xFF81C784) // Soft green
        val background = Color(0x1A81C784) // 10% opacity
        val iconBackground = Color(0x2681C784) // 15% opacity
    }

    // Arms - Warm orange/peach tones
    private object ArmsColors {
        val primary = Color(0xFFFFB74D) // Soft orange
        val background = Color(0x1AFFB74D) // 10% opacity
        val iconBackground = Color(0x26FFB74D) // 15% opacity
    }

    // Abs - Soft warm pink/rose tones
    private object AbsColors {
        val primary = Color(0xFFF06292) // Soft warm pink
        val background = Color(0x1AF06292) // 10% opacity
        val iconBackground = Color(0x26F06292) // 15% opacity
    }

    // Cardio - Energetic purple tones
    private object CardioColors {
        val primary = Color(0xFFBA68C8) // Soft purple
        val background = Color(0x1ABA68C8) // 10% opacity
        val iconBackground = Color(0x26BA68C8) // 15% opacity
    }
}
