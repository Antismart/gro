package com.example.gro.data.repository

import com.example.gro.data.local.db.dao.JournalDao
import com.example.gro.data.local.db.entity.JournalEntryEntity
import com.example.gro.data.mapper.toDomain
import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.model.JournalEntry
import com.example.gro.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao,
) : JournalRepository {

    override fun observeEntries(walletAddress: String): Flow<List<JournalEntry>> {
        return journalDao.observeEntries(walletAddress).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun logEntry(
        walletAddress: String,
        action: JournalAction,
        details: String,
        gardenSnapshot: Int,
    ) {
        journalDao.insertEntry(
            JournalEntryEntity(
                walletAddress = walletAddress,
                timestamp = System.currentTimeMillis(),
                action = action.name,
                details = details,
                gardenSnapshot = gardenSnapshot,
            ),
        )
    }
}
