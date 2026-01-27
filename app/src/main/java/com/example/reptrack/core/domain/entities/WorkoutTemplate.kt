package com.example.reptrack.core.domain.entities

data class WorkoutTemplate(
    val id: String,
    val name: String,
    val iconId: String?,
    val exerciseIds: List<String>
)