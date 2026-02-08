package com.example.gro.domain.usecase

import com.example.gro.domain.repository.StreakRepository
import javax.inject.Inject

class RecordActivityUseCase @Inject constructor(
    private val streakRepository: StreakRepository,
) {
    suspend operator fun invoke(walletAddress: String) {
        streakRepository.recordActivity(walletAddress)
    }
}
