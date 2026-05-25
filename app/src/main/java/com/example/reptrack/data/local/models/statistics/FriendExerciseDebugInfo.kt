package com.example.reptrack.data.local.models.statistics

import androidx.room.ColumnInfo

data class ExerciseIdNamePair(
    @ColumnInfo(name = "id") val exerciseId: String,
    @ColumnInfo(name = "name") val exerciseName: String
)

data class FriendExerciseDebugInfo(
    @ColumnInfo(name = "exerciseId") val exerciseId: String,
    @ColumnInfo(name = "exerciseName") val exerciseName: String
)