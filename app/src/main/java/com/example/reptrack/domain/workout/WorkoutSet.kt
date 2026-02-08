package com.example.reptrack.domain.workout

data class WorkoutSet(
    val id: String,
    val index: Int,           // Порядковый номер (1, 2, 3...)
    val weight: Float?,       // Вес (может быть null если это кардио)
    val reps: Int?,           // Повторы
    val isCompleted: Boolean  // Чекбокс выполнения
)
