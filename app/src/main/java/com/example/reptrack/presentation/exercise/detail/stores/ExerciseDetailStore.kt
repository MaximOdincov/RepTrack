package com.example.reptrack.presentation.exercise.detail.stores

import com.arkivanov.mvikotlin.core.store.Store
import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.navigation.ExerciseDetailMode

/**
 * Store for Exercise Detail screen
 */
interface ExerciseDetailStore : Store<ExerciseDetailStore.Intent, ExerciseDetailStore.State, ExerciseDetailStore.Label> {

    sealed interface Intent {
        data class Initialize(val exerciseId: String, val mode: ExerciseDetailMode) : Intent
        data class NameChanged(val name: String) : Intent
        data class MuscleGroupChanged(val group: MuscleGroup) : Intent
        object SaveAndExit : Intent
        object OpenCustomizationSheet : Intent
        object CloseCustomizationSheet : Intent
        data class IconSelected(val iconRes: Int) : Intent
        data class ColorSelected(val color: String) : Intent
        data class SheetModeChanged(val mode: CustomizationSheetMode) : Intent
    }

    data class State(
        val exerciseId: String = "",
        val name: String = "",
        val muscleGroup: MuscleGroup = MuscleGroup.CHEST,
        val iconRes: Int? = null,
        val iconColor: String? = null,

        // Bottom sheet state
        val isCustomizationSheetVisible: Boolean = false,
        val sheetMode: CustomizationSheetMode = CustomizationSheetMode.ICON,

        // Draft values (changes in bottom sheet)
        val draftIconRes: Int? = null,
        val draftIconColor: String? = null,

        // Loading/Saving
        val isLoading: Boolean = false,
        val isSaving: Boolean = false
    ) {
        companion object {
            val Default = State()
        }
    }

    sealed interface Label {
        data class ShowError(val message: String) : Label
        data class ShowSavedToast(val message: String) : Label
        object NavigateBack : Label
    }
}

/**
 * Mode for customization bottom sheet
 */
enum class CustomizationSheetMode {
    ICON,
    COLOR
}
