package com.example.gro.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.gro.data.local.db.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {

    @Query("SELECT * FROM streaks WHERE walletAddress = :walletAddress")
    suspend fun getStreak(walletAddress: String): StreakEntity?

    @Query("SELECT * FROM streaks WHERE walletAddress = :walletAddress")
    fun observeStreak(walletAddress: String): Flow<StreakEntity?>

    @Upsert
    suspend fun upsertStreak(streak: StreakEntity)
}
