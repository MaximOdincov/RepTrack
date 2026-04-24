package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndLog
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkoutTemplateByIdUseCase(
    private val templateRepository: WorkoutTemplateRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(templateId: String): Flow<WorkoutTemplate?> {
        return templateRepository.observeTemplateById(templateId)
            .catchAndLog(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "TemplateDetail",
                    action = "ObserveWorkoutTemplateById",
                    entityId = templateId
                )
            )
    }
}
