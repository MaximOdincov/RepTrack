package com.example.reptrack.core.data.local.aggregates

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.reptrack.core.data.local.models.ExerciseDb
import com.example.reptrack.core.data.local.models.TemplateExerciseDb
import com.example.reptrack.core.data.local.models.WorkoutTemplateDb

data class WorkoutTemplateWithExercises(
    @Embedded val template: WorkoutTemplateDb,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TemplateExerciseDb::class,
            parentColumn = "templateId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<ExerciseDb>
)
