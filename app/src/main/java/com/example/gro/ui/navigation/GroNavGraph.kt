package com.example.gro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gro.ui.screen.deposit.DepositScreen
import com.example.gro.ui.screen.garden.GardenScreen
import com.example.gro.ui.screen.onboarding.OnboardingScreen
import com.example.gro.ui.screen.plantdetail.PlantDetailScreen
import com.example.gro.ui.screen.journal.JournalScreen
import com.example.gro.ui.screen.splash.SplashScreen
import com.example.gro.ui.screen.visit.VisitGardenScreen
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
                onNavigateToDeposit = {
                    navController.navigate(Screen.Deposit.route)
                },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                },
                onNavigateToVisit = {
                    navController.navigate(Screen.VisitGarden.route)
                },
                onNavigateToJournal = {
                    navController.navigate(Screen.Journal.route)
                },
            )
        }

        composable(
            route = Screen.PlantDetail.route,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType }),
        ) {
            PlantDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDeposit = { navController.navigate(Screen.Deposit.route) },
            )
        }

        composable(Screen.VisitGarden.route) {
            VisitGardenScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Journal.route) {
            JournalScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Deposit.route) {
            DepositScreen(
                activityResultSender = activityResultSender,
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}
