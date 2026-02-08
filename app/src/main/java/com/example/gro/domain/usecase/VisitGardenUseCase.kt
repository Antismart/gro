package com.example.gro.domain.usecase

import com.example.gro.data.remote.SolanaRpcClient
import com.example.gro.domain.model.GrowthStage
import com.example.gro.domain.model.Plant
import com.example.gro.domain.model.PlantSpecies
import javax.inject.Inject

class VisitGardenUseCase @Inject constructor(
    private val solanaRpcClient: SolanaRpcClient,
) {
    suspend operator fun invoke(friendAddress: String): List<Plant> {
        val plants = mutableListOf<Plant>()
        var gridIndex = 0

        // SOL balance as a plant
        val solBalance = solanaRpcClient.getBalance(friendAddress)
        if (solBalance > 0) {
            plants.add(
                createVisitPlant(
                    id = gridIndex.toLong(),
                    walletAddress = friendAddress,
                    species = PlantSpecies.SOL,
                    depositAmount = solBalance,
                    gridX = gridIndex % 4,
                    gridY = gridIndex / 4,
                ),
            )
            gridIndex++
        }

        // Token accounts
        val tokenAccounts = solanaRpcClient.getTokenAccounts(friendAddress)
        for (account in tokenAccounts) {
            val species = PlantSpecies.fromMint(account.mint) ?: continue
            plants.add(
                createVisitPlant(
                    id = gridIndex.toLong(),
                    walletAddress = friendAddress,
                    species = species,
                    depositAmount = account.amount,
                    gridX = gridIndex % 4,
                    gridY = gridIndex / 4,
                ),
            )
            gridIndex++
            if (gridIndex >= 12) break // 4x3 grid max
        }

        return plants
    }

    private fun createVisitPlant(
        id: Long,
        walletAddress: String,
        species: PlantSpecies,
        depositAmount: Long,
        gridX: Int,
        gridY: Int,
    ): Plant {
        // Estimate growth stage from deposit amount
        val stage = when {
            depositAmount > 10_000_000_000L -> GrowthStage.BLOOMING
            depositAmount > 1_000_000_000L -> GrowthStage.MATURE
            depositAmount > 100_000_000L -> GrowthStage.SAPLING
            depositAmount > 10_000_000L -> GrowthStage.SPROUT
            else -> GrowthStage.SEED
        }

        return Plant(
            id = id,
            walletAddress = walletAddress,
            tokenMint = species.tokenMint,
            species = species,
            growthStage = stage,
            healthScore = 80,
            growthPoints = 0f,
            plantedAt = System.currentTimeMillis(),
            lastWateredAt = System.currentTimeMillis(),
            totalDeposits = 1,
            totalDepositedAmount = depositAmount,
            gridPositionX = gridX,
            gridPositionY = gridY,
        )
    }
}
