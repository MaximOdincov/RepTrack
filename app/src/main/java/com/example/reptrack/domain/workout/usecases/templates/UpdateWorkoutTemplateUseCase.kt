package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.logOnFailure
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository

class UpdateWorkoutTemplateUseCase(
    private val templateRepository: WorkoutTemplateRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(template: WorkoutTemplate): Result<Unit> {
        return templateRepository.updateTemplate(template)
            .logOnFailure(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "TemplateDetail",
                    action = "UpdateWorkoutTemplate",
                    entityId = template.id
                )
            )
    }
}
