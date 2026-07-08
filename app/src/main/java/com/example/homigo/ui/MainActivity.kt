package com.example.homigo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.homigo.data.repository.HomigoRepository
import com.example.homigo.ui.screens.*
import com.example.homigo.ui.theme.HomigoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Retrieve session details dynamically
            val currentUserState = HomigoRepository.currentUser.collectAsState()
            val gender = currentUserState.value?.gender ?: "male"

            // Apply dynamic Theme wrapper based on user gender
            HomigoTheme(gender = gender) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(400)) },
                        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(400)) },
                        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(400)) },
                        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(400)) }
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
                                onLogout = {
                                    HomigoRepository.clearSession()
                                    navController.navigate("login") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            )
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
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(
    onNavigateToChat: (otherUserId: Int, otherUserName: String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabLabels = listOf("Explore", "Invites", "Chats", "Splitter", "Assistant", "Ratings", "Profile")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Homigo", fontWeight = FontWeight.Bold) 
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Log Out", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(72.dp)
            ) {
                tabLabels.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(label, fontSize = 9.sp) },
                        icon = {
                            // Simple text icon placeholder or circle badge
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(1.dp)
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> ExploreScreen()
                1 -> RequestsScreen()
                2 -> ChatListScreen(onNavigateToChat = onNavigateToChat)
                3 -> ExpenseScreen()
                4 -> ChatbotScreen()
                5 -> ReviewsScreen()
                6 -> ProfileSetupScreen(onSetupComplete = {
                    // Profile setup complete action inside main tabs
                })
            }
        }
    }
}
