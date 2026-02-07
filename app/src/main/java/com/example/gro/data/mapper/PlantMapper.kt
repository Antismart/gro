package com.example.gro.data.mapper

import com.example.gro.data.local.db.entity.PlantEntity
import com.example.gro.domain.model.GrowthStage
import com.example.gro.domain.model.Plant
import com.example.gro.domain.model.PlantSpecies

fun PlantEntity.toDomain(): Plant = Plant(
    id = id,
    walletAddress = walletAddress,
    tokenMint = tokenMint,
    species = PlantSpecies.valueOf(species),
    growthStage = GrowthStage.valueOf(growthStage),
    healthScore = healthScore,
    growthPoints = growthPoints,
    plantedAt = plantedAt,
    lastWateredAt = lastWateredAt,
    totalDeposits = totalDeposits,
    totalDepositedAmount = totalDepositedAmount,
    gridPositionX = gridPositionX,
    gridPositionY = gridPositionY,
    isStaked = isStaked,
    stakedAmount = stakedAmount,
    earnedYield = earnedYield,
)

fun Plant.toEntity(): PlantEntity = PlantEntity(
    id = id,
    walletAddress = walletAddress,
    tokenMint = tokenMint,
    species = species.name,
    growthStage = growthStage.name,
    healthScore = healthScore,
    growthPoints = growthPoints,
    plantedAt = plantedAt,
    lastWateredAt = lastWateredAt,
    totalDeposits = totalDeposits,
    totalDepositedAmount = totalDepositedAmount,
    gridPositionX = gridPositionX,
    gridPositionY = gridPositionY,
    isStaked = isStaked,
    stakedAmount = stakedAmount,
    earnedYield = earnedYield,
)
