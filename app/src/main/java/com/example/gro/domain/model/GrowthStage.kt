package com.example.gro.domain.model

enum class GrowthStage(
    val minDays: Int,
    val minDeposits: Int,
    val displayName: String,
) {
    SEED(minDays = 0, minDeposits = 1, displayName = "Seed"),
    SPROUT(minDays = 3, minDeposits = 2, displayName = "Sprout"),
    SAPLING(minDays = 7, minDeposits = 4, displayName = "Sapling"),
    MATURE(minDays = 14, minDeposits = 7, displayName = "Mature"),
    BLOOMING(minDays = 30, minDeposits = 10, displayName = "Blooming");

    companion object {
        fun calculate(daysSincePlanted: Int, totalDeposits: Int): GrowthStage {
            return entries.reversed().firstOrNull { stage ->
                daysSincePlanted >= stage.minDays && totalDeposits >= stage.minDeposits
            } ?: SEED
        }
    }
}
