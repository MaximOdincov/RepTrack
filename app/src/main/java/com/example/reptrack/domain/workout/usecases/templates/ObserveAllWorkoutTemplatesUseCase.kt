package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.domain.workout.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllWorkoutTemplatesUseCase(
    private val templateRepository: WorkoutTemplateRepository
) {
    operator fun invoke(): Flow<List<WorkoutTemplate>> {
        return templateRepository.observeAllTemplates()
    }
}
