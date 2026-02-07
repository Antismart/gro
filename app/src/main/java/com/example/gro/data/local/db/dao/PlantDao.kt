package com.example.gro.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gro.data.local.db.entity.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Query("SELECT * FROM plants WHERE walletAddress = :walletAddress ORDER BY gridPositionY, gridPositionX")
    fun observePlantsByWallet(walletAddress: String): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE walletAddress = :walletAddress")
    suspend fun getPlantsByWallet(walletAddress: String): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE walletAddress = :walletAddress AND tokenMint = :tokenMint LIMIT 1")
    suspend fun getPlantByMint(walletAddress: String, tokenMint: String): PlantEntity?

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Long): PlantEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Query(
        """UPDATE plants SET
        healthScore = :health,
        lastWateredAt = :wateredAt,
        totalDeposits = totalDeposits + 1,
        totalDepositedAmount = totalDepositedAmount + :amount,
        growthStage = :newStage
        WHERE id = :plantId"""
    )
    suspend fun waterPlant(plantId: Long, health: Int, wateredAt: Long, amount: Long, newStage: String)

    @Query("UPDATE plants SET growthStage = :stage, growthPoints = :points WHERE id = :plantId")
    suspend fun updateGrowth(plantId: Long, stage: String, points: Float)
}
