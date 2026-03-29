package com.example.reptrack.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.reptrack.presentation.template.detail.screens.TemplateDetailScreen
import com.example.reptrack.presentation.template.detail.stores.TemplateDetailStore
import com.example.reptrack.presentation.template.detail.stores.TemplateDetailStoreFactory
import com.example.reptrack.presentation.template.list.screens.TemplateListScreen
import com.example.reptrack.presentation.template.list.stores.TemplateListStore
import com.example.reptrack.presentation.template.list.stores.TemplateListStoreFactory
import com.example.reptrack.presentation.workout_exercise.detail.screens.WorkoutExerciseDetailScreen
import com.example.reptrack.presentation.workout_exercise.detail.stores.WorkoutExerciseDetailStore
import com.example.reptrack.presentation.workout_exercise.detail.stores.WorkoutExerciseDetailStoreFactory
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.profile.screens.ProfileScreen
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
                        calendarUseCase = calendarUseCase,
                        onNavigateToExerciseDetail = { workoutExerciseId ->
                            navController.navigate(Screen.WorkoutExerciseDetail.createRoute(workoutExerciseId))
                        },
                        onNavigateToTemplates = {
                            navController.navigate(Screen.TemplateList.createRoute(TemplateListMode.VIEW_MODE))
                        }
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
                        onAddToTemplateAndBack = { exercise ->
                            // Get the previous back stack entry to check if we came from TemplateDetail
                            val previousBackStackEntry = navController.previousBackStackEntry
                            // Just pop back - the template detail screen will handle adding the exercise
                            navController.previousBackStackEntry?.savedStateHandle?.set("added_exercise_id", exercise.id)
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

                composable(
                    route = Screen.WorkoutExerciseDetail.route,
                    arguments = listOf(
                        navArgument(Screen.WorkoutExerciseDetail.WORKOUT_EXERCISE_ID_ARG) {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val workoutExerciseId = backStackEntry.arguments?.getString(Screen.WorkoutExerciseDetail.WORKOUT_EXERCISE_ID_ARG) ?: ""

                    val storeFactory: WorkoutExerciseDetailStoreFactory = getKoin().get()

                    // Use remember to keep the same store instance across recompositions
                    val store = remember(workoutExerciseId) {
                        storeFactory.create()
                    }

                    WorkoutExerciseDetailScreen(
                        store = store,
                        workoutExerciseId = workoutExerciseId,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.TemplateList.route,
                    arguments = listOf(
                        navArgument(Screen.TemplateList.MODE_ARG) {
                            type = NavType.StringType
                            defaultValue = TemplateListMode.VIEW_MODE.value
                        }
                    )
                ) { backStackEntry ->
                    val modeValue = backStackEntry.arguments?.getString(Screen.TemplateList.MODE_ARG)
                    val mode = when (modeValue) {
                        TemplateListMode.SELECT_MODE.value -> TemplateListStore.TemplateListMode.SELECT_MODE
                        else -> TemplateListStore.TemplateListMode.VIEW_MODE
                    }

                    val storeFactory: TemplateListStoreFactory = getKoin().get()

                    // Use remember to keep the same store instance across recompositions
                    val store = remember(mode) {
                        storeFactory.create()
                    }

                    TemplateListScreen(
                        store = store,
                        onNavigateToDetail = { templateId ->
                            navController.navigate(Screen.TemplateDetail.createRoute(templateId, TemplateDetailMode.EDIT_MODE))
                        },
                        onSelectTemplateAndBack = { template ->
                            navController.popBackStack()
                        },
                        onNavigateToAddTemplate = {
                            navController.navigate(Screen.TemplateDetail.createRoute(null, TemplateDetailMode.CREATE_MODE))
                        },
                        onInitialize = { initMode ->
                            store.accept(TemplateListStore.Intent.Initialize(initMode))
                        }
                    )
                }

                composable(
                    route = Screen.TemplateDetail.route,
                    arguments = listOf(
                        navArgument(Screen.TemplateDetail.TEMPLATE_ID_ARG) {
                            type = NavType.StringType
                        },
                        navArgument(Screen.TemplateDetail.MODE_ARG) {
                            type = NavType.StringType
                            defaultValue = TemplateDetailMode.VIEW_MODE.value
                        }
                    )
                ) { backStackEntry ->
                    val templateIdArg = backStackEntry.arguments?.getString(Screen.TemplateDetail.TEMPLATE_ID_ARG)
                    val templateId = if (templateIdArg == "new") null else templateIdArg
                    val modeValue = backStackEntry.arguments?.getString(Screen.TemplateDetail.MODE_ARG)
                    val mode = when (modeValue) {
                        TemplateDetailMode.CREATE_MODE.value -> TemplateDetailStore.TemplateDetailMode.CREATE_MODE
                        TemplateDetailMode.EDIT_MODE.value -> TemplateDetailStore.TemplateDetailMode.EDIT_MODE
                        else -> TemplateDetailStore.TemplateDetailMode.VIEW_MODE
                    }

                    val storeFactory: TemplateDetailStoreFactory = getKoin().get()

                    // Use remember to keep the same store instance across recompositions
                    val store = remember(templateId, mode) {
                        storeFactory.create()
                    }

                    // Handle exercise selection result
                    val addedExerciseId by backStackEntry.savedStateHandle.getStateFlow<String?>("added_exercise_id", null).collectAsState()
                    LaunchedEffect(addedExerciseId) {
                        val exerciseId = addedExerciseId
                        if (exerciseId != null) {
                            store.accept(TemplateDetailStore.Intent.AddExerciseToTemplate(exerciseId))
                            backStackEntry.savedStateHandle.remove<String>("added_exercise_id")
                        }
                    }

                    TemplateDetailScreen(
                        store = store,
                        templateId = templateId,
                        mode = mode,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onNavigateToExerciseSelection = { currentExerciseIds ->
                            navController.navigate(Screen.ExerciseList.createRoute(ExerciseListMode.SELECT_MODE))
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
