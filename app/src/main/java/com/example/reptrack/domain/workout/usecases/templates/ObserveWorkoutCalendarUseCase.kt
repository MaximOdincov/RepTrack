package com.example.reptrack.domain.workout.usecases.templates

import com.example.reptrack.domain.workout.WorkoutTemplate
import com.example.reptrack.domain.workout.repositories.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkoutCalendarUseCase(
    private val templateRepository: WorkoutTemplateRepository
) {
    /**
     * @param dayOfWeek День недели (0 = Понедельник, 6 = Воскресенье)
     * @param isSecondWeek Вторая ли неделя (для ротирующихся расписаний)
     */
    operator fun invoke(
        dayOfWeek: Int,
        isSecondWeek: Boolean = false
    ): Flow<List<WorkoutTemplate>> {
        return templateRepository.observeTemplatesByDayOfWeek(dayOfWeek, isSecondWeek)
    }
}
