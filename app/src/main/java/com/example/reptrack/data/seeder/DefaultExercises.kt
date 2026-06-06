package com.example.reptrack.data.seeder

import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.ExerciseType
import com.example.reptrack.domain.workout.entities.MuscleGroup

object DefaultExercises {

    private const val COLOR_CHEST = "#FF6B6B"      // ярко-коралловый (грудь)
    private const val COLOR_BACK = "#4ECDC4"       // яркий бирюзовый (спина)
    private const val COLOR_LEGS = "#45B7D1"       // ярко-голубой (ноги)
    private const val COLOR_ARMS = "#9B59B6"       // яркий фиолетовый (руки)
    private const val COLOR_ABS = "#F39C12"        // яркий оранжевый (пресс)
    private const val COLOR_CARDIO = "#2ECC71"     // ярко-зеленый (кардио)

    private val exercises = listOf(
        // ==================== CHEST ====================
        Exercise("chest_bench_press", "Жим лежа", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.bench_press, COLOR_CHEST, null, null, false),
        Exercise("chest_incline_bench_press", "Жим лежа на наклонной скамье", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.weight_lifting, COLOR_CHEST, null, null, false),
        Exercise("chest_dumbbell_bench_press", "Жим гантелей лежа", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.dumbell, COLOR_CHEST, null, null, false),
        Exercise("chest_incline_dumbbell_press", "Жим гантелей на наклонной скамье", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.weights, COLOR_CHEST, null, null, false),
        Exercise("chest_cable_flyes", "Сведения рук в кроссовере", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.exercise_icon_4, COLOR_CHEST, null, null, false),
        Exercise("chest_dumbbell_flyes", "Разводка гантелей лежа", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.exercis_icon_2, COLOR_CHEST, null, null, false),
        Exercise("chest_pec_deck", "Сведения рук в тренажере (бабочка)", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.heart_dumbell, COLOR_CHEST, null, null, false),
        Exercise("chest_push_ups", "Отжимания от пола", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.exercise_icon_3, COLOR_CHEST, null, null, false),
        Exercise("chest_dips", "Отжимания на брусьях", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.bear, COLOR_CHEST, null, null, false),
        Exercise("chest_chest_press_machine", "Жим в тренажере для груди", MuscleGroup.CHEST, ExerciseType.WEIGHT_REPS, R.drawable.muscle_icon_chest, COLOR_CHEST, null, null, false),

        // ==================== BACK ====================
        Exercise("back_deadlift", "Становая тяга", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.barbell_energy, COLOR_BACK, null, null, false),
        Exercise("back_sumo_deadlift", "Становая тяга сумо", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.back_muscles, COLOR_BACK, null, null, false),
        Exercise("back_pull_ups", "Подтягивания", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.muscles, COLOR_BACK, null, null, false),
        Exercise("back_chin_ups", "Подтягивания обратным хватом", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.muscle_icon_back, COLOR_BACK, null, null, false),
        Exercise("back_lat_pulldown", "Тяга верхнего блока к груди", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.muscle_icon_back, COLOR_BACK, null, null, false),
        Exercise("back_cable_row", "Тяга блока к поясу", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.puzzle, COLOR_BACK, null, null, false),
        Exercise("back_barbell_row", "Тяга штанги в наклоне", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.rowing, COLOR_BACK, null, null, false),
        Exercise("back_dumbbell_row", "Тяга гантели в наклоне", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.dumbel_with_a_hand, COLOR_BACK, null, null, false),
        Exercise("back_t_bar_row", "Тяга T-грифа", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.tank, COLOR_BACK, null, null, false),
        Exercise("back_seated_cable_row", "Тяга нижнего блока сидя", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.castle, COLOR_BACK, null, null, false),
        Exercise("back_face_pulls", "Тяга к лицу", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.skull, COLOR_BACK, null, null, false),
        Exercise("back_barbell_shrugs", "Шраги со штангой", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.goal, COLOR_BACK, null, null, false),
        Exercise("back_dumbbell_shrugs", "Шраги с гантелями", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.limited, COLOR_BACK, null, null, false),
        Exercise("back_assisted_pull_up", "Подтягивания с противовесом", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.robot, COLOR_BACK, null, null, false),
        Exercise("back_landmine_row", "Тяга Landmine", MuscleGroup.BACK, ExerciseType.WEIGHT_REPS, R.drawable.rocket, COLOR_BACK, null, null, false),

        // ==================== LEGS ====================
        Exercise("legs_squat", "Приседания", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.leg, COLOR_LEGS, null, null, false),
        Exercise("legs_barbell_squat", "Приседания со штангой", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.best_choice, COLOR_LEGS, null, null, false),
        Exercise("legs_dumbbell_squat", "Приседания с гантелями", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.dumbell, COLOR_LEGS, null, null, false),
        Exercise("legs_leg_press", "Жим ногами", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.leg_push, COLOR_LEGS, null, null, false),
        Exercise("legs_hack_squat", "Приседания в тренажере Хаккеншмидта", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.muscle_icon_legs, COLOR_LEGS, null, null, false),
        Exercise("legs_lunges", "Выпады", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.foot, COLOR_LEGS, null, null, false),
        Exercise("legs_walking_lunges", "Выпады в ходьбе", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.deer, COLOR_LEGS, null, null, false),
        Exercise("legs_reverse_lunges", "Обратные выпады", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.wolf, COLOR_LEGS, null, null, false),
        Exercise("legs_bulgarian_split_squat", "Болгарские сплит-приседания", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.bear_big, COLOR_LEGS, null, null, false),
        Exercise("legs_leg_extension", "Разгибание ног", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.speedometr, COLOR_LEGS, null, null, false),
        Exercise("legs_seated_leg_curl", "Сгибание ног сидя", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.sand_clock, COLOR_LEGS, null, null, false),
        Exercise("legs_lying_leg_curl", "Сгибание ног лежа", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.teddy_bear, COLOR_LEGS, null, null, false),
        Exercise("legs_calf_raises", "Подъемы на носки", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.elephant, COLOR_LEGS, null, null, false),
        Exercise("legs_seated_calf_raises", "Подъемы на носки сидя", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.duck, COLOR_LEGS, null, null, false),
        Exercise("legs_romanian_deadlift", "Румынская тяга", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.barbell_energy, COLOR_LEGS, null, null, false),
        Exercise("legs_standing_calf_raises", "Подъемы на носки стоя", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.walrus, COLOR_LEGS, null, null, false),
        Exercise("legs_hip_thrust", "Ягодичный мостик со штангой", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.moose, COLOR_LEGS, null, null, false),
        Exercise("legs_glute_bridge", "Ягодичный мостик", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.chess_sword, COLOR_LEGS, null, null, false),
        Exercise("legs_step_ups", "Зашагивания на платформу", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.hedgehog, COLOR_LEGS, null, null, false),
        Exercise("legs_leg_adduction", "Приведение бедра", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.compass, COLOR_LEGS, null, null, false),
        Exercise("legs_leg_abduction", "Отведение бедра", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.fins, COLOR_LEGS, null, null, false),
        Exercise("legs_sumo_squat", "Приседания сумо", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.piggy, COLOR_LEGS, null, null, false),
        Exercise("legs_front_squat", "Фронтальные приседания", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.focus, COLOR_LEGS, null, null, false),
        Exercise("legs_good_mornings", "Наклоны вперед со штангой", MuscleGroup.LEGS, ExerciseType.WEIGHT_REPS, R.drawable.idea, COLOR_LEGS, null, null, false),

        // ==================== ARMS (BICEPS + TRICEPS + FOREARMS) ====================
        Exercise("arms_barbell_curl", "Сгибания рук со штангой", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.bic_dumbell, COLOR_ARMS, null, null, false),
        Exercise("arms_dumbbell_curl", "Сгибания рук с гантелями", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.dumbell, COLOR_ARMS, null, null, false),
        Exercise("arms_hammer_curl", "Молотковые сгибания", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.dumbell, COLOR_ARMS, null, null, false),
        Exercise("arms_preacher_curl", "Сгибания на скамье Скотта", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.bench_press, COLOR_ARMS, null, null, false),
        Exercise("arms_cable_curl", "Сгибания в кроссовере", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.exercise_icon_4, COLOR_ARMS, null, null, false),
        Exercise("arms_incline_curl", "Сгибания на наклонной скамье", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.weight_lifting, COLOR_ARMS, null, null, false),
        Exercise("arms_ez_bar_curl", "Сгибания с EZ-штангой", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.barbell_energy, COLOR_ARMS, null, null, false),
        Exercise("arms_concentration_curl", "Концентрированные сгибания", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.muscle_icon_arms, COLOR_ARMS, null, null, false),
        Exercise("arms_tricep_pushdown", "Разгибания на блоке", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.exercis_icon_2, COLOR_ARMS, null, null, false),
        Exercise("arms_skull_crushers", "Французский жим лежа", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.skull, COLOR_ARMS, null, null, false),
        Exercise("arms_close_grip_press", "Жим узким хватом", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.bench_press, COLOR_ARMS, null, null, false),
        Exercise("arms_tricep_dips", "Отжимания на брусьях", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.bear, COLOR_ARMS, null, null, false),
        Exercise("arms_overhead_extension", "Разгибания из-за головы", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.dumbell, COLOR_ARMS, null, null, false),
        Exercise("arms_kickbacks", "Разгибания в наклоне", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.dumbel_with_a_hand, COLOR_ARMS, null, null, false),
        Exercise("arms_wrist_curl", "Сгибания кистей", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.weights, COLOR_ARMS, null, null, false),
        Exercise("arms_reverse_wrist_curl", "Разгибания кистей", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.weights, COLOR_ARMS, null, null, false),
        Exercise("arms_farmer_walk", "Прогулка фермера", MuscleGroup.ARMS, ExerciseType.WEIGHT_REPS, R.drawable.leg, COLOR_ARMS, null, null, false),

        // ==================== ABS ====================
        Exercise("abs_crunches", "Скручивания", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.abs, COLOR_ABS, null, null, false),
        Exercise("abs_sit_ups", "Подъемы туловища", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.muscle_icon_abs, COLOR_ABS, null, null, false),
        Exercise("abs_plank", "Планка", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.fitness_women, COLOR_ABS, null, null, false),
        Exercise("abs_side_plank", "Боковая планка", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.chess_sword, COLOR_ABS, null, null, false),
        Exercise("abs_leg_raises", "Подъемы ног", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.leg, COLOR_ABS, null, null, false),
        Exercise("abs_hanging_leg_raises", "Подъемы ног в висе", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.leg_push, COLOR_ABS, null, null, false),
        Exercise("abs_russian_twist", "Русский твист", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.walrus, COLOR_ABS, null, null, false),
        Exercise("abs_mountain_climbers", "Скалолаз", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.goal, COLOR_ABS, null, null, false),
        Exercise("abs_cable_crunch", "Молитва", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.reloadsvg, COLOR_ABS, null, null, false),
        Exercise("abs_ab_wheel", "Роллер", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.duck, COLOR_ABS, null, null, false),
        Exercise("abs_oblique_crunches", "Косые скручивания", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.skull, COLOR_ABS, null, null, false),
        Exercise("abs_toe_touches", "Касания носков", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.thunder, COLOR_ABS, null, null, false),
        Exercise("abs_flutter_kicks", "Махи ногами", MuscleGroup.ABS, ExerciseType.WEIGHT_REPS, R.drawable.compass, COLOR_ABS, null, null, false),

        // ==================== CARDIO ====================
        Exercise("cardio_running", "Бег", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.chronometr, COLOR_CARDIO, null, null, false),
        Exercise("cardio_walking", "Ходьба", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.fitness, COLOR_CARDIO, null, null, false),
        Exercise("cardio_cycling", "Велосипед", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.bicycle, COLOR_CARDIO, null, null, false),
        Exercise("cardio_elliptical", "Эллиптический тренажер", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.stationary_bike, COLOR_CARDIO, null, null, false),
        Exercise("cardio_rowing", "Гребной тренажер", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.rowing, COLOR_CARDIO, null, null, false),
        Exercise("cardio_stair_climber", "Степпер", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.treadmill, COLOR_CARDIO, null, null, false),
        Exercise("cardio_jump_rope", "Скакалка", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.energy_arrow, COLOR_CARDIO, null, null, false),
        Exercise("cardio_burpees", "Бёрпи", MuscleGroup.CARDIO, ExerciseType.WEIGHT_REPS, R.drawable.fitness_women, COLOR_CARDIO, null, null, false),
        Exercise("cardio_box_jumps", "Прыжки на коробку", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.thunder, COLOR_CARDIO, null, null, false),
        Exercise("cardio_battle_ropes", "Волновые канаты", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.drama, COLOR_CARDIO, null, null, false),
        Exercise("cardio_sprinting", "Спринт", MuscleGroup.CARDIO, ExerciseType.TIME_DISTANCE, R.drawable.rocket, COLOR_CARDIO, null, null, false)
    )

    fun getAllExercises(): List<Exercise> = exercises.toList()
    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<Exercise> = exercises.filter { it.muscleGroup == muscleGroup }
    fun getExerciseById(id: String): Exercise? = exercises.find { it.id == id }
}