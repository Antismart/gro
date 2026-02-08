package com.example.gro.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gro.ui.screen.deposit.DepositScreen
import com.example.gro.ui.screen.garden.GardenScreen
import com.example.gro.ui.screen.journal.JournalScreen
import com.example.gro.ui.screen.onboarding.OnboardingScreen
import com.example.gro.ui.screen.plantdetail.PlantDetailScreen
import com.example.gro.ui.screen.settings.SettingsScreen
import com.example.gro.ui.screen.splash.SplashScreen
import com.example.gro.ui.screen.visit.VisitGardenScreen
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

private val BOTTOM_NAV_ROUTES = setOf(
    Screen.Garden.route,
    Screen.Deposit.route,
    Screen.VisitGarden.route,
    Screen.Settings.route,
)

@Composable
fun GroNavGraph(activityResultSender: ActivityResultSender) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNav = currentRoute in BOTTOM_NAV_ROUTES

    val currentNavItem = when (currentRoute) {
        Screen.Garden.route -> BottomNavItem.Garden
        Screen.Deposit.route -> BottomNavItem.Deposit
        Screen.VisitGarden.route -> BottomNavItem.Social
        Screen.Settings.route -> BottomNavItem.Settings
        else -> BottomNavItem.Garden
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                GroBottomNavBar(
                    currentItem = currentNavItem,
                    onItemSelected = { item ->
                        val route = when (item) {
                            BottomNavItem.Garden -> Screen.Garden.route
                            BottomNavItem.Deposit -> Screen.Deposit.route
                            BottomNavItem.Social -> Screen.VisitGarden.route
                            BottomNavItem.Settings -> Screen.Settings.route
                        }
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(Screen.Garden.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = if (showBottomNav) Modifier.padding(bottom = innerPadding.calculateBottomPadding()) else Modifier,
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
                        navController.navigate(Screen.Deposit.route) {
                            popUpTo(Screen.Garden.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToPlantDetail = { plantId ->
                        navController.navigate(Screen.PlantDetail.createRoute(plantId))
                    },
                    onNavigateToVisit = {
                        navController.navigate(Screen.VisitGarden.route) {
                            popUpTo(Screen.Garden.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                    onNavigateBack = {
                        navController.navigate(Screen.Garden.route) {
                            popUpTo(Screen.Garden.route) { inclusive = true }
                        }
                    },
                    activityResultSender = activityResultSender,
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
                        navController.navigate(Screen.Garden.route) {
                            popUpTo(Screen.Garden.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onDisconnect = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Garden.route) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
