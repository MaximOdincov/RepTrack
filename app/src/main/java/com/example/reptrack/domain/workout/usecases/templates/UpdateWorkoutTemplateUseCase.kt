package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.logOnFailure
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import com.example.reptrack.domain.workout.usecases.sessions.UpdateLinkedSessionsUseCase

class UpdateWorkoutTemplateUseCase(
    private val templateRepository: WorkoutTemplateRepository,
    private val errorHandler: ErrorHandler,
    private val updateLinkedSessionsUseCase: UpdateLinkedSessionsUseCase
) {
    suspend operator fun invoke(template: WorkoutTemplate): Result<Unit> {
        val result = templateRepository.updateTemplate(template)
            .logOnFailure(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "TemplateDetail",
                    action = "UpdateWorkoutTemplate",
                    entityId = template.id
                )
            )

        // If template was updated successfully, update all linked sessions
        if (result.isSuccess) {
            updateLinkedSessionsUseCase(template.id)
        }

        return result
    }
}
