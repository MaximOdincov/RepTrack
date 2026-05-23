package com.example.reptrack.data.seeder

import com.example.reptrack.domain.workout.entities.MuscleGroup
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.entities.TemplateSchedule

/**
 * Comprehensive workout templates with pre-configured exercises and schedules
 * All templates use existing drawable resources from the project
 */
object DefaultTemplates {

    val templates = listOf(
        // ==================== PUSH / PULL / LEGS SPLIT ====================
        WorkoutTemplate(
            id = "push_day",
            name = "Push Day",
            description = "Chest, shoulders and triceps training",
            iconId = "push_day",
            exerciseIds = listOf(
                "chest_bench_press",
                "chest_incline_dumbbell_press",
                "chest_cable_flyes",
                "shoulders_dumbbell_overhead_press",
                "shoulders_lateral_raise",
                "shoulders_front_raise",
                "arms_tricep_pushdown",
                "arms_skull_crushers"
            ),
            iconRes = null,
            iconColor = "#FF6B6B",
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0), // Monday
                week2Days = setOf(3, 6) // Thursday, Sunday
            )
        ),

        WorkoutTemplate(
            id = "pull_day",
            name = "Pull Day",
            description = "Back and biceps training",
            iconId = "pull_day",
            exerciseIds = listOf(
                "back_deadlift",
                "back_barbell_row",
                "back_lat_pulldown",
                "back_dumbbell_row",
                "back_seated_cable_row",
                "back_face_pulls",
                "arms_barbell_curl",
                "arms_hammer_curl",
                "arms_dumbbell_curl"
            ),
            iconRes = null,
            iconColor = "#4ECDC4",
            muscleGroups = listOf(MuscleGroup.BACK, MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(1), // Tuesday
                week2Days = setOf(4) // Friday
            )
        ),

        WorkoutTemplate(
            id = "legs_day",
            name = "Legs Day",
            description = "Complete lower body training",
            iconId = "legs_day",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "legs_leg_press",
                "legs_romanian_deadlift",
                "legs_lunges",
                "legs_leg_extension",
                "legs_leg_curl",
                "legs_calf_raises",
                "abs_crunches",
                "abs_plank"
            ),
            iconRes = null,
            iconColor = "#95E1D3",
            muscleGroups = listOf(MuscleGroup.LEGS, MuscleGroup.ABS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(2), // Wednesday
                week2Days = setOf(5) // Saturday
            )
        ),

        // ==================== 3-DAY FULL BODY ====================
        WorkoutTemplate(
            id = "full_body_3x",
            name = "Full Body 3x Week",
            description = "Full body workout 3 times per week",
            iconId = "full_body",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "chest_bench_press",
                "back_barbell_row",
                "shoulders_dumbbell_overhead_press",
                "arms_dumbbell_curl",
                "arms_tricep_pushdown",
                "legs_lunges",
                "abs_crunches",
                "abs_plank"
            ),
            iconRes = null,
            iconColor = "#FFE66D",
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS,
                MuscleGroup.ARMS, MuscleGroup.ABS
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 2, 4), // Mon, Wed, Fri
                week2Days = setOf(0, 2, 4)
            )
        ),

        // ==================== UPPER / LOWER SPLIT ====================
        WorkoutTemplate(
            id = "upper_body",
            name = "Upper Body",
            description = "Complete upper body training",
            iconId = "upper_body",
            exerciseIds = listOf(
                "chest_bench_press",
                "chest_incline_dumbbell_press",
                "back_barbell_row",
                "back_lat_pulldown",
                "shoulders_dumbbell_overhead_press",
                "shoulders_lateral_raise",
                "arms_barbell_curl",
                "arms_tricep_pushdown",
                "abs_crunches"
            ),
            iconRes = null,
            iconColor = "#A29BFE",
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.ARMS, MuscleGroup.ABS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 3), // Mon, Thu
                week2Days = setOf(1, 4) // Tue, Fri
            )
        ),

        WorkoutTemplate(
            id = "lower_body",
            name = "Lower Body",
            description = "Complete lower body training",
            iconId = "lower_body",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "legs_leg_press",
                "legs_romanian_deadlift",
                "legs_lunges",
                "legs_leg_extension",
                "legs_leg_curl",
                "legs_calf_raises",
                "abs_plank",
                "abs_leg_raises"
            ),
            iconRes = null,
            iconColor = "#95E1D3",
            muscleGroups = listOf(MuscleGroup.LEGS, MuscleGroup.ABS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(1, 4), // Tue, Fri
                week2Days = setOf(2, 5) // Wed, Sat
            )
        ),

        // ==================== FOCUS TEMPLATES ====================
        WorkoutTemplate(
            id = "chest_focus",
            name = "Chest Focus",
            description = "Intensive chest training",
            iconId = "chest_focus",
            exerciseIds = listOf(
                "chest_bench_press",
                "chest_incline_bench_press",
                "chest_decline_bench_press",
                "chest_dumbbell_bench_press",
                "chest_cable_flyes",
                "chest_push_ups",
                "chest_dips",
                "chest_cable_crossover"
            ),
            iconRes = null,
            iconColor = "#FF6B6B",
            muscleGroups = listOf(MuscleGroup.CHEST),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0),
                week2Days = emptySet()
            )
        ),

        WorkoutTemplate(
            id = "back_focus",
            name = "Back Focus",
            description = "Intensive back training",
            iconId = "back_focus",
            exerciseIds = listOf(
                "back_deadlift",
                "back_barbell_row",
                "back_lat_pulldown",
                "back_dumbbell_row",
                "back_t_bar_row",
                "back_seated_cable_row",
                "back_pull_ups",
                "back_face_pulls"
            ),
            iconRes = null,
            iconColor = "#4ECDC4",
            muscleGroups = listOf(MuscleGroup.BACK),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(1),
                week2Days = emptySet()
            )
        ),

        WorkoutTemplate(
            id = "arm_focus",
            name = "Arm Focus",
            description = "Intensive arm training (biceps & triceps)",
            iconId = "arm_focus",
            exerciseIds = listOf(
                "arms_barbell_curl",
                "arms_dumbbell_curl",
                "arms_hammer_curl",
                "arms_preacher_curl",
                "arms_tricep_pushdown",
                "arms_skull_crushers",
                "arms_overhead_extension",
                "arms_rope_pushdown"
            ),
            iconRes = null,
            iconColor = "#FF9F43",
            muscleGroups = listOf(MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(3),
                week2Days = emptySet()
            )
        ),

        WorkoutTemplate(
            id = "leg_focus",
            name = "Leg Focus",
            description = "Intensive leg training",
            iconId = "leg_focus",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "legs_leg_press",
                "legs_hack_squat",
                "legs_romanian_deadlift",
                "legs_lunges",
                "legs_leg_extension",
                "legs_leg_curl",
                "legs_calf_raises",
                "abs_plank"
            ),
            iconRes = null,
            iconColor = "#95E1D3",
            muscleGroups = listOf(MuscleGroup.LEGS, MuscleGroup.ABS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(2),
                week2Days = emptySet()
            )
        ),

        WorkoutTemplate(
            id = "shoulders_focus",
            name = "Shoulders Focus",
            description = "Intensive shoulder training",
            iconId = "shoulders_focus",
            exerciseIds = listOf(
                "shoulders_dumbbell_overhead_press",
                "shoulders_barbell_overhead_press",
                "shoulders_lateral_raise",
                "shoulders_front_raise",
                "shoulders_reverse_flyes",
                "shoulders_rear_delt_fly",
                "shoulders_arnold_press",
                "shoulders_upright_row"
            ),
            iconRes = null,
            iconColor = "#FD79A8",
            muscleGroups = listOf(MuscleGroup.ARMS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(4),
                week2Days = emptySet()
            )
        ),

        WorkoutTemplate(
            id = "abs_focus",
            name = "Abs Focus",
            description = "Intensive core training",
            iconId = "abs_focus",
            exerciseIds = listOf(
                "abs_crunches",
                "abs_plank",
                "abs_leg_raises",
                "abs_hanging_leg_raises",
                "abs_russian_twist",
                "abs_bicycle_crunches",
                "abs_mountain_climbers",
                "abs_cable_crunch",
                "abs_ab_wheel"
            ),
            iconRes = null,
            iconColor = "#FFEAA7",
            muscleGroups = listOf(MuscleGroup.ABS),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(5),
                week2Days = emptySet()
            )
        ),

        // ==================== GOAL-BASED TEMPLATES ====================
        WorkoutTemplate(
            id = "strength",
            name = "Strength Training",
            description = "Focus on heavy compounds - 5x5 rep range",
            iconId = "strength",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "chest_bench_press",
                "back_deadlift",
                "back_barbell_row",
                "shoulders_barbell_overhead_press"
            ),
            iconRes = null,
            iconColor = "#636E72",
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS, MuscleGroup.ARMS
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 2, 4),
                week2Days = setOf(0, 2, 4)
            )
        ),

        WorkoutTemplate(
            id = "hypertrophy",
            name = "Hypertrophy",
            description = "Focus on muscle growth - 8-12 rep range",
            iconId = "hypertrophy",
            exerciseIds = listOf(
                "chest_bench_press",
                "chest_incline_dumbbell_press",
                "chest_cable_flyes",
                "back_barbell_row",
                "back_lat_pulldown",
                "legs_barbell_squat",
                "legs_leg_press",
                "shoulders_dumbbell_overhead_press",
                "shoulders_lateral_raise",
                "arms_barbell_curl",
                "arms_tricep_pushdown"
            ),
            iconRes = null,
            iconColor = "#FF6B6B",
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS,
                MuscleGroup.ARMS
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 1, 2, 3, 4),
                week2Days = setOf(0, 1, 2, 3, 4)
            )
        ),

        WorkoutTemplate(
            id = "endurance",
            name = "Endurance",
            description = "High rep range for muscular endurance",
            iconId = "endurance",
            exerciseIds = listOf(
                "legs_barbell_squat",
                "chest_bench_press",
                "back_barbell_row",
                "shoulders_dumbbell_overhead_press",
                "legs_lunges",
                "chest_push_ups",
                "back_pull_ups",
                "abs_plank",
                "cardio_treadmill"
            ),
            iconRes = null,
            iconColor = "#74B9FF",
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS,
                MuscleGroup.ARMS, MuscleGroup.ABS, MuscleGroup.CARDIO
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(1, 3, 5),
                week2Days = setOf(1, 3, 5)
            )
        ),

        // ==================== EQUIPMENT-SPECIFIC TEMPLATES ====================
        WorkoutTemplate(
            id = "bodyweight",
            name = "Bodyweight Only",
            description = "No equipment needed - perfect for home workouts",
            iconId = "bodyweight",
            exerciseIds = listOf(
                "chest_push_ups",
                "chest_diamond_push_ups",
                "back_pull_ups",
                "back_assisted_pull_up",
                "legs_lunges",
                "legs_bulgarian_split_squat",
                "legs_step_ups",
                "abs_crunches",
                "abs_plank",
                "abs_leg_raises",
                "abs_russian_twist",
                "cardio_jump_rope",
                "cardio_burpees"
            ),
            iconRes = null,
            iconColor = "#55E6C1",
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS,
                MuscleGroup.ABS, MuscleGroup.CARDIO
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 2, 4, 6),
                week2Days = setOf(1, 3, 5)
            )
        ),

        WorkoutTemplate(
            id = "dumbbell_only",
            name = "Dumbbell Only",
            description = "Complete workout with dumbbells only",
            iconId = "dumbbell_only",
            exerciseIds = listOf(
                "chest_dumbbell_bench_press",
                "chest_incline_dumbbell_press",
                "back_dumbbell_row",
                "shoulders_dumbbell_overhead_press",
                "shoulders_lateral_raise",
                "shoulders_front_raise",
                "legs_dumbbell_squat",
                "legs_lunges",
                "legs_romanian_deadlift",
                "arms_dumbbell_curl",
                "arms_hammer_curl",
                "arms_overhead_dumbbell_extension",
                "arms_kickbacks",
                "abs_crunches",
                "abs_plank"
            ),
            iconRes = null,
            iconColor = "#FF9F43",
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS,
                MuscleGroup.ARMS, MuscleGroup.ABS
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 2, 4),
                week2Days = setOf(1, 3, 5)
            )
        ),

        // ==================== CARDIO TEMPLATES ====================
        WorkoutTemplate(
            id = "cardio_mix",
            name = "Cardio Mix",
            description = "Variety of cardio exercises",
            iconId = "cardio_mix",
            exerciseIds = listOf(
                "cardio_treadmill",
                "cardio_running",
                "cardio_cycling",
                "cardio_rowing_machine",
                "cardio_jump_rope"
            ),
            iconRes = null,
            iconColor = "#74B9FF",
            muscleGroups = listOf(MuscleGroup.CARDIO),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(6), // Sunday
                week2Days = setOf(5) // Saturday
            )
        ),

        WorkoutTemplate(
            id = "hiit_workout",
            name = "HIIT Workout",
            description = "High intensity interval training",
            iconId = "hiit",
            exerciseIds = listOf(
                "cardio_burpees",
                "cardio_box_jumps",
                "cardio_kettlebell_swings",
                "cardio_battle_ropes",
                "cardio_sprinting",
                "cardio_jump_rope",
                "abs_mountain_climbers",
                "abs_plank_jacks",
                "chest_push_ups",
                "legs_lunges"
            ),
            iconRes = null,
            iconColor = "#FF6B6B",
            muscleGroups = listOf(
                MuscleGroup.CHEST, MuscleGroup.LEGS, MuscleGroup.ABS, MuscleGroup.CARDIO
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(5),
                week2Days = setOf(6)
            )
        ),

        // ==================== WARM UP / COOL DOWN ====================
        WorkoutTemplate(
            id = "warm_up",
            name = "Warm Up",
            description = "Dynamic stretching and activation",
            iconId = "warm_up",
            exerciseIds = listOf(
                "cardio_treadmill",
                "cardio_cycling",
                "legs_lunges",
                "legs_step_ups",
                "shoulders_arm_circles",
                "shoulders_band_pull_apart",
                "abs_bird_dog",
                "abs_dead_bug"
            ),
            iconRes = null,
            iconColor = "#FFEAA7",
            muscleGroups = listOf(
                MuscleGroup.LEGS, MuscleGroup.ARMS, MuscleGroup.ABS, MuscleGroup.CARDIO
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 1, 2, 3, 4, 5, 6),
                week2Days = setOf(0, 1, 2, 3, 4, 5, 6)
            )
        ),

        WorkoutTemplate(
            id = "cool_down",
            name = "Cool Down",
            description = "Static stretching and recovery",
            iconId = "cool_down",
            exerciseIds = listOf(
                "legs_leg_stretch",
                "chest_chest_stretch",
                "back_back_stretch",
                "shoulders_shoulder_stretch",
                "abs_plank",
                "abs_side_plank",
                "abs_v_sit"
            ),
            iconRes = null,
            iconColor = "#95E1D3",
            muscleGroups = listOf(
                MuscleGroup.LEGS, MuscleGroup.CHEST, MuscleGroup.BACK,
                MuscleGroup.ARMS, MuscleGroup.ABS
            ),
            isCustom = false,
            schedule = TemplateSchedule(
                week1Days = setOf(0, 1, 2, 3, 4, 5, 6),
                week2Days = setOf(0, 1, 2, 3, 4, 5, 6)
            )
        )
    )

    fun getAllTemplates(): List<WorkoutTemplate> = templates.toList()

    /**
     * Get template by ID
     */
    fun getTemplateById(id: String): WorkoutTemplate? {
        return templates.find { it.id == id }
    }

    /**
     * Get templates by muscle group
     */
    fun getTemplatesByMuscleGroup(muscleGroup: MuscleGroup): List<WorkoutTemplate> {
        return templates.filter { muscleGroup in it.muscleGroups }
    }
}