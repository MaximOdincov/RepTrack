package com.example.reptrack.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Relation
import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.WorkoutTemplateDb

/**
 * Aggregate for workout template with exercises in correct order.
 * Unlike WorkoutTemplateWithExercises which uses @Relation without ordering,
 * this should be loaded via a custom query with ORDER BY.
 */
data class TemplateWithOrderedExercises(
    @Embedded val template: WorkoutTemplateDb,
    val exerciseIds: List<String>  // Ordered list of exercise IDs
)
