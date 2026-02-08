package com.example.gro.domain.usecase

import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.model.PlantSpecies
import com.example.gro.domain.repository.DepositRepository
import com.example.gro.domain.repository.DepositResult
import com.example.gro.domain.repository.GardenRepository
import com.example.gro.domain.repository.JournalRepository
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import javax.inject.Inject

class DepositUseCase @Inject constructor(
    private val depositRepository: DepositRepository,
    private val gardenRepository: GardenRepository,
    private val recordActivityUseCase: RecordActivityUseCase,
    private val journalRepository: JournalRepository,
) {
    suspend operator fun invoke(
        sender: ActivityResultSender,
        walletAddress: String,
        lamports: Long,
        species: PlantSpecies = PlantSpecies.SOL,
    ): DepositResult {
        val result = depositRepository.depositSol(sender, walletAddress, lamports, species)

        if (result is DepositResult.Success) {
            val tokenMint = species.tokenMint
            val existingPlant = gardenRepository.getPlantByMint(walletAddress, tokenMint)
            if (existingPlant != null) {
                gardenRepository.waterPlant(existingPlant.id, lamports)
            } else {
                gardenRepository.createPlant(walletAddress, tokenMint, lamports)
            }
            recordActivityUseCase(walletAddress)

            val solAmount = lamports / 1_000_000_000.0
            journalRepository.logEntry(
                walletAddress = walletAddress,
                action = JournalAction.DEPOSIT,
                details = "Deposited ${"%.4f".format(solAmount)} ${species.displayName} to grow ${species.plantName}",
            )
        }

        return result
    }
}
