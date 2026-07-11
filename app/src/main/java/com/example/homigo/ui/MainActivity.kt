package com.example.homigo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.homigo.data.repository.HomigoRepository
import com.example.homigo.ui.screens.*
import com.example.homigo.ui.theme.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomigoRepository.init(applicationContext)
        setContent {
            val tokenState = HomigoRepository.token.collectAsState()
            val currentUserState = HomigoRepository.currentUser.collectAsState()
            val gender = currentUserState.value?.gender ?: "male"
            val completionState = HomigoRepository.profileCompletion.collectAsState()
            val completion = completionState.value

            LaunchedEffect(tokenState.value) {
                if (tokenState.value != null) {
                    try {
                        HomigoRepository.fetchProfile()
                    } catch (e: Exception) {
                        // ignore if profile not set up yet
                    }
                }
            }

            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            HomigoTheme(gender = gender, completion = completion) {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = "splash",
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                )
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                )
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                )
                            }
                        ) {
                            composable("splash") {
                                SplashScreen(
                                    onSplashComplete = {
                                        navController.navigate("onboarding") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("onboarding") {
                                OnboardingWizardScreen(
                                    onOnboardingComplete = {
                                        navController.navigate("main") {
                                            popUpTo("onboarding") { inclusive = true }
                                        }
                                    },
                                    onNavigateToLogin = {
                                        navController.navigate("login") {
                                            popUpTo("onboarding") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("login") {
                                LoginScreen(
                                    onLoginSuccess = { hasProfile ->
                                        if (hasProfile) {
                                            navController.navigate("main") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate("onboarding") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    },
                                    onNavigateToRegister = {
                                        navController.navigate("onboarding")
                                    }
                                )
                            }

                            composable("main") {
                                MainTabScreen(
                                    onNavigateToChat = { otherUserId, otherUserName ->
                                        navController.navigate("chat/$otherUserId/$otherUserName")
                                    },
                                    onNavigateToChatbot = { navController.navigate("chatbot") },
                                    onNavigateToExpenses = { navController.navigate("expenses") },
                                    onNavigateToRequests = { navController.navigate("requests") },
                                    onNavigateToReviews = { navController.navigate("reviews") },
                                    onLogout = { message ->
                                        navController.navigate("onboarding") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                        scope.launch {
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    },
                                    onShowSnackbar = { message ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    }
                                )
                            }

                        composable("chatbot") {
                            ChatbotScreen()
                        }

                        composable("expenses") {
                            ExpenseScreen()
                        }

                        composable("requests") {
                            RequestsScreen()
                        }

                        composable("reviews") {
                            ReviewsScreen()
                        }

                        composable(
                            route = "chat/{userId}/{userName}",
                            arguments = listOf(
                                navArgument("userId") { type = NavType.IntType },
                                navArgument("userName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val userName = backStackEntry.arguments?.getString("userName") ?: "Roommate"
                            ChatScreen(
                                otherUserId = userId,
                                otherUserName = userName,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        }
                    }
                }
            }
        }
    }
}

// ─── TAB DEFINITION ──────────────────────────────────────────────────────────

private val tabs = listOf(
    BottomTab("Home",     Icons.Filled.Home,       Icons.Outlined.Home),
    BottomTab("Discover", Icons.Filled.Search,      Icons.Outlined.Search),
    BottomTab("AI Radar", Icons.Filled.Star,        Icons.Outlined.Star),
    BottomTab("Chats",     Icons.Filled.Email,       Icons.Outlined.Email),
    BottomTab("Me",       Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
)

// ─── MAIN TAB SCREEN ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(
    onNavigateToChat: (otherUserId: Int, otherUserName: String) -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onLogout: (String) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val currentUserState = HomigoRepository.currentUser.collectAsState()
    val completionState = HomigoRepository.profileCompletion.collectAsState()

    val gender = currentUserState.value?.gender ?: "male"
    val completion = completionState.value

    Scaffold(
        bottomBar = {
            FloatingGlassBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                tabs = tabs,
                gender = gender,
                completion = completion
            )
        },
        containerColor = Color.Transparent // Allow background gradients to flow fully
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(
                        onNavigateToChat = onNavigateToChat,
                        onCompleteProfileClick = { selectedTab = 4 },
                        onNavigateToChatbot = onNavigateToChatbot,
                        onNavigateToExpenses = onNavigateToExpenses,
                        onNavigateToRequests = onNavigateToRequests,
                        onNavigateToReviews = onNavigateToReviews,
                        onNavigateToDiscover = { selectedTab = 1 }
                    )
                    1 -> ExploreScreen()
                    2 -> AIRadarScreen()
                    3 -> ChatListScreen(onNavigateToChat = onNavigateToChat)
                    4 -> ProfileSetupScreen(
                        onSetupComplete = {},
                        onLogout = onLogout,
                        onShowSnackbar = onShowSnackbar
                    )
                }
            }
        }
    }
}
