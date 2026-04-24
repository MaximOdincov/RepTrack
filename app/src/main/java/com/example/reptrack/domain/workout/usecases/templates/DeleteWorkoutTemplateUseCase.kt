package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.logOnFailure
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository

class DeleteWorkoutTemplateUseCase(
    private val templateRepository: WorkoutTemplateRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(templateId: String): Result<Unit> {
        return templateRepository.deleteTemplate(templateId)
            .logOnFailure(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "TemplateList",
                    action = "DeleteWorkoutTemplate",
                    entityId = templateId
                )
            )
    }
}
