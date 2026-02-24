package com.example.reptrack.data.local.mappers

import com.example.reptrack.data.local.aggregates.WorkoutExerciseWithSets
import com.example.reptrack.data.local.aggregates.WorkoutSessionWithExercises
import com.example.reptrack.data.local.models.WorkoutExerciseDb
import com.example.reptrack.data.local.models.WorkoutSessionDb
import com.example.reptrack.domain.workout.entities.WorkoutExercise
import com.example.reptrack.domain.workout.entities.WorkoutSession

fun WorkoutSessionWithExercises.toDomain(): WorkoutSession = WorkoutSession(
    id = session.id,
    userId = session.userId,
    date = session.date,
    status = session.status,
    name = session.name,
    durationSeconds = session.durationSeconds,
    exercises = exercises.map { it.toDomain() },
    comment = session.comment
)

fun WorkoutSession.toDb(): WorkoutSessionDb = WorkoutSessionDb(
    id = id,
    userId = userId,
    date = date,
    status = status,
    name = name,
    durationSeconds = durationSeconds,
    comment = comment,
    updatedAt = java.time.LocalDateTime.now(),
    deletedAt = null
)

fun WorkoutExerciseWithSets.toDomain(): WorkoutExercise = WorkoutExercise(
    id = exercise.id,
    exerciseId = exercise.exerciseId,
    sets = sets.map { it.toDomain() },
    restTimerSeconds = exercise.restTimerSeconds
)

fun WorkoutExercise.toDb(workoutSessionId: String): WorkoutExerciseDb = WorkoutExerciseDb(
    id = id,
    workoutSessionId = workoutSessionId,
    exerciseId = exerciseId,
    restTimerSeconds = restTimerSeconds,
    updatedAt = java.time.LocalDateTime.now(),
    deletedAt = null
)
