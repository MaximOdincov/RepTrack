package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.models.WorkoutSetDb
import com.example.reptrack.domain.workout.entities.WorkoutSet

fun WorkoutSetDb.toDomain(): WorkoutSet = WorkoutSet(
    id = id,
    index = setOrder,
    weight = weight,
    reps = reps,
    isCompleted = isCompleted
)

fun WorkoutSet.toDb(workoutExerciseId: String): WorkoutSetDb = WorkoutSetDb(
    id = id,
    workoutExerciseId = workoutExerciseId,
    setOrder = index,
    weight = weight,
    reps = reps,
    isCompleted = isCompleted,
    updatedAt = java.time.LocalDateTime.now(),
    deletedAt = null
)
