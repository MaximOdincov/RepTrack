package com.example.reptrack.navigation

/**
 * Режим экрана списка упражнений
 */
enum class ExerciseListMode(val value: String) {
    VIEW_MODE("view"),
    WORKOUT_MODE("workout");

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

sealed class Screen(val route: String) {
    data object Splash: Screen("splash")
    data object Main: Screen("main")
    data object SignIn: Screen("sign_in")
    data object SignUp: Screen("sign_up")
    data object Timer: Screen("timer")
    data object Profile: Screen("profile")
    data object Library: Screen("library")
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
}
