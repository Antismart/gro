package com.example.gro.domain.repository

import com.example.gro.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface GardenRepository {
    fun observeGarden(walletAddress: String): Flow<List<Plant>>
    suspend fun syncGardenWithChain(walletAddress: String)
    suspend fun getPlantByMint(walletAddress: String, tokenMint: String): Plant?
    suspend fun getPlantById(plantId: Long): Plant?
    suspend fun waterPlant(plantId: Long, depositAmountLamports: Long)
    suspend fun createPlant(walletAddress: String, tokenMint: String, depositAmountLamports: Long): Plant
}
