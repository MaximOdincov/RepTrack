package com.example.reptrack.domain.workout.entities

/**
 * Сущность шаблона тренировки
 *
 * @param id Уникальный идентификатор шаблона
 * @param name Название шаблона
 * @param description Описание шаблона
 * @param iconId Идентификатор иконки (строка для совместимости с существующим кодом)
 * @param exerciseIds Список ID упражнений в шаблоне
 * @param iconRes Ресурс иконки для Compose (nullable)
 * @param iconColor Цвет иконки в формате HEX (nullable)
 * @param muscleGroups Список задействованных групп мышц
 * @param isCustom Является ли шаблон пользовательским
 * @param schedule График тренировок (для календаря)
 */
data class WorkoutTemplate(
    val id: String,
    val name: String,
    val description: String = "",
    val iconId: String? = null,
    val exerciseIds: List<String> = emptyList(),
    val iconRes: Int? = null,
    val iconColor: String? = null,
    val muscleGroups: List<MuscleGroup> = emptyList(),
    val isCustom: Boolean = true,
    val schedule: TemplateSchedule? = null
)

/**
 * График тренировок для шаблона
 * @param week1Days Дни недели для первой недели (MON-SUN, где MON=0, SUN=6)
 * @param week2Days Дни недели для второй недели
 */
data class TemplateSchedule(
    val week1Days: Set<Int>,
    val week2Days: Set<Int>
) {
    companion object {
        const val MONDAY = 0
        const val TUESDAY = 1
        const val WEDNESDAY = 2
        const val THURSDAY = 3
        const val FRIDAY = 4
        const val SATURDAY = 5
        const val SUNDAY = 6
    }
}