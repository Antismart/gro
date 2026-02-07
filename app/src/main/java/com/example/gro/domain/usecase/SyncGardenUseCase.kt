package com.example.gro.domain.usecase

import com.example.gro.domain.repository.GardenRepository
import javax.inject.Inject

class SyncGardenUseCase @Inject constructor(
    private val gardenRepository: GardenRepository,
) {
    suspend operator fun invoke(walletAddress: String) {
        gardenRepository.syncGardenWithChain(walletAddress)
    }
}
