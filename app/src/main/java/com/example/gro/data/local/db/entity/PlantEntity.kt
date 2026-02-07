package com.example.gro.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plants",
    indices = [
        Index(value = ["walletAddress", "tokenMint"], unique = true),
    ],
)
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val walletAddress: String,
    val tokenMint: String,
    val species: String,
    val growthStage: String,
    val healthScore: Int,
    val growthPoints: Float,
    val plantedAt: Long,
    val lastWateredAt: Long,
    val totalDeposits: Int,
    val totalDepositedAmount: Long,
    val gridPositionX: Int,
    val gridPositionY: Int,
    val isStaked: Boolean,
    val stakedAmount: Long,
    val earnedYield: Long,
)
