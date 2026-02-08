package com.example.gro.data.repository

import com.example.gro.data.local.db.dao.PlantDao
import com.example.gro.data.local.db.entity.PlantEntity
import com.example.gro.data.mapper.toDomain
import com.example.gro.domain.model.GrowthStage
import com.example.gro.domain.model.Plant
import com.example.gro.domain.model.PlantSpecies
import com.example.gro.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
) : GardenRepository {

    override fun observeGarden(walletAddress: String): Flow<List<Plant>> {
        return plantDao.observePlantsByWallet(walletAddress).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncGardenWithChain(walletAddress: String) {
        val plants = plantDao.getPlantsByWallet(walletAddress)
        val now = System.currentTimeMillis()

        for (plant in plants) {
            val hoursSinceWatered = (now - plant.lastWateredAt) / (1000 * 60 * 60)
            val newHealth = if (hoursSinceWatered <= 24) {
                100
            } else {
                (100 - ((hoursSinceWatered - 24) * 2).toInt()).coerceIn(0, 100)
            }

            val daysSincePlanted = ((now - plant.plantedAt) / (1000 * 60 * 60 * 24)).toInt()
            val newStage = GrowthStage.calculate(daysSincePlanted, plant.totalDeposits)

            if (newHealth != plant.healthScore || newStage.name != plant.growthStage) {
                plantDao.updatePlant(
                    plant.copy(
                        healthScore = newHealth,
                        growthStage = newStage.name,
                    )
                )
            }
        }
    }

    override suspend fun getPlantByMint(walletAddress: String, tokenMint: String): Plant? {
        return plantDao.getPlantByMint(walletAddress, tokenMint)?.toDomain()
    }

    override suspend fun getPlantById(plantId: Long): Plant? {
        return plantDao.getPlantById(plantId)?.toDomain()
    }

    override suspend fun waterPlant(plantId: Long, depositAmountLamports: Long) {
        val plant = plantDao.getPlantById(plantId) ?: return
        val now = System.currentTimeMillis()
        val daysSincePlanted = ((now - plant.plantedAt) / (1000 * 60 * 60 * 24)).toInt()
        val newDeposits = plant.totalDeposits + 1
        val newStage = GrowthStage.calculate(daysSincePlanted, newDeposits)
        plantDao.waterPlant(
            plantId = plantId,
            health = 100,
            wateredAt = now,
            amount = depositAmountLamports,
            newStage = newStage.name,
        )
    }

    override suspend fun createPlant(
        walletAddress: String,
        tokenMint: String,
        depositAmountLamports: Long,
    ): Plant {
        val species = PlantSpecies.fromMint(tokenMint) ?: PlantSpecies.SOL
        val position = findNextGridPosition(walletAddress)
        val now = System.currentTimeMillis()
        val entity = PlantEntity(
            walletAddress = walletAddress,
            tokenMint = tokenMint,
            species = species.name,
            growthStage = GrowthStage.SEED.name,
            healthScore = 100,
            growthPoints = 0f,
            plantedAt = now,
            lastWateredAt = now,
            totalDeposits = 1,
            totalDepositedAmount = depositAmountLamports,
            gridPositionX = position.first,
            gridPositionY = position.second,
            isStaked = false,
            stakedAmount = 0,
            earnedYield = 0,
        )
        val id = plantDao.insertPlant(entity)
        return entity.copy(id = id).toDomain()
    }

    private suspend fun findNextGridPosition(walletAddress: String): Pair<Int, Int> {
        val existing = plantDao.getPlantsByWallet(walletAddress)
        val occupied = existing.map { it.gridPositionX to it.gridPositionY }.toSet()
        for (y in 0 until 3) {
            for (x in 0 until 4) {
                if ((x to y) !in occupied) return x to y
            }
        }
        return 0 to 0
    }
}
