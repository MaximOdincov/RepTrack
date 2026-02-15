package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.domain.workout.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkoutTemplateByIdUseCase(
    private val templateRepository: WorkoutTemplateRepository
) {
    operator fun invoke(templateId: String): Flow<WorkoutTemplate?> {
        return templateRepository.observeTemplateById(templateId)
    }
}
