package com.example.gro.domain.model

data class Plant(
    val id: Long = 0,
    val walletAddress: String,
    val tokenMint: String,
    val species: PlantSpecies,
    val growthStage: GrowthStage,
    val healthScore: Int,
    val growthPoints: Float,
    val plantedAt: Long,
    val lastWateredAt: Long,
    val totalDeposits: Int,
    val totalDepositedAmount: Long,
    val gridPositionX: Int,
    val gridPositionY: Int,
    val isStaked: Boolean = false,
    val stakedAmount: Long = 0,
    val earnedYield: Long = 0,
) {
    val healthTier: HealthTier get() = HealthTier.fromScore(healthScore)
}

enum class HealthTier(val label: String) {
    VIBRANT("Vibrant"),
    SLIGHTLY_MUTED("Needs attention"),
    WILTING("Wilting"),
    DORMANT("Dormant");

    companion object {
        fun fromScore(score: Int): HealthTier = when {
            score >= 80 -> VIBRANT
            score >= 50 -> SLIGHTLY_MUTED
            score >= 20 -> WILTING
            else -> DORMANT
        }
    }
}
