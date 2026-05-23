package com.example.reptrack.data.seeder

import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup

/**
 * Comprehensive exercise database with pre-mapped icons
 * All exercises use existing drawable resources from the project
 */
object DefaultExercises {

    private val exercises = listOf(
        // ==================== CHEST EXERCISES ====================
        Exercise(
            id = "chest_bench_press",
            name = "Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null, // Will be mapped from resource name
            iconColor = "#FF6B6B",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_incline_bench_press",
            name = "Incline Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF8585",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_decline_bench_press",
            name = "Decline Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9F9F",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_dumbbell_bench_press",
            name = "Dumbbell Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF6B6B",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_incline_dumbbell_press",
            name = "Incline Dumbbell Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF8585",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_decline_dumbbell_press",
            name = "Decline Dumbbell Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9F9F",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_cable_flyes",
            name = "Cable Flyes",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFB4B4",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_dumbbell_flyes",
            name = "Dumbbell Flyes",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFC9C9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_pec_deck",
            name = "Pec Deck",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFDEDE",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_push_ups",
            name = "Push Ups",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF6B6B",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_diamond_push_ups",
            name = "Diamond Push Ups",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF8585",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_dips",
            name = "Chest Dips",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9F9F",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_landmine_press",
            name = "Landmine Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFB4B4",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_cable_crossover",
            name = "Cable Crossover",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFC9C9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_chest_press_machine",
            name = "Chest Press Machine",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFDEDE",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "chest_floor_press",
            name = "Floor Press",
            muscleGroup = MuscleGroup.CHEST,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF6B6B",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),

        // ==================== BACK EXERCISES ====================
        Exercise(
            id = "back_deadlift",
            name = "Deadlift",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#4ECDC4",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_conventional_deadlift",
            name = "Conventional Deadlift",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#45B7AA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_sumo_deadlift",
            name = "Sumo Deadlift",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#3DA290",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_romanian_deadlift",
            name = "Romanian Deadlift",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#358D76",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_pull_ups",
            name = "Pull Ups",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#4ECDC4",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_chin_ups",
            name = "Chin Ups",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#45B7AA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_lat_pulldown",
            name = "Lat Pulldown",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#3DA290",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_cable_row",
            name = "Cable Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#358D76",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_barbell_row",
            name = "Barbell Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#2E7C5C",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_dumbbell_row",
            name = "Dumbbell Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#4ECDC4",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_t_bar_row",
            name = "T-Bar Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#45B7AA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_seated_cable_row",
            name = "Seated Cable Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#3DA290",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_face_pulls",
            name = "Face Pulls",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#358D76",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_reverse_flyes",
            name = "Reverse Flyes",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#2E7C5C",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_shrugs",
            name = "Shrugs",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#4ECDC4",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_barbell_shrugs",
            name = "Barbell Shrugs",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#45B7AA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_dumbbell_shrugs",
            name = "Dumbbell Shrugs",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#3DA290",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_assisted_pull_up",
            name = "Assisted Pull Up",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#358D76",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "back_landmine_row",
            name = "Landmine Row",
            muscleGroup = MuscleGroup.BACK,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#2E7C5C",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),

        // ==================== LEGS EXERCISES ====================
        Exercise(
            id = "legs_squat",
            name = "Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_barbell_squat",
            name = "Barbell Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_dumbbell_squat",
            name = "Dumbbell Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_goblet_squat",
            name = "Goblet Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_leg_press",
            name = "Leg Press",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_hack_squat",
            name = "Hack Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_lunges",
            name = "Lunges",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_walking_lunges",
            name = "Walking Lunges",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_reverse_lunges",
            name = "Reverse Lunges",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_bulgarian_split_squat",
            name = "Bulgarian Split Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_leg_curl",
            name = "Leg Curl",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_leg_extension",
            name = "Leg Extension",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_seated_leg_curl",
            name = "Seated Leg Curl",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_lying_leg_curl",
            name = "Lying Leg Curl",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_calf_raises",
            name = "Calf Raises",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_seated_calf_raises",
            name = "Seated Calf Raises",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_standing_calf_raises",
            name = "Standing Calf Raises",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_hip_thrust",
            name = "Hip Thrust",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_glute_bridge",
            name = "Glute Bridge",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_step_ups",
            name = "Step Ups",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_leg_adduction",
            name = "Leg Adduction",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_leg_abduction",
            name = "Leg Abduction",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_sumo_squat",
            name = "Sumo Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_front_squat",
            name = "Front Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_box_squat",
            name = "Box Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#95E1D3",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_good_mornings",
            name = "Good Mornings",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#7DC9BA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "legs_sissy_squat",
            name = "Sissy Squat",
            muscleGroup = MuscleGroup.LEGS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#65C1A1",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),

        // ==================== ARMS EXERCISES - BICEPS ====================
        Exercise(
            id = "arms_barbell_curl",
            name = "Barbell Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9F43",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_dumbbell_curl",
            name = "Dumbbell Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFB065",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_hammer_curl",
            name = "Hammer Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFC287",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_preacher_curl",
            name = "Preacher Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFD3A9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_concentration_curl",
            name = "Concentration Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9F43",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_cable_curl",
            name = "Cable Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFB065",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_incline_dumbbell_curl",
            name = "Incline Dumbbell Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFC287",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_spider_curl",
            name = "Spider Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFD3A9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_ez_bar_curl",
            name = "EZ Bar Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9F43",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_drag_curl",
            name = "Drag Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFB065",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_zottman_curl",
            name = "Zottman Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFC287",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),

        // ==================== ARMS EXERCISES - TRICEPS ====================
        Exercise(
            id = "arms_tricep_pushdown",
            name = "Tricep Pushdown",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#A29BFE",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_skull_crushers",
            name = "Skull Crushers",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#B5ADF8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_overhead_extension",
            name = "Overhead Extension",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#C7C0FC",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_close_grip_bench_press",
            name = "Close Grip Bench Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#D9D4FF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_tricep_dips",
            name = "Tricep Dips",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#A29BFE",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_kickbacks",
            name = "Kickbacks",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#B5ADF8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_overhead_dumbbell_extension",
            name = "Overhead Dumbbell Extension",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#C7C0FC",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_cable_overhead_extension",
            name = "Cable Overhead Extension",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#D9D4FF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_rope_pushdown",
            name = "Rope Pushdown",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#A29BFE",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_lying_tricep_extension",
            name = "Lying Tricep Extension",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#B5ADF8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_tate_press",
            name = "Tate Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#C7C0FC",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),

        // ==================== ARMS EXERCISES - FOREARMS ====================
        Exercise(
            id = "arms_wrist_curl",
            name = "Wrist Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9F43",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_reverse_wrist_curl",
            name = "Reverse Wrist Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFB065",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_farmer_walk",
            name = "Farmer Walk",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFC287",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_plate_curl",
            name = "Plate Curl",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFD3A9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "arms_grip_squeeze",
            name = "Grip Squeeze",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9F43",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),

        // ==================== SHOULDERS EXERCISES ====================
        Exercise(
            id = "shoulders_overhead_press",
            name = "Overhead Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FD79A8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_barbell_overhead_press",
            name = "Barbell Overhead Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FE8BA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_dumbbell_overhead_press",
            name = "Dumbbell Overhead Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9DA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_seated_overhead_press",
            name = "Seated Overhead Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FD79A8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_lateral_raise",
            name = "Lateral Raise",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FE8BA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_dumbbell_lateral_raise",
            name = "Dumbbell Lateral Raise",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9DA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_cable_lateral_raise",
            name = "Cable Lateral Raise",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FD79A8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_front_raise",
            name = "Front Raise",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FE8BA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_dumbbell_front_raise",
            name = "Dumbbell Front Raise",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9DA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_cable_front_raise",
            name = "Cable Front Raise",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FD79A8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_reverse_flyes",
            name = "Reverse Flyes",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FE8BA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_rear_delt_fly",
            name = "Rear Delt Fly",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9DA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_shoulder_press_machine",
            name = "Shoulder Press Machine",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FD79A8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_arnold_press",
            name = "Arnold Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FE8BA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_upright_row",
            name = "Upright Row",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9DA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_face_pulls",
            name = "Face Pulls",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FD79A8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_landmine_press",
            name = "Landmine Press",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FE8BA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_halo",
            name = "Halo",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FF9DA9",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "shoulders_band_pull_apart",
            name = "Band Pull Apart",
            muscleGroup = MuscleGroup.ARMS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FD79A8",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),

        // ==================== ABS EXERCISES ====================
        Exercise(
            id = "abs_crunches",
            name = "Crunches",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_sit_ups",
            name = "Sit Ups",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_plank",
            name = "Plank",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_side_plank",
            name = "Side Plank",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_leg_raises",
            name = "Leg Raises",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_hanging_leg_raises",
            name = "Hanging Leg Raises",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_russian_twist",
            name = "Russian Twist",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_bicycle_crunches",
            name = "Bicycle Crunches",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_mountain_climbers",
            name = "Mountain Climbers",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_cable_crunch",
            name = "Cable Crunch",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_ab_wheel",
            name = "Ab Wheel Rollout",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_dead_bug",
            name = "Dead Bug",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_bird_dog",
            name = "Bird Dog",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_hollow_body_hold",
            name = "Hollow Body Hold",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_v_sit",
            name = "V Sit",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_oblique_crunches",
            name = "Oblique Crunches",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_reverse_crunches",
            name = "Reverse Crunches",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_plank_jacks",
            name = "Plank Jacks",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_toe_touches",
            name = "Toe Touches",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEAA7",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "abs_flutter_kicks",
            name = "Flutter Kicks",
            muscleGroup = MuscleGroup.ABS,
            type = ExerciseType.WEIGHT_REPS,
            iconRes = null,
            iconColor = "#FFEFBA",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),

        // ==================== CARDIO EXERCISES ====================
        Exercise(
            id = "cardio_treadmill",
            name = "Treadmill",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#74B9FF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_running",
            name = "Running",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#81CAFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_walking",
            name = "Walking",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#8EDBFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_cycling",
            name = "Cycling",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#74B9FF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_stationary_bike",
            name = "Stationary Bike",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#81CAFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_elliptical",
            name = "Elliptical",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#8EDBFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_rowing_machine",
            name = "Rowing Machine",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#74B9FF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_stair_climber",
            name = "Stair Climber",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#81CAFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_jump_rope",
            name = "Jump Rope",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#8EDBFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_burpees",
            name = "Burpees",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#74B9FF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_box_jumps",
            name = "Box Jumps",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#81CAFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_kettlebell_swings",
            name = "Kettlebell Swings",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#8EDBFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_battle_ropes",
            name = "Battle Ropes",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#74B9FF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_sprinting",
            name = "Sprinting",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#81CAFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_interval_running",
            name = "Interval Running",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#8EDBFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_hiit",
            name = "HIIT",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#74B9FF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        ),
        Exercise(
            id = "cardio_skipping",
            name = "Skipping",
            muscleGroup = MuscleGroup.CARDIO,
            type = ExerciseType.TIME_DISTANCE,
            iconRes = null,
            iconColor = "#81CAFF",
            backgroundRes = null,
            backgroundColor = null,
            isCustom = false
        )
    )

    fun getAllExercises(): List<Exercise> = exercises.toList()

    /**
     * Get exercises by muscle group
     */
    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<Exercise> {
        return exercises.filter { it.muscleGroup == muscleGroup }
    }

    /**
     * Get exercise by ID
     */
    fun getExerciseById(id: String): Exercise? {
        return exercises.find { it.id == id }
    }
}