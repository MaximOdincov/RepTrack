package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.domain.workout.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository

class CreateWorkoutTemplateUseCase(
    private val templateRepository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(template: WorkoutTemplate): Result<Unit> {
        return templateRepository.createTemplate(template)
    }
}
