package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.aggregates.WorkoutExerciseWithSets
import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.domain.workout.entities.Exercise

/**
 * Extension functions for mapping Exercise entities between DB and Domain layers.
 */

fun ExerciseDb.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    type = type,
    iconUrl = iconUrl,
    iconColor = iconColor,
    backgroundImageUrl = backgroundImageUrl,
    backgroundColor = backgroundColor,
    isCustom = isCustom
)

fun Exercise.toDb(): ExerciseDb = ExerciseDb(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    type = type,
    iconUrl = iconUrl,
    iconColor = iconColor,
    backgroundImageUrl = backgroundImageUrl,
    backgroundColor = backgroundColor,
    isCustom = isCustom
)
