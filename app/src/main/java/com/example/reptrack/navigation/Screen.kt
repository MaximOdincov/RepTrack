package com.example.reptrack.navigation

/**
 * Режим экрана списка упражнений
 */
enum class ExerciseListMode(val value: String) {
    VIEW_MODE("view"),
    WORKOUT_MODE("workout"),
    SELECT_MODE("select");

    companion object {
        fun fromValue(value: String): ExerciseListMode =
            values().firstOrNull { it.value == value } ?: VIEW_MODE
    }
}

/**
 * Режим экрана деталей упражнения
 */
enum class ExerciseDetailMode(val value: String) {
    DESIGN_MODE("design"),
    WORKOUT_MODE("workout");

    companion object {
        fun fromValue(value: String): ExerciseDetailMode =
            values().firstOrNull { it.value == value } ?: DESIGN_MODE
    }
}

/**
 * Режим экрана списка шаблонов
 */
enum class TemplateListMode(val value: String) {
    VIEW_MODE("view"),
    SELECT_MODE("select");

    companion object {
        fun fromValue(value: String): TemplateListMode =
            values().firstOrNull { it.value == value } ?: VIEW_MODE
    }
}

/**
 * Режим экрана деталей шаблона
 */
enum class TemplateDetailMode(val value: String) {
    CREATE_MODE("create"),
    EDIT_MODE("edit"),
    VIEW_MODE("view");

    companion object {
        fun fromValue(value: String): TemplateDetailMode =
            values().firstOrNull { it.value == value } ?: VIEW_MODE
    }
}

sealed class Screen(val route: String) {
    data object Splash: Screen("splash")
    data object Main: Screen("main")
    data object SignIn: Screen("sign_in")
    data object SignUp: Screen("sign_up")
    data object Timer: Screen("timer")
    data object Profile: Screen("profile")
    data object Library: Screen("library")
    data object CrashlyticsTest: Screen("crashlytics_test")
    data object ExerciseList: Screen("exercises/{mode}") {
        fun createRoute(mode: ExerciseListMode) = "exercises/${mode.value}"

        const val MODE_ARG = "mode"
    }
    data object ExerciseDetail: Screen("exercise/{exerciseId}/{mode}") {
        fun createRoute(exerciseId: String, mode: ExerciseDetailMode) =
            "exercise/$exerciseId/${mode.value}"

        const val EXERCISE_ID_ARG = "exerciseId"
        const val MODE_ARG = "mode"
    }
    data object WorkoutExerciseDetail: Screen("workout_exercise/{workoutExerciseId}") {
        fun createRoute(workoutExerciseId: String) =
            "workout_exercise/$workoutExerciseId"

        const val WORKOUT_EXERCISE_ID_ARG = "workoutExerciseId"
    }
    data object TemplateList: Screen("templates/{mode}") {
        fun createRoute(mode: TemplateListMode = TemplateListMode.VIEW_MODE) =
            "templates/${mode.value}"

        const val MODE_ARG = "mode"
    }
    data object TemplateDetail: Screen("template/{templateId}/{mode}") {
        fun createRoute(templateId: String? = null, mode: TemplateDetailMode) =
            "template/${templateId ?: "new"}/${mode.value}"

        const val TEMPLATE_ID_ARG = "templateId"
        const val MODE_ARG = "mode"
    }
}
