package com.example.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.SessionManager
import com.example.presentation.dashboard.MainHostScreen
import com.example.presentation.dashboard.DashboardViewModel
import com.example.presentation.dashboard.ResultDetailsScreen
import com.example.presentation.dashboard.SettingsScreen
import com.example.presentation.dashboard.ScanComparisonScreen
import com.example.presentation.auth.AuthScreen
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.onboarding.OnboardingScreen
import com.example.presentation.onboarding.OnboardingViewModel
import com.example.presentation.splash.SplashScreen
import com.example.presentation.splash.SplashViewModel

@Composable
fun ResyncNavHost(
    sessionManager: SessionManager,
    dashboardViewModel: DashboardViewModel,
    splashViewModel: SplashViewModel,
    authViewModel: AuthViewModel,
    onboardingViewModel: OnboardingViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier.fillMaxSize()
    ) {
        // 1. Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                viewModel = splashViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Screen
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Auth Screen (Login & Register)
        composable(Screen.Auth.route) {
            AuthScreen(
                viewModel = authViewModel,
                onNavigateToDashboard = {
                    navController.navigate(Screen.DashboardHost.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Main Host (holds bottom navigation with 4 tabs)
        composable(Screen.DashboardHost.route) {
            MainHostScreen(
                viewModel = dashboardViewModel,
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.DashboardHost.route) { inclusive = true }
                    }
                },
                onNavigateToDetails = { coherenceScore, url, missingSections ->
                    navController.navigate(Screen.ResultDetails.createRoute(coherenceScore, url, missingSections))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToComparison = { scanIds ->
                    navController.navigate(Screen.ScanComparison.createRoute(scanIds))
                }
            )
        }

        // Settings Screen (pushed, hiding bottom navigation)
        composable(Screen.Settings.route) {
            SettingsScreen(
                sessionManager = sessionManager,
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.DashboardHost.route) { inclusive = true }
                    }
                }
            )
        }

        // Scan Comparison Screen
        composable(
            route = Screen.ScanComparison.route,
            arguments = listOf(
                navArgument("scanIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val scanIds = backStackEntry.arguments?.getString("scanIds") ?: ""
            ScanComparisonScreen(
                scanIds = scanIds,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 4. Result Details (stacks on top of bottom navigation)
        composable(
            route = Screen.ResultDetails.route,
            arguments = listOf(
                navArgument("coherenceScore") { type = NavType.IntType },
                navArgument("url") { type = NavType.StringType },
                navArgument("missingSections") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                androidx.navigation.navDeepLink { uriPattern = "resync://results/{coherenceScore}/{url}/{missingSections}" }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("coherenceScore") ?: 0
            val rawUrl = backStackEntry.arguments?.getString("url") ?: ""
            val rawMissing = backStackEntry.arguments?.getString("missingSections") ?: "none"
            
            // Decode the URL to restore its original format
            val decodedUrl = try {
                java.net.URLDecoder.decode(rawUrl, "UTF-8")
            } catch (e: Exception) {
                rawUrl
            }
            
            val decodedMissing = try {
                java.net.URLDecoder.decode(rawMissing, "UTF-8")
            } catch (e: Exception) {
                rawMissing
            }
            
            val missingSectionsList = if (decodedMissing == "none" || decodedMissing.isBlank()) {
                emptyList()
            } else {
                decodedMissing.split(",")
            }

            ResultDetailsScreen(
                coherenceScore = score,
                url = decodedUrl,
                missingSections = missingSectionsList,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
