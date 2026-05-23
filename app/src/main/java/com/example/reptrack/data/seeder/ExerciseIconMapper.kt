package com.example.reptrack.data.seeder

import android.content.Context

/**
 * Maps exercise IDs to drawable resource names
 * Uses existing drawable resources from the project
 */
object ExerciseIconMapper {

    /**
     * Maps exercise ID to drawable resource name
     * Returns the drawable name that should be used for the exercise
     */
    fun getIconForExercise(exerciseId: String): String {
        return when (exerciseId) {
            // CHEST
            "chest_bench_press" -> "bench_press"
            "chest_incline_bench_press" -> "bench_press"
            "chest_decline_bench_press" -> "bench_press"
            "chest_dumbbell_bench_press" -> "dumbell"
            "chest_incline_dumbbell_press" -> "dumbell"
            "chest_decline_dumbbell_press" -> "dumbell"
            "chest_cable_flyes" -> "back_muscles"
            "chest_dumbbell_flyes" -> "dumbell"
            "chest_pec_deck" -> "muscles"
            "chest_push_ups" -> "fitness"
            "chest_diamond_push_ups" -> "fitness"
            "chest_dips" -> "fitness"
            "chest_landmine_press" -> "barbell_energy"
            "chest_cable_crossover" -> "back_muscles"
            "chest_chest_press_machine" -> "fitness"
            "chest_floor_press" -> "bench_press"

            // BACK
            "back_deadlift" -> "barbell_energy"
            "back_conventional_deadlift" -> "barbell_energy"
            "back_sumo_deadlift" -> "barbell_energy"
            "back_romanian_deadlift" -> "barbell_energy"
            "back_pull_ups" -> "fitness"
            "back_chin_ups" -> "fitness"
            "back_lat_pulldown" -> "back_muscles"
            "back_cable_row" -> "rowing"
            "back_barbell_row" -> "barbell_energy"
            "back_dumbbell_row" -> "dumbel_with_a_hand"
            "back_t_bar_row" -> "barbell_energy"
            "back_seated_cable_row" -> "rowing"
            "back_face_pulls" -> "back_muscles"
            "back_reverse_flyes" -> "back_muscles"
            "back_shrugs" -> "weights"
            "back_barbell_shrugs" -> "weights"
            "back_dumbbell_shrugs" -> "weights"
            "back_assisted_pull_up" -> "fitness"
            "back_landmine_row" -> "dumbel_with_a_hand"

            // LEGS
            "legs_squat" -> "leg_push"
            "legs_barbell_squat" -> "barbell_energy"
            "legs_dumbbell_squat" -> "dumbell"
            "legs_goblet_squat" -> "dumbell"
            "legs_leg_press" -> "leg_push"
            "legs_hack_squat" -> "leg_push"
            "legs_lunges" -> "fitness"
            "legs_walking_lunges" -> "fitness"
            "legs_reverse_lunges" -> "fitness"
            "legs_bulgarian_split_squat" -> "fitness"
            "legs_leg_curl" -> "leg_push"
            "legs_leg_extension" -> "leg_push"
            "legs_seated_leg_curl" -> "leg_push"
            "legs_lying_leg_curl" -> "leg_push"
            "legs_calf_raises" -> "leg_push"
            "legs_seated_calf_raises" -> "leg_push"
            "legs_standing_calf_raises" -> "leg_push"
            "legs_hip_thrust" -> "leg_push"
            "legs_glute_bridge" -> "leg_push"
            "legs_step_ups" -> "fitness"
            "legs_leg_adduction" -> "leg_push"
            "legs_leg_abduction" -> "leg_push"
            "legs_sumo_squat" -> "barbell_energy"
            "legs_front_squat" -> "barbell_energy"
            "legs_box_squat" -> "barbell_energy"
            "legs_good_mornings" -> "barbell_energy"
            "legs_sissy_squat" -> "dumbell"

            // ARMS - BICEPS
            "arms_barbell_curl" -> "barbell_energy"
            "arms_dumbbell_curl" -> "dumbell"
            "arms_hammer_curl" -> "dumbell"
            "arms_preacher_curl" -> "dumbell"
            "arms_concentration_curl" -> "bic_dumbell"
            "arms_cable_curl" -> "back_muscles"
            "arms_incline_dumbbell_curl" -> "bic_dumbell"
            "arms_spider_curl" -> "bic_dumbell"
            "arms_ez_bar_curl" -> "barbell_energy"
            "arms_drag_curl" -> "dumbell"
            "arms_zottman_curl" -> "bic_dumbell"

            // ARMS - TRICEPS
            "arms_tricep_pushdown" -> "back_muscles"
            "arms_skull_crushers" -> "barbell_energy"
            "arms_overhead_extension" -> "dumbel_with_a_hand"
            "arms_close_grip_bench_press" -> "bench_press"
            "arms_tricep_dips" -> "fitness"
            "arms_kickbacks" -> "bic_dumbell"
            "arms_overhead_dumbbell_extension" -> "dumbel_with_a_hand"
            "arms_cable_overhead_extension" -> "back_muscles"
            "arms_rope_pushdown" -> "back_muscles"
            "arms_lying_tricep_extension" -> "barbell_energy"
            "arms_tate_press" -> "dumbell"

            // ARMS - FOREARMS
            "arms_wrist_curl" -> "bic_dumbell"
            "arms_reverse_wrist_curl" -> "bic_dumbell"
            "arms_farmer_walk" -> "weights"
            "arms_plate_curl" -> "weights"
            "arms_grip_squeeze" -> "weights"

            // SHOULDERS
            "shoulders_overhead_press" -> "barbell_energy"
            "shoulders_barbell_overhead_press" -> "barbell_energy"
            "shoulders_dumbbell_overhead_press" -> "dumbell"
            "shoulders_seated_overhead_press" -> "dumbell"
            "shoulders_lateral_raise" -> "dumbel_with_a_hand"
            "shoulders_dumbbell_lateral_raise" -> "dumbel_with_a_hand"
            "shoulders_cable_lateral_raise" -> "back_muscles"
            "shoulders_front_raise" -> "dumbel_with_a_hand"
            "shoulders_dumbbell_front_raise" -> "dumbel_with_a_hand"
            "shoulders_cable_front_raise" -> "back_muscles"
            "shoulders_reverse_flyes" -> "back_muscles"
            "shoulders_rear_delt_fly" -> "back_muscles"
            "shoulders_shoulder_press_machine" -> "fitness"
            "shoulders_arnold_press" -> "dumbell"
            "shoulders_upright_row" -> "barbell_energy"
            "shoulders_face_pulls" -> "back_muscles"
            "shoulders_landmine_press" -> "dumbel_with_a_hand"
            "shoulders_halo" -> "dumbell"
            "shoulders_band_pull_apart" -> "back_muscles"

            // ABS
            "abs_crunches" -> "abs"
            "abs_sit_ups" -> "abs"
            "abs_plank" -> "abs"
            "abs_side_plank" -> "abs"
            "abs_leg_raises" -> "abs"
            "abs_hanging_leg_raises" -> "fitness"
            "abs_russian_twist" -> "abs"
            "abs_bicycle_crunches" -> "abs"
            "abs_mountain_climbers" -> "fitness"
            "abs_cable_crunch" -> "abs"
            "abs_ab_wheel" -> "dumbell"
            "abs_dead_bug" -> "abs"
            "abs_bird_dog" -> "fitness"
            "abs_hollow_body_hold" -> "abs"
            "abs_v_sit" -> "abs"
            "abs_oblique_crunches" -> "abs"
            "abs_reverse_crunches" -> "abs"
            "abs_plank_jacks" -> "fitness"
            "abs_toe_touches" -> "abs"
            "abs_flutter_kicks" -> "fitness"

            // CARDIO
            "cardio_treadmill" -> "treadmill"
            "cardio_running" -> "treadmill"
            "cardio_walking" -> "treadmill"
            "cardio_cycling" -> "bicycle"
            "cardio_stationary_bike" -> "stationary_bike"
            "cardio_elliptical" -> "fitness"
            "cardio_rowing_machine" -> "rowing"
            "cardio_stair_climber" -> "fitness"
            "cardio_jump_rope" -> "fitness"
            "cardio_burpees" -> "fitness"
            "cardio_box_jumps" -> "fitness"
            "cardio_kettlebell_swings" -> "dumbell"
            "cardio_battle_ropes" -> "weights"
            "cardio_sprinting" -> "treadmill"
            "cardio_interval_running" -> "treadmill"
            "cardio_hiit" -> "fitness"
            "cardio_skipping" -> "fitness"

            else -> "exercise_default_icon"
        }
    }

    /**
     * Maps template ID to drawable resource name
     */
    fun getIconForTemplate(templateId: String): String {
        return when (templateId) {
            "push_day" -> "bench_press"
            "pull_day" -> "back_muscles"
            "legs_day" -> "leg_push"
            "full_body_3x", "full_body" -> "fitness"
            "upper_body" -> "muscles"
            "lower_body" -> "leg_push"
            "chest_focus" -> "muscle_icon_chest"
            "back_focus" -> "muscle_icon_back"
            "arm_focus" -> "muscle_icon_arms"
            "leg_focus" -> "muscle_icon_legs"
            "shoulders_focus" -> "muscle_icon_arms"
            "abs_focus" -> "muscle_icon_abs"
            "strength" -> "weights"
            "hypertrophy" -> "weight_lifting"
            "endurance" -> "treadmill"
            "bodyweight" -> "fitness"
            "dumbbell_only" -> "dumbell"
            "cardio_mix" -> "treadmill"
            "hiit_workout" -> "fire"
            "warm_up" -> "chronometr"
            "cool_down" -> "chronometr"
            else -> "template_icon"
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