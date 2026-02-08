package com.example.gro.domain.repository

import com.example.gro.domain.model.Streak
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    fun observeStreak(walletAddress: String): Flow<Streak?>
    suspend fun getStreak(walletAddress: String): Streak?
    suspend fun recordActivity(walletAddress: String)
    suspend fun getGrowthMultiplier(walletAddress: String): Float
}
