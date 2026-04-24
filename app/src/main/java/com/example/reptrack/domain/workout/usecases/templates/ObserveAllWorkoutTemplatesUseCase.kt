package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.core.error.ErrorHandler
import com.example.reptrack.core.error.model.ErrorContext
import com.example.reptrack.core.extensions.catchAndLog
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllWorkoutTemplatesUseCase(
    private val templateRepository: WorkoutTemplateRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(): Flow<List<WorkoutTemplate>> {
        return templateRepository.observeAllTemplates()
            .catchAndLog(
                errorHandler = errorHandler,
                context = ErrorContext(
                    screen = "TemplateList",
                    action = "ObserveAllWorkoutTemplates"
                )
            )
    }
}
