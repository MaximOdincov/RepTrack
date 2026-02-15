package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository

class DeleteWorkoutTemplateUseCase(
    private val templateRepository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(templateId: String): Result<Unit> {
        return templateRepository.deleteTemplate(templateId)
    }
}
