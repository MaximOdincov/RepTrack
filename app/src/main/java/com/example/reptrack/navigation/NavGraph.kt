package com.example.reptrack.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.navigation.components.BottomBar
import com.example.reptrack.presentation.auth.signIn.SignInScreen
import com.example.reptrack.presentation.auth.signIn.SignInStore
import com.example.reptrack.presentation.auth.signUp.SignUpScreen
import com.example.reptrack.presentation.auth.signUp.SignUpStore
import com.example.reptrack.presentation.auth.splash.SplashScreen
import com.example.reptrack.presentation.auth.splash.SplashStore
import com.example.reptrack.presentation.exercise.list.screens.ExerciseListScreen
import com.example.reptrack.presentation.exercise.list.stores.ExerciseListStore
import com.example.reptrack.presentation.main.screens.MainScreen
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.profile.screens.ProfileScreen
import com.example.reptrack.presentation.timer.screens.TimerScreen
import org.koin.compose.getKoin

/**
 * Routes that should NOT show bottom bar
 */
private val BOTTOM_BAR_EXCLUDED_ROUTES = listOf(
    Screen.Splash.route,
    Screen.SignIn.route,
    Screen.SignUp.route
)

/**
 * Check if current route should show bottom bar
 */
@Composable
private fun shouldShowBottomBar(navController: NavController): Boolean {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination
    return currentDestination?.route !in BOTTOM_BAR_EXCLUDED_ROUTES
}

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(navController)) {
                BottomBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
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
                    val store: MainScreenStore = getKoin().get()
                    val calendarUseCase: CalendarUseCase = getKoin().get()

                    MainScreen(
                        store = store,
                        calendarUseCase = calendarUseCase
                    )
                }

                composable(
                    route = Screen.ExerciseList.route,
                    arguments = listOf(
                        navArgument(Screen.ExerciseList.MODE_ARG) {
                            type = NavType.StringType
                            defaultValue = ExerciseListMode.VIEW_MODE.value
                        }
                    )
                ) { backStackEntry ->
                    val modeValue = backStackEntry.arguments?.getString(Screen.ExerciseList.MODE_ARG)
                    val mode = ExerciseListMode.fromValue(modeValue ?: ExerciseListMode.VIEW_MODE.value)

                    val store: ExerciseListStore = getKoin().get()

                    ExerciseListScreen(
                        store = store,
                        onNavigateToDetail = { exerciseId ->
                            // TODO: Navigate to ExerciseDetail when implemented
                            // navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId, ExerciseDetailMode.DESIGN_MODE))
                        },
                        onAddToWorkoutAndBack = { exercise ->
                            // TODO: Add exercise to current workout
                            navController.popBackStack()
                        },
                        onNavigateToAddExercise = {
                            navController.navigate(Screen.Main.route)
                        },
                        onInitialize = {
                            store.accept(ExerciseListStore.Intent.Initialize(mode))
                        }
                    )
                }

                composable(Screen.Timer.route){
                    TimerScreen()
                }

                composable(Screen.Profile.route){
                    ProfileScreen()
                }

                composable(Screen.Library.route){
                    navController.navigate(Screen.ExerciseList.createRoute(ExerciseListMode.VIEW_MODE)) {
                        popUpTo(Screen.Library.route) { inclusive = true }
                    }
                }
            }
        }
    }
}
