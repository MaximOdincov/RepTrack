package com.example.reptrack.data.local.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reptrack.domain.workout.ExerciseType
import com.example.reptrack.domain.workout.MuscleGroup
import java.time.LocalDateTime

@Entity(tableName = "exercise")
data class ExerciseDb @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val iconUrl: String?,
    val iconColor: String?,         
    val backgroundImageUrl: String?,
    val backgroundColor: String?,
    val isCustom: Boolean,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)
