package com.example.gro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gro.ui.screen.garden.GardenScreen
import com.example.gro.ui.screen.onboarding.OnboardingScreen
import com.example.gro.ui.screen.splash.SplashScreen
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

@Composable
fun GroNavGraph(activityResultSender: ActivityResultSender) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToGarden = {
                    navController.navigate(Screen.Garden.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                activityResultSender = activityResultSender,
                onNavigateToGarden = {
                    navController.navigate(Screen.Garden.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Garden.route) {
            GardenScreen(
                onDisconnect = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Garden.route) { inclusive = true }
                    }
                },
            )
        }
    }
}
