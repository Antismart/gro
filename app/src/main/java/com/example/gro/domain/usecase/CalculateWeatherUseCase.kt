package com.example.gro.domain.usecase

import com.example.gro.domain.model.GardenWeather
import com.example.gro.domain.model.GrowthStage
import com.example.gro.domain.model.Plant
import com.example.gro.domain.model.Streak
import javax.inject.Inject

class CalculateWeatherUseCase @Inject constructor() {

    operator fun invoke(streak: Streak?, plants: List<Plant>): GardenWeather {
        val hasBlooming = plants.any { it.growthStage == GrowthStage.BLOOMING }
        if (hasBlooming) return GardenWeather.GOLDEN_HOUR

        val currentStreak = streak?.currentStreak ?: 0
        return when {
            currentStreak >= 5 -> GardenWeather.SUNNY
            currentStreak >= 2 -> GardenWeather.PARTLY_CLOUDY
            currentStreak >= 1 -> GardenWeather.CLOUDY
            else -> GardenWeather.RAINY
        }
    }
}
