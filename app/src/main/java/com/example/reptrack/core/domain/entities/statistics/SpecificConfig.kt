package com.example.reptrack.core.domain.entities.statistics

sealed interface SpecificConfig {
    data class ExerciseLine(
        val exerciseId: String,
        val setConfigs: List<SetConfig>
    ) : SpecificConfig

    data class SetConfig(val setIndex: Int, val color: Long, val visible: Boolean)
}