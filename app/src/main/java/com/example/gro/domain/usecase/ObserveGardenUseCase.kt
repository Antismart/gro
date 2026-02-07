package com.example.gro.domain.usecase

import com.example.gro.domain.model.Plant
import com.example.gro.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGardenUseCase @Inject constructor(
    private val gardenRepository: GardenRepository,
) {
    operator fun invoke(walletAddress: String): Flow<List<Plant>> {
        return gardenRepository.observeGarden(walletAddress)
    }
}
