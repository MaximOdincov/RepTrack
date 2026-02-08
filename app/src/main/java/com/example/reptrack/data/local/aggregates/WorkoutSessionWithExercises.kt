package com.example.reptrack.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Relation
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.data.local.models.WorkoutSessionDb

data class WorkoutSessionWithExercises(
    @Embedded val session: WorkoutSessionDb,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutSessionId",
        entity = WorkoutExerciseDb::class
    )
    val exercises: List<WorkoutExerciseWithSets>
)
