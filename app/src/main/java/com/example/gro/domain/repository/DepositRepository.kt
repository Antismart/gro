package com.example.gro.domain.repository

import com.example.gro.domain.model.PlantSpecies
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

sealed class DepositResult {
    data class Success(val signature: String) : DepositResult()
    data class Error(val message: String) : DepositResult()
}

interface DepositRepository {
    suspend fun depositSol(
        sender: ActivityResultSender,
        fromAddress: String,
        lamports: Long,
        species: PlantSpecies = PlantSpecies.SOL,
    ): DepositResult

    suspend fun sendSunflower(
        sender: ActivityResultSender,
        fromAddress: String,
        toAddress: String,
    ): DepositResult
}
