package com.example.reptrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.ActivityNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reptrack.core.presentaion.MainScreen
import com.example.reptrack.feature_auth.presentation.signIn.SignInScreen
import com.example.reptrack.feature_auth.presentation.signIn.SignInStore
import com.example.reptrack.feature_auth.presentation.signUp.SignUpScreen
import com.example.reptrack.feature_auth.presentation.signUp.SignUpStore
import com.example.reptrack.feature_auth.presentation.splash.SplashScreen
import com.example.reptrack.feature_auth.presentation.splash.SplashStore
import org.koin.compose.getKoin

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ){
        composable(Screen.Splash.route){
            val store: SplashStore = getKoin().get()
            SplashScreen(
                store = store,
                onAuthorized = {
                    navController.navigate(Screen.Main.route){
                        popUpTo(Screen.Splash.route){inclusive = true}
                    }
                },
                onUnAuthorized = {
                    navController.navigate(Screen.SignIn.route){
                        popUpTo(Screen.Splash.route){inclusive = true}
                    }
                }
            )
        }

        composable(Screen.SignIn.route){
            val store: SignInStore = getKoin().get()
            SignInScreen(
                store = store,
                onAuthorized = {
                    navController.navigate(Screen.Main.route){
                        popUpTo(Screen.SignIn.route){inclusive = true}
                    }
                },
                onOpenSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route){
            val store: SignUpStore = getKoin().get()
            SignUpScreen(
                store = store,
                onAuthorized = {
                    navController.navigate(Screen.Main.route){
                        popUpTo(Screen.SignUp.route){inclusive = true}
                    }
                },
            )
        }

        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}