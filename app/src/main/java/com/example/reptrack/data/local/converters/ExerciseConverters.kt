package com.example.reptrack.data.local.converters

import androidx.room.TypeConverter
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup

/**
 * Type converters for Exercise-related enum types
 */
class ExerciseConverters {

    @TypeConverter
    fun fromMuscleGroup(muscleGroup: MuscleGroup): String {
        return muscleGroup.name
    }

    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup {
        return try {
            MuscleGroup.valueOf(value)
        } catch (e: IllegalArgumentException) {
            MuscleGroup.CHEST // Default fallback
        }
    }

    @TypeConverter
    fun fromExerciseType(exerciseType: ExerciseType): String {
        return exerciseType.name
    }

    @TypeConverter
    fun toExerciseType(value: String): ExerciseType {
        return try {
            ExerciseType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ExerciseType.WEIGHT_REPS // Default fallback
        }
    }
}
