package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository

class UpdateWorkoutTemplateUseCase(
    private val templateRepository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(template: WorkoutTemplate): Result<Unit> {
        return templateRepository.updateTemplate(template)
    }
}
