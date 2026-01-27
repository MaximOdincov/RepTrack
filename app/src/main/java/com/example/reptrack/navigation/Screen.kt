package com.example.reptrack.navigation

sealed class Screen(val route: String) {
    data object Splash: Screen("splash")
    data object Main: Screen("main")
    data object SignIn: Screen("sign_in")
    data object SignUp: Screen("sign_up")
}