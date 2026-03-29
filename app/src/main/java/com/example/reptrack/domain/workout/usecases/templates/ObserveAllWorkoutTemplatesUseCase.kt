package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllWorkoutTemplatesUseCase(
    private val templateRepository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(): Flow<List<WorkoutTemplate>> {
        return templateRepository.observeAllTemplates()
    }
}
