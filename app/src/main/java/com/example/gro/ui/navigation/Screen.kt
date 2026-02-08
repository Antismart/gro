package com.example.gro.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Garden : Screen("garden")
    data object Deposit : Screen("deposit")
    data object PlantDetail : Screen("plant_detail/{plantId}") {
        fun createRoute(plantId: Long) = "plant_detail/$plantId"
    }
    data object VisitGarden : Screen("visit_garden")
    data object Journal : Screen("journal")
    data object Settings : Screen("settings")
}
