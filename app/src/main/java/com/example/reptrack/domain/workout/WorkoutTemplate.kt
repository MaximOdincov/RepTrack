package com.example.reptrack.domain.workout

data class WorkoutTemplate(
    val id: String,
    val name: String,
    val iconId: String?,
    val exerciseIds: List<String>,
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