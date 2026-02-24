package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.domain.workout.entities.Exercise

/**
 * Extension functions for mapping Exercise entities between DB and Domain layers.
 */

fun ExerciseDb.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    type = type,
    iconRes = iconRes,
    iconColor = iconColor,
    backgroundRes = backgroundRes,
    backgroundColor = backgroundColor,
    isCustom = isCustom
)

fun Exercise.toDb(): ExerciseDb = ExerciseDb(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    type = type,
    iconRes = iconRes,
    iconColor = iconColor,
    backgroundRes = backgroundRes,
    backgroundColor = backgroundColor,
    isCustom = isCustom
)
