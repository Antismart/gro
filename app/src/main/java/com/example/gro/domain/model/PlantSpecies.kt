package com.example.gro.domain.model

enum class Rarity { COMMON, UNCOMMON, RARE, LEGENDARY }

enum class PlantSpecies(
    val tokenMint: String,
    val displayName: String,
    val plantName: String,
    val description: String,
    val growthRate: Float,
    val rarity: Rarity,
) {
    SOL(
        tokenMint = "So11111111111111111111111111111111",
        displayName = "SOL",
        plantName = "Solana Fern",
        description = "A resilient fern that thrives in any condition. The backbone of every garden.",
        growthRate = 1.0f,
        rarity = Rarity.COMMON,
    ),
    USDC(
        tokenMint = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
        displayName = "USDC",
        plantName = "Stableleaf",
        description = "Steady and reliable. Grows slowly but never wilts.",
        growthRate = 0.5f,
        rarity = Rarity.COMMON,
    ),
    BONK(
        tokenMint = "DezXAZ8z7PnrnRJjz3wXBoRgixCa6xjnB7YaB1pPB263",
        displayName = "BONK",
        plantName = "Bonk Cactus",
        description = "A spiky little character. Small but full of personality.",
        growthRate = 1.5f,
        rarity = Rarity.UNCOMMON,
    ),
    JUP(
        tokenMint = "JUPyiwrYJFskUPiHa7hkeR8VUtAeFoSYbKedZNsDvCN",
        displayName = "JUP",
        plantName = "Jupiter Vine",
        description = "A climbing vine that reaches for the sky. Ambitious and far-reaching.",
        growthRate = 1.2f,
        rarity = Rarity.UNCOMMON,
    ),
    RAY(
        tokenMint = "4k3Dyjzvzp8eMZWUXbBCjEvwSkkk59S5iCNLY3QrkX6R",
        displayName = "RAY",
        plantName = "Radiant Sunflower",
        description = "Always faces the light. A garden showpiece.",
        growthRate = 1.1f,
        rarity = Rarity.RARE,
    ),
    ORCA(
        tokenMint = "orcaEKTdK7LKz57vaAYr9QeNsVEPfiu6QeMU1kektZE",
        displayName = "ORCA",
        plantName = "Ocean Lily",
        description = "A serene aquatic plant. Floats above the rest.",
        growthRate = 1.0f,
        rarity = Rarity.RARE,
    );

    companion object {
        private val mintMap = entries.associateBy { it.tokenMint }
        fun fromMint(mint: String): PlantSpecies? = mintMap[mint]
    }
}
