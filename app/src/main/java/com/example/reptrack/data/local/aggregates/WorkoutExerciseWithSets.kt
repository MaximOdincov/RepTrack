package com.example.reptrack.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Relation
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.data.local.models.WorkoutSetDb

data class WorkoutExerciseWithSets(
    @Embedded val exercise: WorkoutExerciseDb,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutExerciseId"
    )
    val sets: List<WorkoutSetDb>
)