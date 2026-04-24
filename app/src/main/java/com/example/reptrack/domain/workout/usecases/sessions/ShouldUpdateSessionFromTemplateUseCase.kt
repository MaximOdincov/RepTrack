package com.example.reptrack.domain.workout.usecases.sessions

import com.example.reptrack.domain.workout.entities.WorkoutSession
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.entities.WorkoutStatus

/**
 * Use case для определения необходимости обновления сессии из шаблона
 * Возвращает true если сессию нужно перезаписать
 */
class ShouldUpdateSessionFromTemplateUseCase {
    operator fun invoke(
        session: WorkoutSession?,
        template: WorkoutTemplate?
    ): Boolean {
        // Если нет шаблона - не обновляем
        if (template == null) return false

        // Если нет сессии - нужно создать
        if (session == null) return true

        // Если сессия в прогрессе/completed/overdue - не обновляем
        if (session.status != WorkoutStatus.PLANNED) return false

        // Сравниваем упражнения
        val sessionExerciseIds = session.exercises.map { it.exerciseId }
        val templateExerciseIds = template.exerciseIds

        // Если разное количество или порядок - обновляем
        return sessionExerciseIds != templateExerciseIds
    }
}
