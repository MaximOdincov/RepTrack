package com.example.reptrack.presentation.exercise.list.components

/**
 * Global state holder for muscle group accordion expansion states.
 * This class maintains which muscle groups are expanded/collapsed
 * and can be saved/restored across configuration changes.
 */
class MuscleGroupExpansionState {
    private val expansions = mutableMapOf<String, Boolean>()

    /**
     * Check if a muscle group is currently expanded
     */
    fun isExpanded(muscleGroupName: String): Boolean {
        return expansions[muscleGroupName] ?: false
    }

    /**
     * Toggle the expansion state of a muscle group
     */
    fun toggle(muscleGroupName: String) {
        expansions[muscleGroupName] = !(expansions[muscleGroupName] ?: false)
    }

    /**
     * Set the expansion state of a muscle group
     */
    fun setExpanded(muscleGroupName: String, expanded: Boolean) {
        expansions[muscleGroupName] = expanded
    }

    /**
     * Get a copy of the current expansion state map
     */
    fun getState(): Map<String, Boolean> = expansions.toMap()

    /**
     * Restore expansion state from a saved map
     */
    fun restoreState(state: Map<String, Boolean>) {
        expansions.clear()
        expansions.putAll(state)
    }
}
