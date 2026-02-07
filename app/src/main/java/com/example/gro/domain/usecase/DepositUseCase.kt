package com.example.gro.domain.usecase

import com.example.gro.domain.model.PlantSpecies
import com.example.gro.domain.repository.DepositRepository
import com.example.gro.domain.repository.DepositResult
import com.example.gro.domain.repository.GardenRepository
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import javax.inject.Inject

class DepositUseCase @Inject constructor(
    private val depositRepository: DepositRepository,
    private val gardenRepository: GardenRepository,
) {
    suspend operator fun invoke(
        sender: ActivityResultSender,
        walletAddress: String,
        lamports: Long,
    ): DepositResult {
        val result = depositRepository.depositSol(sender, walletAddress, lamports)

        if (result is DepositResult.Success) {
            val solMint = PlantSpecies.SOL.tokenMint
            val existingPlant = gardenRepository.getPlantByMint(walletAddress, solMint)
            if (existingPlant != null) {
                gardenRepository.waterPlant(existingPlant.id, lamports)
            } else {
                gardenRepository.createPlant(walletAddress, solMint, lamports)
            }
        }

        return result
    }
}
