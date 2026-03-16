package com.example.reptrack.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reptrack.App
import com.example.reptrack.di.databaseModule
import com.example.reptrack.di.exerciseModule
import com.example.reptrack.di.profileModule
import com.example.reptrack.di.workoutModule
import com.example.reptrack.domain.profile.usecases.AddUserUseCase
import com.example.reptrack.domain.auth.usecases.GetCurrentUserUseCase
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
import com.example.reptrack.presentation.exercise.detail.screens.ExerciseDetailScreen
import com.example.reptrack.presentation.exercise.detail.stores.ExerciseDetailStoreFactory
import com.example.reptrack.presentation.main.screens.MainScreen
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.profile.screens.ProfileScreen
import com.example.reptrack.presentation.profile.stores.ProfileStore
import com.example.reptrack.presentation.profile.stores.ProfileStoreFactory
import com.example.reptrack.presentation.timer.screens.TimerScreen
import com.example.reptrack.data.auth.toDomain
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()
    var authenticatedModulesLoaded = remember { false }

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
                            // Load modules that require database only after successful auth
                            if (!authenticatedModulesLoaded) {
                                App.loadAuthenticatedModules()
                                authenticatedModulesLoaded = true
                            }

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
                            // Load modules that require database only after successful auth
                            if (!authenticatedModulesLoaded) {
                                App.loadAuthenticatedModules()
                                authenticatedModulesLoaded = true
                            }

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
                            // Load modules that require database only after successful auth
                            if (!authenticatedModulesLoaded) {
                                App.loadAuthenticatedModules()
                                authenticatedModulesLoaded = true
                            }

                            navController.navigate(Screen.Main.route){
                                popUpTo(Screen.SignUp.route){inclusive = true}
                            }
                        },
                    )
                }

                composable(Screen.Main.route) {
                    val store: MainScreenStore = getKoin().get()
                    val calendarUseCase: CalendarUseCase = getKoin().get()
                    val addUserUseCase: AddUserUseCase = getKoin().get()
                    val getCurrentUserUseCase: GetCurrentUserUseCase = getKoin().get()

                    // Add user to database on first entry to Main screen
                    LaunchedEffect(Unit) {
                        try {
                            val authUser = getCurrentUserUseCase()
                            authUser?.let { addUserUseCase(it.toDomain()) }
                        } catch (e: Exception) {
                            android.util.Log.e("NavGraph", "Failed to add user: ${e.message}")
                        }
                    }

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
                            navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId, ExerciseDetailMode.DESIGN_MODE))
                        },
                        onAddToWorkoutAndBack = { exercise ->
                            navController.popBackStack()
                        },
                        onNavigateToAddExercise = {
                            navController.navigate(Screen.ExerciseDetail.createRoute("new", ExerciseDetailMode.DESIGN_MODE))
                        },
                        onInitialize = {
                            store.accept(ExerciseListStore.Intent.Initialize(mode))
                        }
                    )
                }

                composable(
                    route = Screen.ExerciseDetail.route,
                    arguments = listOf(
                        navArgument(Screen.ExerciseDetail.EXERCISE_ID_ARG) {
                            type = NavType.StringType
                        },
                        navArgument(Screen.ExerciseDetail.MODE_ARG) {
                            type = NavType.StringType
                            defaultValue = ExerciseDetailMode.DESIGN_MODE.value
                        }
                    )
                ) { backStackEntry ->
                    val exerciseId = backStackEntry.arguments?.getString(Screen.ExerciseDetail.EXERCISE_ID_ARG) ?: ""
                    val modeValue = backStackEntry.arguments?.getString(Screen.ExerciseDetail.MODE_ARG)
                    val mode = ExerciseDetailMode.fromValue(modeValue ?: ExerciseDetailMode.DESIGN_MODE.value)

                    val storeFactory: ExerciseDetailStoreFactory = getKoin().get()

                    // Use remember to keep the same store instance across recompositions
                    val store = remember(exerciseId, mode) {
                        storeFactory.create(exerciseId, mode)
                    }

                    // Store will be automatically garbage collected when screen is destroyed
                    // No manual dispose needed - MVIKotlin handles lifecycle

                    ExerciseDetailScreen(
                        store = store,
                        exerciseId = exerciseId,
                        mode = mode,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.Timer.route){
                    TimerScreen()
                }

                composable(Screen.Profile.route){
                    val storeFactory: ProfileStoreFactory = getKoin().get()

                    // Use remember to keep the same store instance across recompositions
                    val store = remember {
                        storeFactory.create()
                    }

                    ProfileScreen(
                        store = store,
                        onSignedOut = {
                            // 1. Reset the flag so modules can be loaded again for new user
                            authenticatedModulesLoaded = false

                            // 2. Navigate to Sign In screen
                            navController.navigate(Screen.SignIn.route) {
                                popUpTo(Screen.Main.route) { inclusive = true }
                            }

                            // 3. Unload modules after navigation has started
                            // Use a coroutine to give navigation time to start
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(100) // Small delay for navigation to start
                                App.unloadAuthenticatedModules()
                            }
                        }
                    )
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
