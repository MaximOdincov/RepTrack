package com.example.reptrack.data.local.models.statistics

import androidx.room.ColumnInfo

data class FriendExerciseDebugDetails(
    @ColumnInfo(name = "sessionId") val sessionId: String,
    @ColumnInfo(name = "sessionStatus") val sessionStatus: String,
    @ColumnInfo(name = "exerciseId") val workoutExerciseId: String,
    @ColumnInfo(name = "exerciseRefId") val exerciseRefId: String,
    @ColumnInfo(name = "setId") val setId: String,
    @ColumnInfo(name = "setCompleted") val setCompleted: Boolean,
    @ColumnInfo(name = "weight") val weight: Float?
)