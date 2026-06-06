package com.example.reptrack.data.seeder

import android.content.Context

/**
 * Maps exercise IDs to drawable resource names
 * Uses existing drawable resources from the project
 */
object ExerciseIconMapper {

    // Константы для иконок (чтобы не дублировать строки)
    private const val ICON_BARBELL = "barbell_energy"
    private const val ICON_DUMBBELL = "dumbell"
    private const val ICON_DUMBBELL_HAND = "dumbel_with_a_hand"
    private const val ICON_BICEPS = "bic_dumbell"
    private const val ICON_BENCH = "bench_press"
    private const val ICON_BACK = "back_muscles"
    private const val ICON_LEG = "leg_push"
    private const val ICON_ABS = "abs"
    private const val ICON_FITNESS = "fitness"
    private const val ICON_WEIGHTS = "weights"
    private const val ICON_ROWING = "rowing"
    private const val ICON_TREADMILL = "treadmill"
    private const val ICON_BICYCLE = "bicycle"
    private const val ICON_MUSCLES = "muscles"
    private const val ICON_FIRE = "fire"
    private const val ICON_CHRONOMETER = "chronometr"
    private const val ICON_WEIGHT_LIFTING = "weight_lifting"
    private const val ICON_CHEST = "muscle_icon_chest"
    private const val ICON_BACK_MUSCLE = "muscle_icon_back"
    private const val ICON_ARMS = "muscle_icon_arms"
    private const val ICON_LEGS_MUSCLE = "muscle_icon_legs"
    private const val ICON_ABS_MUSCLE = "muscle_icon_abs"
    private const val ICON_DEFAULT = "exercise_default_icon"
    private const val ICON_TEMPLATE_DEFAULT = "template_icon"

