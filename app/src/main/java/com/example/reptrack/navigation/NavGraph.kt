package com.example.reptrack.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.App
import com.example.reptrack.domain.profile.usecases.AddUserUseCase
import com.example.reptrack.domain.profile.usecases.InitializeDatabaseUseCase
import com.example.reptrack.domain.auth.usecases.GetCurrentUserUseCase
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.navigation.components.BottomBar
import kotlinx.coroutines.flow.first
import com.example.reptrack.presentation.auth.signIn.SignInScreen
import com.example.reptrack.presentation.auth.signIn.SignInStore
import com.example.reptrack.presentation.auth.signUp.screens.SignUpScreen
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
import com.example.reptrack.presentation.workout_exercise.detail.stores.WorkoutExerciseDetailStoreFactory
import com.example.reptrack.presentation.main.stores.MainScreenStore
import com.example.reptrack.presentation.profile.screens.ProfileScreen
import com.example.reptrack.presentation.profile.stores.ProfileStoreFactory
import com.example.reptrack.presentation.profile.stores.FriendsStoreFactory
import com.example.reptrack.presentation.timer.screens.TimerScreen
import com.example.reptrack.presentation.crashlytics_test.CrashlyticsTestScreen
import com.example.reptrack.data.auth.toDomain
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.domain.workout.usecases.templates.CreateWorkoutTemplateUseCase
import com.example.reptrack.presentation.library.screens.LibraryScreen
import com.example.reptrack.presentation.timer.stores.TimerStore
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
fun AppNavGraph(
    onThemeChanged: (Boolean) -> Unit = {}
) {
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
                startDestination = Screen.Splash.route,
                enterTransition = { androidx.compose.animation.EnterTransition.None },
                exitTransition = { androidx.compose.animation.ExitTransition.None },
                popEnterTransition = { androidx.compose.animation.EnterTransition.None },
                popExitTransition = { androidx.compose.animation.ExitTransition.None }
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
                        },
                        onNavigateBack = {
                            navController.popBackStack()
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
                        onBackToSignIn = {
                            navController.navigate(Screen.SignIn.route) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Main.route) {
                    val store: MainScreenStore = getKoin().get()
                    val calendarUseCase: CalendarUseCase = getKoin().get()
                    val addUserUseCase: AddUserUseCase = getKoin().get()
                    val initializeDatabaseUseCase: InitializeDatabaseUseCase = getKoin().get()
                    val getCurrentUserUseCase: GetCurrentUserUseCase = getKoin().get()
                    val getCurrentUserProfileUseCase: com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase = getKoin().get()
                    val firebaseUserDataSource: com.example.reptrack.data.auth.FirebaseUserDataSource = getKoin().get()

                    // Add user to database on first entry to Main screen
                    LaunchedEffect(Unit) {
                        val authUser = getCurrentUserUseCase()
                        authUser?.let { auth ->
                            // Add user to local database
                            addUserUseCase(auth.toDomain())

                            // Initialize database with default exercises and templates
                            initializeDatabaseUseCase()

                            // Wait a bit for database to be ready
                            kotlinx.coroutines.delay(100)

                            // Get the user from database to get username, avatarUrl, and passkey
                            getCurrentUserProfileUseCase().collect { user ->
                                user?.let {
                                    // Also save user to Firebase with passkey
                                    firebaseUserDataSource.saveUser(
                                        userId = it.id,
                                        username = it.username,
                                        email = it.email,
                                        avatarUrl = it.avatarUrl,
                                        passkey = it.passkey
                                    )
                                    // Only need to save once
                                    return@collect
                                }
                            }
                        }
                    }

                    val state by store.states.collectAsState(com.example.reptrack.presentation.main.stores.MainScreenStore.State())

                    MainScreen(
                        store = store,
                        calendarUseCase = calendarUseCase,
                        onNavigateToExerciseDetail = { workoutExerciseId ->
                            navController.navigate(Screen.WorkoutExerciseDetail.createRoute(workoutExerciseId))
                        },
                        onNavigateToLibrary = {
                            navController.navigate(Screen.LibraryAddToWorkout.createRoute(state.currentDate.toString()))
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

                    // Try to get templateId from previous backStackEntry (if came from TemplateDetail)
                    val templateId = navController.previousBackStackEntry
                        ?.arguments
                        ?.getString(Screen.TemplateDetail.TEMPLATE_ID_ARG)

                    io.github.aakira.napier.Napier.i(
                        "ExerciseList: mode=$mode, templateId=$templateId",
                        tag = "NavGraph"
                    )

                    val store: ExerciseListStore = getKoin().get()
                    val coroutineScope = rememberCoroutineScope()
                    val updateTemplateUseCase: com.example.reptrack.domain.workout.usecases.templates.UpdateWorkoutTemplateUseCase = getKoin().get()
                    val observeTemplateUseCase: com.example.reptrack.domain.workout.usecases.templates.ObserveWorkoutTemplateByIdUseCase = getKoin().get()

                    ExerciseListScreen(
                        store = store,
                        onNavigateToDetail = { exerciseId ->
                            navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId, ExerciseDetailMode.DESIGN_MODE))
                        },
                        onAddToWorkoutAndBack = { exercise ->
                            navController.popBackStack()
                        },
                        onAddToTemplateAndBack = { exercise ->
                            if (templateId != null) {
                                // Immediately save exercise to template in database
                                io.github.aakira.napier.Napier.i(
                                    "Adding exercise ${exercise.id} to template $templateId",
                                    tag = "NavGraph"
                                )

                                // Run in coroutine and wait for completion
                                coroutineScope.launch {
                                    try {
                                        // Load current template
                                        val template = observeTemplateUseCase(templateId).first()
                                        io.github.aakira.napier.Napier.i(
                                            "Loaded template: $template",
                                            tag = "NavGraph"
                                        )

                                        if (template != null) {
                                            // Add exercise to template
                                            val updatedTemplate = template.copy(
                                                exerciseIds = template.exerciseIds + exercise.id
                                            )
                                            // Save to database
                                            val result = updateTemplateUseCase(updatedTemplate)
                                            if (result.isSuccess) {
                                                io.github.aakira.napier.Napier.i(
                                                    "Successfully added exercise to template. New exerciseIds: ${updatedTemplate.exerciseIds}",
                                                    tag = "NavGraph"
                                                )
                                            } else {
                                                io.github.aakira.napier.Napier.e(
                                                    "Failed to add exercise: ${result.exceptionOrNull()?.message}",
                                                    tag = "NavGraph"
                                                )
                                            }
                                        } else {
                                            io.github.aakira.napier.Napier.e(
                                                "Template is null! Cannot add exercise",
                                                tag = "NavGraph"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        io.github.aakira.napier.Napier.e(
                                            "Error adding exercise to template: ${e.message}",
                                            tag = "NavGraph"
                                        )
                                        e.printStackTrace()
                                    }
                                    // Navigate back AFTER saving completes
                                    navController.popBackStack()
                                }
                            } else {
                                // No templateId, just go back
                                io.github.aakira.napier.Napier.w(
                                    "No templateId provided, cannot add exercise to template",
                                    tag = "NavGraph"
                                )
                                navController.popBackStack()
                            }
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
                    val createTemplateUseCase: com.example.reptrack.domain.workout.usecases.templates.CreateWorkoutTemplateUseCase = getKoin().get()

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
                            // Create empty template immediately, then navigate to edit it
                            coroutineScope.launch {
                                val emptyTemplate = com.example.reptrack.domain.workout.entities.WorkoutTemplate(
                                    id = "template_${System.currentTimeMillis()}",
                                    name = "",
                                    description = "",
                                    iconId = "custom",
                                    exerciseIds = emptyList(),
                                    iconRes = null,
                                    iconColor = null,
                                    muscleGroups = emptyList(),
                                    isCustom = true
                                )
                                val result = createTemplateUseCase(emptyTemplate)
                                if (result.isSuccess) {
                                    navController.navigate(Screen.TemplateDetail.createRoute(emptyTemplate.id, TemplateDetailMode.EDIT_MODE))
                                } else {
                                    io.github.aakira.napier.Napier.e(
                                        "Failed to create empty template: ${result.exceptionOrNull()?.message}",
                                        tag = "NavGraph"
                                    )
                                }
                            }
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

                    // Create ONE store per templateId and keep it alive
                    // This way data is already loaded when we navigate to the same template again
                    val store = remember(templateId) {
                        io.github.aakira.napier.Napier.i(
                            "Creating store for templateId=$templateId, mode=$mode",
                            tag = "NavGraph"
                        )
                        storeFactory.create()
                    }

                    // Get state and wait for initialization
                    val state by store.states.collectAsState(TemplateDetailStore.State())

                    // Pre-initialize BEFORE screen composition
                    LaunchedEffect(templateId, mode) {
                        io.github.aakira.napier.Napier.i(
                            "Initializing store: templateId=$templateId, mode=$mode",
                            tag = "NavGraph"
                        )
                        store.accept(TemplateDetailStore.Intent.Initialize(templateId, mode))
                    }

                    // Show loading spinner while data is being loaded for EDIT_MODE
                    if (mode == TemplateDetailStore.TemplateDetailMode.EDIT_MODE && templateId != null && !state.isInitialized) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        TemplateDetailScreen(
                            store = store,
                            templateId = templateId,
                            mode = mode,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToExerciseSelection = {
                                navController.navigate(Screen.ExerciseList.createRoute(ExerciseListMode.SELECT_MODE))
                            }
                        )
                    }
                }

                composable(Screen.Timer.route){
                    val storeFactory: com.example.reptrack.presentation.timer.stores.TimerStoreFactory = getKoin().get()
                    val store = remember { storeFactory.create() }

                    val state by store.states.collectAsState(initial = com.example.reptrack.presentation.timer.stores.TimerStore.State())

                    LaunchedEffect(store) {
                        store.labels.collect {label ->
                            when (label) {
                                TimerStore.Label.TimerCompleted -> {
                                    // Timer completed - sound is played in the store
                                }
                                else -> {}
                            }
                        }
                    }

                    TimerScreen(store = store)
                }

                composable(Screen.Profile.route){
                    // Load authenticated modules before creating ProfileScreen
                    LaunchedEffect(Unit) {
                        if (!authenticatedModulesLoaded) {
                            App.loadAuthenticatedModules()
                            authenticatedModulesLoaded = true
                        }
                    }

                    val storeFactory: ProfileStoreFactory = getKoin().get()
                    val friendsStoreFactory: FriendsStoreFactory = getKoin().get()
                    val statisticsStore: com.example.reptrack.presentation.statistics.stores.StatisticsStore = getKoin().get()

                    // Use remember to keep the same store instance across recompositions
                    val store = remember {
                        storeFactory.create()
                    }
                    val friendsStore = remember {
                        friendsStoreFactory.create()
                    }

                    ProfileScreen(
                        store = store,
                        friendsStore = friendsStore,
                        statisticsStore = statisticsStore,
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
                        },
                        onNavigateToStatistics = {
                            navController.navigate(Screen.Statistics.route)
                        }
                    )
                }

                composable(Screen.Statistics.route){
                    val statisticsStore: com.example.reptrack.presentation.statistics.stores.StatisticsStore = getKoin().get()
                    val friendsStore: com.example.reptrack.presentation.profile.stores.FriendsStore = getKoin().get()
                    val getFriendsUseCase: com.example.reptrack.domain.friends.usecases.GetFriendsUseCase = getKoin().get()
                    val observeAllExercisesUseCase: com.example.reptrack.domain.workout.usecases.exercises.ObserveAllExercisesUseCase = getKoin().get()
                    val getCurrentUserProfileUseCase: com.example.reptrack.domain.profile.usecases.GetCurrentUserProfileUseCase = getKoin().get()

                    // Collect friends state
                    val friends by friendsStore.states.collectAsState(com.example.reptrack.presentation.profile.stores.FriendsStore.State())

                    // Get current user
                    var user by remember { mutableStateOf<com.example.reptrack.domain.profile.User?>(null) }
                    LaunchedEffect(Unit) {
                        getCurrentUserProfileUseCase().collect { userProfile ->
                            user = userProfile
                        }
                    }

                    // Load exercises and friends
                    LaunchedEffect(Unit) {
                        android.util.Log.d("important", "=== NavGraph LaunchedEffect - loading data ===")
                        android.util.Log.d("important", "Calling statisticsStore.LoadData...")
                        statisticsStore.accept(com.example.reptrack.presentation.statistics.stores.StatisticsStore.Intent.LoadData)
                        android.util.Log.d("important", "Calling friendsStore.LoadFriends...")
                        friendsStore.accept(com.example.reptrack.presentation.profile.stores.FriendsStore.Intent.LoadFriends)
                    }

                    // Collect exercises
                    var exercises by remember { mutableStateOf(emptyList<com.example.reptrack.domain.workout.entities.Exercise>()) }
                    LaunchedEffect(Unit) {
                        observeAllExercisesUseCase().collect { exerciseMap ->
                            exercises = exerciseMap.values.flatten()

                            // Log all user exercises with IDs
                            android.util.Log.d("EXERCISE_IDS", "=== 📋 USER'S EXERCISES ===")
                            android.util.Log.d("EXERCISE_IDS", "Total exercises: ${exercises.size}")
                            exercises.forEach { exercise ->
                                android.util.Log.d("EXERCISE_IDS", "   🏋️ ID: ${exercise.id} | Name: ${exercise.name} | Custom: ${exercise.isCustom}")
                            }
                        }
                    }

                    // Log friends state
                    LaunchedEffect(friends.friends) {
                        android.util.Log.d("important", "=== Friends state changed in NavGraph ===")
                        android.util.Log.d("important", "friends size: ${friends.friends.size}")
                        android.util.Log.d("important", "friends: ${friends.friends.map { it.friendUserId to (it.username ?: "Unknown") }}")
                    }

                    com.example.reptrack.presentation.statistics.screens.StatisticsScreen(
                        store = statisticsStore,
                        getFriendsUseCase = getFriendsUseCase,
                        exercises = exercises,
                        friends = friends.friends,
                        isGuest = user?.isGuest ?: true,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                composable(Screen.Library.route){
                    val exerciseStore: ExerciseListStore = getKoin().get()
                    val templateStoreFactory: TemplateListStoreFactory = getKoin().get()
                    val createTemplateUseCase: CreateWorkoutTemplateUseCase = getKoin().get()

                    // Use remember to keep the same store instance across recompositions
                    val templateStore = remember {
                        templateStoreFactory.create()
                    }

                    LibraryScreen(
                        exerciseStore = exerciseStore,
                        templateStore = templateStore,
                        mode = com.example.reptrack.presentation.library.screens.LibraryMode.VIEW,
                        onNavigateToExerciseDetail = { exerciseId ->
                            navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId, ExerciseDetailMode.DESIGN_MODE))
                        },
                        onNavigateToTemplateDetail = { templateId ->
                            navController.navigate(Screen.TemplateDetail.createRoute(templateId, TemplateDetailMode.EDIT_MODE))
                        },
                        onNavigateToAddExercise = {
                            navController.navigate(Screen.ExerciseDetail.createRoute("new", ExerciseDetailMode.DESIGN_MODE))
                        },
                        onNavigateToAddTemplate = {
                            // Create empty template immediately, then navigate to edit it
                            coroutineScope.launch {
                                val emptyTemplate = WorkoutTemplate(
                                    id = "template_${System.currentTimeMillis()}",
                                    name = "",
                                    description = "",
                                    iconId = "custom",
                                    exerciseIds = emptyList(),
                                    iconRes = null,
                                    iconColor = null,
                                    muscleGroups = emptyList(),
                                    isCustom = true
                                )
                                val result = createTemplateUseCase(emptyTemplate)
                                if (result.isSuccess) {
                                    navController.navigate(Screen.TemplateDetail.createRoute(emptyTemplate.id, TemplateDetailMode.EDIT_MODE))
                                } else {
                                    io.github.aakira.napier.Napier.e(
                                        "Failed to create empty template: ${result.exceptionOrNull()?.message}",
                                        tag = "NavGraph"
                                    )
                                }
                            }
                        },
                        onAddExerciseToWorkout = {},
                        onAddTemplateToWorkout = {}
                    )
                }

                composable(
                    route = Screen.LibraryAddToWorkout.route,
                    arguments = listOf(
                        navArgument(Screen.LibraryAddToWorkout.DATE_ARG) {
                            type = NavType.StringType
                            nullable = false
                        }
                    )
                ) {
                    val dateArg = it.arguments?.getString(Screen.LibraryAddToWorkout.DATE_ARG)
                    val selectedDate = java.time.LocalDate.parse(dateArg)

                    val exerciseStore: ExerciseListStore = getKoin().get()
                    val templateStoreFactory: TemplateListStoreFactory = getKoin().get()

                    // Get use cases
                    val createWorkoutExerciseUseCase: com.example.reptrack.domain.workout.usecases.workout_exercises.CreateWorkoutExerciseUseCase = getKoin().get()
                    val createSessionUseCase: com.example.reptrack.domain.workout.usecases.sessions.CreateWorkoutSessionUseCase = getKoin().get()
                    val createSessionFromTemplateUseCase: com.example.reptrack.domain.workout.usecases.sessions.CreateWorkoutSessionFromTemplateUseCase = getKoin().get()
                    val unlinkSessionFromTemplateUseCase: com.example.reptrack.domain.workout.usecases.sessions.UnlinkSessionFromTemplateUseCase = getKoin().get()
                    val sessionRepository: com.example.reptrack.domain.workout.repositories.WorkoutSessionRepository = getKoin().get()
                    val observeExerciseByIdUseCase: com.example.reptrack.domain.workout.usecases.exercises.ObserveExerciseByIdUseCase = getKoin().get()
                    val authRepository: com.example.reptrack.domain.auth.AuthRepository = getKoin().get()

                    // Use remember to keep the same store instance across recompositions
                    val templateStore = remember {
                        templateStoreFactory.create()
                    }

                    LibraryScreen(
                        exerciseStore = exerciseStore,
                        templateStore = templateStore,
                        mode = com.example.reptrack.presentation.library.screens.LibraryMode.ADD_TO_WORKOUT,
                        onNavigateToExerciseDetail = {},
                        onNavigateToTemplateDetail = {},
                        onNavigateToAddExercise = {},
                        onNavigateToAddTemplate = {},
                        onAddExerciseToWorkout = { exercise ->
                            coroutineScope.launch {
                                try {
                                    // Get or create session for selected date
                                    var session = sessionRepository.getSessionByDate(selectedDate)
                                    val currentUser = authRepository.getCurrentUser()
                                    val userId = currentUser?.id ?: ""

                                    if (session == null && userId.isNotEmpty()) {
                                        // Create new session for selected date
                                        val newSession = com.example.reptrack.domain.workout.entities.WorkoutSession(
                                            id = java.util.UUID.randomUUID().toString(),
                                            userId = userId,
                                            date = selectedDate.atTime(9, 0),
                                            status = com.example.reptrack.domain.workout.entities.WorkoutStatus.IN_PROGRESS,
                                            name = "Тренировка",
                                            durationSeconds = 0,
                                            exercises = emptyList(),
                                            comment = null
                                        )
                                        val createResult = createSessionUseCase(newSession)
                                        if (createResult.isSuccess) {
                                            session = newSession
                                        } else {
                                            io.github.aakira.napier.Napier.e(
                                                "Failed to create session: ${createResult.exceptionOrNull()?.message}",
                                                tag = "NavGraph"
                                            )
                                            return@launch
                                        }
                                    }

                                    if (session != null) {
                                        // Create workout exercise
                                        val workoutExercise = com.example.reptrack.domain.workout.entities.WorkoutExercise(
                                            id = java.util.UUID.randomUUID().toString(),
                                            workoutSessionId = session.id,
                                            exerciseId = exercise.id,
                                            exerciseName = exercise.name,
                                            muscleGroup = exercise.muscleGroup,
                                            exerciseType = exercise.type,
                                            iconRes = exercise.iconRes,
                                            sets = emptyList(),
                                            restTimerSeconds = 60
                                        )
                                        val result = createWorkoutExerciseUseCase(workoutExercise, session.id)
                                        if (result.isSuccess) {
                                            // Unlink from template since we added an exercise manually
                                            unlinkSessionFromTemplateUseCase(session.id)
                                            navController.popBackStack()
                                        } else {
                                            io.github.aakira.napier.Napier.e(
                                                "Failed to add exercise: ${result.exceptionOrNull()?.message}",
                                                tag = "NavGraph"
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    io.github.aakira.napier.Napier.e(
                                        "Error adding exercise to workout: ${e.message}",
                                        tag = "NavGraph"
                                    )
                                }
                            }
                        },
                        onAddTemplateToWorkout = { template ->
                            coroutineScope.launch {
                                val currentUser = authRepository.getCurrentUser()
                                val userId = currentUser?.id ?: ""

                                if (userId.isEmpty()) {
                                    io.github.aakira.napier.Napier.e(
                                        "User not logged in",
                                        tag = "NavGraph"
                                    )
                                    return@launch
                                }

                                // Create session from template (unlink from template - manual addition)
                                val result = createSessionFromTemplateUseCase(
                                    templateId = template.id,
                                    userId = userId,
                                    date = selectedDate,
                                    sessionName = template.name,
                                    unlinkFromTemplate = true
                                )

                                if (result.isSuccess) {
                                    navController.popBackStack()
                                } else {
                                    io.github.aakira.napier.Napier.e(
                                        "Failed to create session from template: ${result.exceptionOrNull()?.message}",
                                        tag = "NavGraph"
                                    )
                                }
                            }
                        }
                    )
                }

                composable(Screen.CrashlyticsTest.route){
                    CrashlyticsTestScreen()
                }
            }
        }
    }
}
