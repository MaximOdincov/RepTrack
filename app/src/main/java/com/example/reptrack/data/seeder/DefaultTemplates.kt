package com.example.reptrack.data.seeder

import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.entities.TemplateSchedule

/**
 * Realistic workout templates for different training styles
 */
object DefaultTemplates {

    // Цвета для шаблонов (синхронизированы с упражнениями)
    private const val COLOR_PUSH = "#FF6B6B"      // ярко-коралловый (грудь)
    private const val COLOR_PULL = "#4ECDC4"       // яркий бирюзовый (спина)
    private const val COLOR_LEGS = "#45B7D1"       // ярко-голубой (ноги)
    private const val COLOR_FULL = "#9B59B6"       // яркий фиолетовый
    private const val COLOR_UPPER = "#9B59B6"      // яркий фиолетовый (руки)
    private const val COLOR_CARDIO = "#2ECC71"     // ярко-зеленый (кардио)

    val templates = listOf(

        // ==================== PUSH / PULL / LEGS (КЛАССИЧЕСКИЙ СПЛИТ) ====================
        WorkoutTemplate(
            id = "push_day",
            name = "Жимовый день",
            description = "Грудь, плечи, трицепс",
            iconId = "push_day",
            exerciseIds = listOf(
                "chest_bench_press",
                "chest_incline_dumbbell_press",
                "chest_dumbbell_flyes",
                "shoulders_dumbbell_lateral_raise",
                "shoulders_arnold_press",
                "arms_tricep_pushdown",
                "arms_overhead_dumbbell_extension"
            ),
            iconRes = null,
            iconColor = COLOR_PUSH,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 3),      // ПН, ЧТ
                week2Days = setOf(0, 3)
            )
        ),

        WorkoutTemplate(
            id = "pull_day",
            name = "Тяговый день",
            description = "Спина, бицепс, задняя дельта",
            iconId = "pull_day",
            exerciseIds = listOf(
                "back_pull_ups",
                "back_barbell_row",
                "back_lat_pulldown",
                "back_dumbbell_row",
                "back_face_pulls",
                "arms_barbell_curl",
                "arms_hammer_curl"
            ),
            iconRes = null,
            iconColor = COLOR_PULL,
            muscleGroups = listOf(MuscleGroup.BACK, MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(1, 4),      // ВТ, ПТ
                week2Days = setOf(1, 4)
            )
        ),

        WorkoutTemplate(
            id = "legs_day",
            name = "Ножный день",
            description = "Ноги + пресс",
            iconId = "legs_day",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "legs_leg_press",
                "back_romanian_deadlift",
                "legs_leg_extension",
                "legs_lying_leg_curl",
                "legs_standing_calf_raises",
                "abs_plank",
                "abs_leg_raises"
            ),
            iconRes = null,
            iconColor = COLOR_LEGS,
            muscleGroups = listOf(MuscleGroup.LEGS, MuscleGroup.ABS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(2, 5),      // СР, СБ
                week2Days = setOf(2, 5)
            )
        ),

        // ==================== FULL BODY (ДЛЯ НОВИЧКОВ) ====================
        WorkoutTemplate(
            id = "full_body_beginner",
            name = "Фуллбоди",
            description = "3 раза в неделю, базовые движения",
            iconId = "full_body",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "chest_bench_press",
                "back_barbell_row",
                "shoulders_dumbbell_lateral_raise",
                "arms_dumbbell_curl",
                "arms_tricep_pushdown",
                "abs_crunches"
            ),
            iconRes = null,
            iconColor = COLOR_FULL,
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS,
                MuscleGroup.ARMS, MuscleGroup.ABS
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 2, 4),   // ПН, СР, ПТ
                week2Days = setOf(0, 2, 4)
            )
        ),

        // ==================== UPPER / LOWER (ПРОДВИНУТЫЙ) ====================
        WorkoutTemplate(
            id = "upper_body",
            name = "Верх тела",
            description = "Грудь, спина, плечи, руки",
            iconId = "upper_body",
            exerciseIds = listOf(
                "chest_bench_press",
                "chest_incline_dumbbell_press",
                "back_pull_ups",
                "back_barbell_row",
                "shoulders_barbell_overhead_press",
                "shoulders_dumbbell_lateral_raise",
                "arms_barbell_curl",
                "arms_tricep_pushdown"
            ),
            iconRes = null,
            iconColor = COLOR_UPPER,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 3),      // ПН, ЧТ
                week2Days = setOf(0, 3)
            )
        ),

        WorkoutTemplate(
            id = "lower_body",
            name = "Низ тела",
            description = "Квадрицепсы, бицепс бедра, ягодицы, икры",
            iconId = "lower_body",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "legs_leg_press",
                "back_romanian_deadlift",
                "legs_lunges",
                "legs_leg_extension",
                "legs_lying_leg_curl",
                "legs_calf_raises",
                "abs_hanging_leg_raises"
            ),
            iconRes = null,
            iconColor = COLOR_LEGS,
            muscleGroups = listOf(MuscleGroup.LEGS, MuscleGroup.ABS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(1, 4),      // ВТ, ПТ
                week2Days = setOf(1, 4)
            )
        ),

        // ==================== СПЕЦИАЛИЗИРОВАННЫЕ ТРЕНИРОВКИ ====================
        WorkoutTemplate(
            id = "chest_triceps",
            name = "Грудь + Трицепс",
            description = "Фокус на грудные и трицепс",
            iconId = "chest_focus",
            exerciseIds = listOf(
                "chest_bench_press",
                "chest_incline_dumbbell_press",
                "chest_dips",
                "chest_cable_flyes",
                "chest_push_ups",
                "arms_tricep_pushdown",
                "arms_skull_crushers"
            ),
            iconRes = null,
            iconColor = COLOR_PUSH,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0),
                week2Days = setOf(3)
            )
        ),

        WorkoutTemplate(
            id = "back_biceps",
            name = "Спина + Бицепс",
            description = "Фокус на широчайшие и бицепс",
            iconId = "back_focus",
            exerciseIds = listOf(
                "back_pull_ups",
                "back_barbell_row",
                "back_lat_pulldown",
                "back_dumbbell_row",
                "back_seated_cable_row",
                "arms_barbell_curl",
                "arms_hammer_curl",
                "arms_preacher_curl"
            ),
            iconRes = null,
            iconColor = COLOR_PULL,
            muscleGroups = listOf(MuscleGroup.BACK, MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(1),
                week2Days = setOf(4)
            )
        ),

        WorkoutTemplate(
            id = "shoulders_arms",
            name = "Плечи + Руки",
            description = "Дельты, бицепс, трицепс",
            iconId = "shoulders_focus",
            exerciseIds = listOf(
                "shoulders_barbell_overhead_press",
                "shoulders_dumbbell_lateral_raise",
                "shoulders_dumbbell_front_raise",
                "shoulders_rear_delt_fly",
                "shoulders_upright_row",
                "arms_dumbbell_curl",
                "arms_tricep_pushdown"
            ),
            iconRes = null,
            iconColor = COLOR_UPPER,
            muscleGroups = listOf(MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(3),
                week2Days = emptySet()
            )
        ),

        WorkoutTemplate(
            id = "legs_glutes",
            name = "Ноги + Ягодицы",
            description = "Фокус на нижнюю часть тела",
            iconId = "leg_focus",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "legs_leg_press",
                "back_romanian_deadlift",
                "legs_hip_thrust",
                "legs_glute_bridge",
                "legs_lunges",
                "legs_bulgarian_split_squat",
                "legs_calf_raises"
            ),
            iconRes = null,
            iconColor = COLOR_LEGS,
            muscleGroups = listOf(MuscleGroup.LEGS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(2),
                week2Days = setOf(5)
            )
        ),

        WorkoutTemplate(
            id = "core_strength",
            name = "Кор и пресс",
            description = "Укрепление кора и пресса",
            iconId = "abs_focus",
            exerciseIds = listOf(
                "abs_plank",
                "abs_side_plank",
                "abs_leg_raises",
                "abs_hanging_leg_raises",
                "abs_russian_twist",
                "abs_crunches",
                "abs_dead_bug",
                "abs_toe_touches"
            ),
            iconRes = null,
            iconColor = COLOR_CARDIO,
            muscleGroups = listOf(MuscleGroup.ABS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(6),      // ВС
                week2Days = emptySet()
            )
        ),

        // ==================== КАРДИО ====================
        WorkoutTemplate(
            id = "cardio_conditioning",
            name = "Кардио",
            description = "Развитие выносливости",
            iconId = "cardio_mix",
            exerciseIds = listOf(
                "cardio_running",
                "cardio_cycling",
                "cardio_rowing_machine",
                "cardio_jump_rope"
            ),
            iconRes = null,
            iconColor = COLOR_CARDIO,
            muscleGroups = listOf(MuscleGroup.CARDIO),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(6),      // ВС
                week2Days = setOf(6)
            )
        ),

        WorkoutTemplate(
            id = "hiit_workout",
            name = "ВИИТ тренировка",
            description = "Высокоинтенсивный интервальный тренинг",
            iconId = "hiit",
            exerciseIds = listOf(
                "cardio_burpees",
                "cardio_box_jumps",
                "cardio_battle_ropes",
                "cardio_sprinting",
                "cardio_jump_rope",
                "abs_mountain_climbers"
            ),
            iconRes = null,
            iconColor = COLOR_CARDIO,
            muscleGroups = listOf(MuscleGroup.CARDIO, MuscleGroup.ABS, MuscleGroup.LEGS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(5),      // СБ
                week2Days = setOf(5)
            )
        ),

        // ==================== ДОМАШНИЕ ТРЕНИРОВКИ ====================
        WorkoutTemplate(
            id = "home_bodyweight",
            name = "Домашняя тренировка",
            description = "Без оборудования, для дома",
            iconId = "bodyweight",
            exerciseIds = listOf(
                "chest_push_ups",
                "back_pull_ups",
                "legs_lunges",
                "legs_bulgarian_split_squat",
                "legs_step_ups",
                "abs_crunches",
                "abs_plank",
                "abs_leg_raises",
                "cardio_burpees",
                "cardio_jump_rope"
            ),
            iconRes = null,
            iconColor = COLOR_FULL,
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS,
                MuscleGroup.ABS, MuscleGroup.CARDIO
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 2, 4),
                week2Days = setOf(1, 3, 5)
            )
        )
    )

    fun getAllTemplates(): List<WorkoutTemplate> = templates.toList()

    fun getTemplateById(id: String): WorkoutTemplate? {
        return templates.find { it.id == id }
    }

    fun getTemplatesByMuscleGroup(muscleGroup: MuscleGroup): List<WorkoutTemplate> {
        return templates.filter { muscleGroup in it.muscleGroups }
    }
}