    /**
     * Maps exercise ID to drawable resource name
     * Returns the drawable name that should be used for the exercise
     */
    fun getIconForExercise(exerciseId: String): String {
        return when (exerciseId) {
            // CHEST
            "chest_bench_press" -> ICON_BENCH
            "chest_incline_bench_press" -> ICON_BENCH
            "chest_dumbbell_bench_press" -> ICON_DUMBBELL
            "chest_incline_dumbbell_press" -> ICON_DUMBBELL
            "chest_cable_flyes" -> ICON_BACK
            "chest_dumbbell_flyes" -> ICON_DUMBBELL
            "chest_pec_deck" -> ICON_MUSCLES
            "chest_push_ups" -> ICON_ARMS
            "chest_dips" -> ICON_FITNESS
            "chest_chest_press_machine" -> ICON_FITNESS

            // BACK
            "back_deadlift" -> ICON_BARBELL
            "back_sumo_deadlift" -> ICON_BARBELL
            "back_pull_ups" -> ICON_FITNESS
            "back_chin_ups" -> ICON_FITNESS
            "back_lat_pulldown" -> ICON_BACK
            "back_cable_row" -> ICON_ROWING
            "back_barbell_row" -> ICON_BARBELL
            "back_dumbbell_row" -> ICON_DUMBBELL_HAND
            "back_t_bar_row" -> ICON_BARBELL
            "back_seated_cable_row" -> ICON_ROWING
            "back_face_pulls" -> ICON_BACK
            "back_barbell_shrugs" -> ICON_WEIGHTS
            "back_dumbbell_shrugs" -> ICON_WEIGHTS
            "back_assisted_pull_up" -> ICON_FITNESS
            "back_landmine_row" -> ICON_DUMBBELL_HAND

            // LEGS
            "legs_squat" -> ICON_LEG
            "legs_barbell_squat" -> ICON_BARBELL
            "legs_dumbbell_squat" -> ICON_DUMBBELL
            "legs_leg_press" -> ICON_LEG
            "legs_hack_squat" -> ICON_LEG
            "legs_lunges" -> ICON_FITNESS
            "legs_walking_lunges" -> ICON_FITNESS
            "legs_reverse_lunges" -> ICON_FITNESS
            "legs_bulgarian_split_squat" -> ICON_FITNESS
            "legs_leg_extension" -> ICON_LEG
            "legs_seated_leg_curl" -> ICON_LEG
            "legs_lying_leg_curl" -> ICON_LEG
            "legs_calf_raises" -> ICON_LEG
            "legs_seated_calf_raises" -> ICON_LEG
            "back_romanian_deadlift" -> ICON_BARBELL
            "legs_standing_calf_raises" -> ICON_LEG
            "legs_hip_thrust" -> ICON_LEG
            "legs_glute_bridge" -> ICON_LEG
            "legs_step_ups" -> ICON_FITNESS
            "legs_leg_adduction" -> ICON_LEG
            "legs_leg_abduction" -> ICON_LEG
            "legs_sumo_squat" -> ICON_BARBELL
            "legs_front_squat" -> ICON_BARBELL
            "legs_box_squat" -> ICON_BARBELL
            "legs_good_mornings" -> ICON_BARBELL

            // ARMS - BICEPS
            "arms_barbell_curl" -> ICON_BARBELL
            "arms_dumbbell_curl" -> ICON_DUMBBELL
            "arms_hammer_curl" -> ICON_DUMBBELL
            "arms_preacher_curl" -> ICON_DUMBBELL
            "arms_cable_curl" -> ICON_BACK
            "arms_incline_dumbbell_curl" -> ICON_BICEPS
            "arms_ez_bar_curl" -> ICON_BARBELL
            "arms_drag_curl" -> ICON_DUMBBELL
            "arms_zottman_curl" -> ICON_BICEPS

            // ARMS - TRICEPS
            "arms_tricep_pushdown" -> ICON_BACK
            "arms_skull_crushers" -> ICON_BARBELL
            "arms_close_grip_bench_press" -> ICON_BENCH
            "arms_tricep_dips" -> ICON_FITNESS
            "arms_kickbacks" -> ICON_BICEPS
            "arms_overhead_dumbbell_extension" -> ICON_DUMBBELL_HAND
            "arms_cable_overhead_extension" -> ICON_BACK
            "arms_lying_tricep_extension" -> ICON_BARBELL

            // ARMS - FOREARMS
            "arms_wrist_curl" -> ICON_BICEPS
            "arms_reverse_wrist_curl" -> ICON_BICEPS
            "arms_farmer_walk" -> ICON_WEIGHTS
            "arms_grip_squeeze" -> ICON_WEIGHTS

            // SHOULDERS
            "shoulders_overhead_press" -> ICON_BARBELL
            "shoulders_barbell_overhead_press" -> ICON_BARBELL
            "shoulders_seated_overhead_press" -> ICON_DUMBBELL
            "shoulders_dumbbell_lateral_raise" -> ICON_DUMBBELL_HAND
            "shoulders_dumbbell_front_raise" -> ICON_DUMBBELL_HAND
            "shoulders_cable_front_raise" -> ICON_BACK
            "shoulders_rear_delt_fly" -> ICON_BACK
            "shoulders_shoulder_press_machine" -> ICON_FITNESS
            "shoulders_arnold_press" -> ICON_DUMBBELL
            "shoulders_upright_row" -> ICON_BARBELL
            "shoulders_face_pulls" -> ICON_BACK

            // ABS
            "abs_crunches" -> ICON_ABS
            "abs_sit_ups" -> ICON_ABS
            "abs_plank" -> ICON_ABS
            "abs_side_plank" -> ICON_ABS
            "abs_leg_raises" -> ICON_ABS
            "abs_hanging_leg_raises" -> ICON_FITNESS
            "abs_russian_twist" -> ICON_ABS
            "abs_mountain_climbers" -> ICON_FITNESS
            "abs_cable_crunch" -> ICON_ABS
            "abs_ab_wheel" -> ICON_DUMBBELL
            "abs_dead_bug" -> ICON_ABS
            "abs_oblique_crunches" -> ICON_ABS
            "abs_toe_touches" -> ICON_ABS
            "abs_flutter_kicks" -> ICON_FITNESS

            // CARDIO
            "cardio_running" -> ICON_TREADMILL
            "cardio_walking" -> ICON_TREADMILL
            "cardio_cycling" -> ICON_BICYCLE
            "cardio_elliptical" -> ICON_FITNESS
            "cardio_rowing_machine" -> ICON_ROWING
            "cardio_stair_climber" -> ICON_FITNESS
            "cardio_jump_rope" -> ICON_FITNESS
            "cardio_burpees" -> ICON_FITNESS
            "cardio_box_jumps" -> ICON_FITNESS
            "cardio_battle_ropes" -> ICON_WEIGHTS
            "cardio_sprinting" -> ICON_TREADMILL

            else -> ICON_DEFAULT
        }
    }

    /**
     * Maps template ID to drawable resource name
     */
    fun getIconForTemplate(templateId: String): String {
        return when (templateId) {
            "push_day" -> ICON_BENCH
            "pull_day" -> ICON_BACK
            "legs_day" -> ICON_LEG
            "full_body_beginner", "full_body_3x", "full_body" -> ICON_FITNESS
            "upper_body" -> ICON_MUSCLES
            "lower_body" -> ICON_LEG
            "chest_triceps", "chest_focus" -> ICON_CHEST
            "back_biceps", "back_focus" -> ICON_BACK_MUSCLE
            "shoulders_arms", "shoulders_focus" -> ICON_ARMS
            "legs_glutes", "leg_focus" -> ICON_LEGS_MUSCLE
            "core_strength", "abs_focus" -> ICON_ABS_MUSCLE
            "cardio_conditioning", "cardio_mix" -> ICON_TREADMILL
            "hiit_workout" -> ICON_FIRE
            "home_bodyweight", "bodyweight" -> ICON_FITNESS
            else -> ICON_TEMPLATE_DEFAULT
        }
    }

    /**
     * Get drawable resource ID from resource name
     */
    fun getDrawableResourceId(context: Context, resourceName: String): Int {
        return context.resources.getIdentifier(
            resourceName,
            "drawable",
            context.packageName
        )
    }
}